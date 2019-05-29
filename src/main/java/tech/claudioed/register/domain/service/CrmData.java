package tech.claudioed.register.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.register.domain.service.data.Crm;

/** @author claudioed on 2019-05-21. Project register */
@Slf4j
@Service
public class CrmData {

  private final String crmDataUrl;

  private final Integer crmDataPort;

  private final RestTemplate restTemplate;

  public CrmData(
      @Value("${crm.data.url}") String crmDataUrl,
      @Value("${crm.data.port}") Integer crmDataPort,
      RestTemplate restTemplate) {
    this.crmDataUrl = crmDataUrl;
    this.crmDataPort = crmDataPort;
    this.restTemplate = restTemplate;
  }

  public Crm find(String id){
    final String svcUrl = this.crmDataUrl + ":" + this.crmDataPort + "/api/crms/" + id;
    log.info("CRM Data SVC URL {}",svcUrl);
    final ResponseEntity<Crm> entity = this.restTemplate.getForEntity(svcUrl, Crm.class);
    return entity.getBody();
  }

}
