package tech.claudioed.register.domain.exception;

/**
 * @author claudioed on 2019-03-01.
 * Project register
 */
public class PaymentDenied extends RuntimeException {

  public PaymentDenied(String message) {
    super(message);
  }

}
