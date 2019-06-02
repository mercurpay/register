package tech.claudioed.register.domain.service.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.claudioed.register.infra.card.CardMask;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

  private String card;

  private String customer;

  private String issuer;

  public String cardData(){
    return CardMask.maskCreditCard(this.card);
  }

}
