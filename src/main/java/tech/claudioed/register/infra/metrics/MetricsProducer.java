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

  @Bean("approvedPaymentsCounter")
  public Counter approvedPaymentCounter(PrometheusMeterRegistry registry){
    return registry.counter("register_payment", "prod","business","status","approved");
  }

  @Bean("deniedPaymentsCounter")
  public Counter deniedPaymentCounter(PrometheusMeterRegistry registry){
    return registry.counter("register_payment", "prod","business","status","denied");
  }

  @Bean("registerTimer")
  public Timer registerTimer(PrometheusMeterRegistry registry){
    return registry.timer("register_payment", "type","infra");
  }

  @Bean("vaultTimer")
  public Timer vaultTimer(PrometheusMeterRegistry registry){
    return registry.timer("vault", "type","infra","operation","detoken");
  }

  @Bean("crmTimer")
  public Timer crmTimer(PrometheusMeterRegistry registry){
    return registry.timer("crm", "type","infra","operation","notify-payment");
  }

}
