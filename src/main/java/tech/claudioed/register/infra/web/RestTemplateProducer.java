package tech.claudioed.register.infra.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author claudioed on 2019-03-05.
 * Project register
 */
@Configuration
public class RestTemplateProducer {

  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

}
