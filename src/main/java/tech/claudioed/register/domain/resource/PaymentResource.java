package tech.claudioed.register.domain.resource;

import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tech.claudioed.register.domain.Payment;
import tech.claudioed.register.domain.exception.PaymentDenied;
import tech.claudioed.register.domain.resource.data.PaymentRequest;
import tech.claudioed.register.domain.service.PaymentService;

/** @author claudioed on 2019-03-01. Project register */
@RestController
@RequestMapping("/api/payments")
public class PaymentResource {

  private final PaymentService paymentService;

  public PaymentResource(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  @Timed(value = "register.payment.time.seconds")
  public ResponseEntity<Payment> newPayment(
      @RequestBody PaymentRequest request, UriComponentsBuilder uriBuilder) {
    try {
      final Payment payment = this.paymentService.newPayment(request);
      final UriComponents uriComponents =
          uriBuilder.path("api/payments/{id}").buildAndExpand(payment.getId());
      return ResponseEntity.created(uriComponents.toUri()).body(payment);
    } catch (PaymentDenied ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getPayment());
    }
  }
}
