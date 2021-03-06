package tech.claudioed.register.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.claudioed.register.domain.Payment;

/**
 * @author claudioed on 2019-03-01.
 * Project register
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentDenied extends RuntimeException {

  private final Payment payment;

  public PaymentDenied(String message, Payment payment) {
    super(message);
    this.payment = payment;
  }

}
