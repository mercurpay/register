package tech.claudioed.register.domain.resource;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Timer;
import io.opentracing.Tracer;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tech.claudioed.register.domain.Payment;
import tech.claudioed.register.domain.exception.PaymentDenied;
import tech.claudioed.register.domain.resource.data.PaymentRequest;
import tech.claudioed.register.domain.service.PaymentService;

/** @author claudioed on 2019-03-01. Project register */
@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentResource {

  private final PaymentService paymentService;

  private final Tracer tracer;

  private final Timer timer;

  public PaymentResource(PaymentService paymentService,
      @Qualifier("registerTimer") Timer timer,Tracer tracer) {
    this.paymentService = paymentService;
    this.timer = timer;
    this.tracer = tracer;
  }

  @PostMapping
  @Timed(value = "register.payment.time.seconds")
  public ResponseEntity<Payment> newPayment(@RequestHeader(value = "Host",required = false)String host,
      @RequestBody PaymentRequest request, UriComponentsBuilder uriBuilder) {
    log.info("Request Host {}",host);
    return timer.record(() -> {
      try {
        final Payment payment = this.paymentService.newPayment(request);
        tracer.activeSpan().log(Collections.singletonMap("payment-id", payment.getId()));
      final UriComponents uriComponents =
          uriBuilder.path("api/payments/{id}").buildAndExpand(payment.getId());
      return ResponseEntity.created(uriComponents.toUri()).body(payment);
    } catch (PaymentDenied ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getPayment());
    }});
  }

}
