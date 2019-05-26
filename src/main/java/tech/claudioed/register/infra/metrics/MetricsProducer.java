package tech.claudioed.register.infra.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author claudioed on 2019-03-02.
 * Project register
 */
@Configuration
public class MetricsProducer {

  @Bean("paymentsCounter")
  public Counter paymentCounter(PrometheusMeterRegistry registry){
    return registry.counter("register.payment.total", "prod","business");
  }

  @Bean("registerTimer")
  public Timer registerTimer(PrometheusMeterRegistry registry){
    return registry.timer("register.payment", "type","infra");
  }

}
