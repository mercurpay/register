package tech.claudioed.register.domain.resource;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Timer;
import java.util.Random;
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

  private final Timer timer;

  private final Random random = new Random();

  public PaymentResource(PaymentService paymentService,
      @Qualifier("registerTimer") Timer timer) {
    this.paymentService = paymentService;
    this.timer = timer;
  }

  @PostMapping
  @Timed(value = "register.payment.time.seconds")
  public ResponseEntity<Payment> newPayment(@RequestHeader(value = "Host",required = false)String host,
      @RequestBody PaymentRequest request, UriComponentsBuilder uriBuilder) {
    log.info("Request Host {}",host);
    return timer.record(() -> {
      try {
        log.info("Processing new register....");
        final int waitTime = (random.nextInt(10 - 1 + 1) + 1) * 1000;
        log.info("Time to wait {}",waitTime);
        Thread.sleep(waitTime);
        final Payment payment = this.paymentService.newPayment(request);
        final UriComponents uriComponents =
            uriBuilder.path("api/payments/{id}").buildAndExpand(payment.getId());
        log.info("Register processed successfully.");
        return ResponseEntity.created(uriComponents.toUri()).body(payment);
      } catch (PaymentDenied ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getPayment());
      } catch (InterruptedException e) {
        return ResponseEntity.unprocessableEntity().build();
      }
    });
  }

}
