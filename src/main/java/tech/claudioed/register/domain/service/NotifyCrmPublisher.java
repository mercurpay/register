package tech.claudioed.register.domain.service;

import lombok.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tech.claudioed.register.OrderData;
import tech.claudioed.register.domain.event.NotifyPaymentEvent;

/**
 * @author claudioed on 2019-03-05.
 * Project register
 */
@Component
public class NotifyCrmPublisher {

  private final ApplicationEventPublisher publisher;

  public NotifyCrmPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void publish(@NonNull OrderData orderData){
    this.publisher.publishEvent(new NotifyPaymentEvent(this,orderData));
  }

}
