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

  private final String crmUrl;

  public OrderData(Payment payment, String crmUrl) {
    this.payment = payment;
    this.crmUrl = crmUrl;
  }


}
