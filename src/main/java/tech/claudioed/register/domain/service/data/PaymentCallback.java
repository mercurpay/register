package tech.claudioed.register.domain.service.data;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author claudioed on 2019-03-05.
 * Project register
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCallback {

  private String paymentId;

  private String status;

  private String orderId;

  private String customerId;

  private BigDecimal value;

}
