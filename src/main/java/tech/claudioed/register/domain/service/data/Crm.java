package tech.claudioed.register.domain.service.data;

import lombok.Data;

/**
 * @author claudioed on 2019-05-21.
 * Project register
 */
@Data
public class Crm {

  private String id;

  private String url;

  private Integer port;

  public String crmSvcHttp(){
    return this.url + ":" + this.port + "/";
  }

}
