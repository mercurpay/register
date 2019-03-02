package tech.claudioed.register.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author claudioed on 2019-03-01.
 * Project register
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {

  @Id
  private String id;

  private BigDecimal value;

  private String requesterId;

  private String customerId;

  private String status;

}
