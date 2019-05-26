package tech.claudioed.register.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.register.domain.service.data.Issuer;

/** @author claudioed on 2019-05-21. Project register */
@Slf4j
@Service
public class IssuerData {

  private final String issuerDataUrl;

  private final Integer issuerDataPort;

  private final RestTemplate restTemplate;

  public IssuerData(
      @Value("${issuer.data.url}") String issuerDataUrl,
      @Value("${issuer.data.port}") Integer issuerDataPort,
      RestTemplate restTemplate) {
    this.issuerDataUrl = issuerDataUrl;
    this.issuerDataPort = issuerDataPort;
    this.restTemplate = restTemplate;
  }

  public Issuer find(String id){
    final String svcUrl = this.issuerDataUrl + ":" + this.issuerDataPort + "/api/issuers/" + id;
    log.info("Issuer Data SVC URL {}",svcUrl);
    final ResponseEntity<Issuer> entity = this.restTemplate.getForEntity(svcUrl, Issuer.class);
    return entity.getBody();
  }

}
