package tech.claudioed.register.domain.service.data;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author claudioed on 2019-03-05.
 * Project crm
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

  private String type;

  private Map<String,Object> data;

}
