package tech.claudioed.register;

import lombok.Builder;
import lombok.Data;
import tech.claudioed.register.domain.Payment;

/**
 * @author claudioed on 2019-03-05.
 * Project register
 */
@Data
@Builder
public class OrderData {

  private final Payment payment;

  private final String crmId;

  public OrderData(Payment payment, String crmId) {
    this.payment = payment;
    this.crmId = crmId;
  }

}
