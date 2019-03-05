package tech.claudioed.register.domain.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;
import tech.claudioed.register.OrderData;

/**
 * @author claudioed on 2019-03-05.
 * Project register
 */
@Data
public class NotifyPaymentEvent extends ApplicationEvent {

  private final OrderData orderData;

  public NotifyPaymentEvent(Object source, OrderData orderData) {
    super(source);
    this.orderData = orderData;
  }

}
