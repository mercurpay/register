package tech.claudioed.register.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.claudioed.register.domain.service.data.Issuer;

/** @author claudioed on 2019-05-21. Project register */
@Service
public class IssuerData {

  private final String issuerDataUrl;

  private final Integer issuerDataPort;

  public IssuerData(
      @Value("${issuer.data.url}") String issuerDataUrl,
      @Value("${issuer.data.port}") Integer issuerDataPort) {
    this.issuerDataUrl = issuerDataUrl;
    this.issuerDataPort = issuerDataPort;
  }

  public Issuer find(String id){

    return null;
  }

}
