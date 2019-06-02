package tech.claudioed.register.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Map;

import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.register.domain.Payment;
import tech.claudioed.register.domain.event.NotifyPaymentEvent;
import tech.claudioed.register.domain.service.data.Crm;
import tech.claudioed.register.domain.service.data.EventRequest;
import tech.claudioed.register.domain.service.data.PaymentCallback;

/**
 * @author claudioed on 2019-03-05.
 * Project register
 */
@Slf4j
@Component
public class NotifyCrmListener implements ApplicationListener<NotifyPaymentEvent> {

  private final RestTemplate restTemplate;

  private final ObjectMapper objectMapper;

  private final CrmData crmData;

  private final Timer crmTimer;

  private final Tracer tracer;


  public NotifyCrmListener(RestTemplate restTemplate,
      ObjectMapper objectMapper, CrmData crmData,
      @Qualifier("crmTimer") Timer crmTimer, Tracer tracer) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.crmData = crmData;
    this.crmTimer = crmTimer;
    this.tracer = tracer;
  }

  @Override
  public void onApplicationEvent(NotifyPaymentEvent event) {
    log.info("Receiving request to notify crm {}",event.toString());
    final Payment payment = event.getOrderData().getPayment();
    final PaymentCallback paymentCallback = PaymentCallback.builder().customerId(payment.getCustomerId())
        .orderId(payment.getOrderId()).paymentId(payment.getId()).status(payment.getStatus())
        .value(payment.getValue()).build();
    final Map<String,Object> data = this.objectMapper.convertValue(paymentCallback, Map.class);
    final EventRequest eventRequest = EventRequest.builder().type(payment.getStatus()).data(data).build();
    Span findCrmSpan =
        tracer
            .buildSpan("find-crm-data")
            .asChildOf(this.tracer.activeSpan())
            .start()
            .setTag("crm-id", event.getOrderData().getCrmId());
    final Crm crm = this.crmData.find(event.getOrderData().getCrmId());
    findCrmSpan.finish();

    final String path = crm.crmSvcHttp() + "api/orders/{id}/events";
    log.info("Target url {} for crmId {}",path,crm.getId());
    this.crmTimer.record(() -> {
      Span notifyCrmSpan =
          tracer
              .buildSpan("notify-crm")
              .asChildOf(this.tracer.activeSpan())
              .start()
              .setTag("crm-id", event.getOrderData().getCrmId());
      this.restTemplate.postForEntity(path,eventRequest,String.class,payment.getOrderId());
      notifyCrmSpan.finish();
    });
  }

}
