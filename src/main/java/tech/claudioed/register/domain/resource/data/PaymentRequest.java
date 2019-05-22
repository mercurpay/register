package tech.claudioed.register.domain.resource.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author claudioed on 2019-03-01.
 * Project register
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {

  private BigDecimal value;

  private String requesterId;

  private String customerId;

  private String token;

}
