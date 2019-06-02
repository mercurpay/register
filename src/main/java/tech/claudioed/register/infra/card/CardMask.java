package tech.claudioed.register.infra.card;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author claudioed on 2019-06-02.
 * Project register
 */
public class CardMask {

  /**
   * This will mask a portion of a credit card with 'x' if it finds it in a sentence.
   \b(?:

   # Visa card number 4[\ -]*(?:\d[\ -]*){11}(?:(?:\d[\ -]*){3})?\d|

   # MasterCard card number (?:5[\ -]*[1-5](?:[\ -]*\d){2}|(?:2[\
   -]*){3}[1-9]|(?:2[\ -]*){2}[3-9][\ -]*\d|2[\ -]* [3-6](?:[\ -]*\d){2}|2[\
   -]*7[\ -]*[01][\ -]*\d|2[\ -]*7[\ -]*2[\ -]*0)(?:[\ -]*\d){12}|

   # American Express card number 3[\ -]*[47](?:[\ -]*\d){13}|

   # Diners Club card number 3[\ -]*(?:0[\ -]*[0-5]|[68][\ -]*\d)(?:[\-]*\d){11}|

   # Discover card number 6[\ -]*(?:0[\ -]*1[\ -]*1|5[\ -]*\d[\ -]*\d)(?:[\-]*\d){12}|

   # JCB card number (?:2[\ -]*1[\ -]*3[\ -]*1|1[\ -]*8[\ -]*0[\ -]*0|3[\
   -]*5(?:[\ -]*\d){3})(?:[\ -]*\d){11}

   )\b

   * @return The masked credit card
   */
  public static String maskCreditCard(String sentenceThatMightContainACreditCard) {
    String maskedSentence = null;

    // Find the credit card. It will find the credit card whether it has
    // spaces, dashes, or no spaces/dashes.
    // It will find Visa, MasterCard, Amex, Diners, Discovery, and JCB.
    Pattern regex = Pattern.compile("\\b(?:4[ -]*(?:\\d[ -]*){11}(?:(?:\\d[ -]*){3})?\\d|"
        + "(?:5[ -]*[1-5](?:[ -]*\\d){2}|(?:2[ -]*){3}[1-9]|(?:2[ -]*){2}[3-9][ -]*"
        + "\\d|2[ -]*[3-6](?:[ -]*\\d){2}|2[ -]*7[ -]*[01][ -]*\\d|2[ -]*7[ -]*2[ -]*0)(?:[ -]*"
        + "\\d){12}|3[ -]*[47](?:[ -]*\\d){13}|3[ -]*(?:0[ -]*[0-5]|[68][ -]*\\d)(?:[ -]*"
        + "\\d){11}|6[ -]*(?:0[ -]*1[ -]*1|5[ -]*\\d[ -]*\\d)(?:[ -]*"
        + "\\d){12}|(?:2[ -]*1[ -]*3[ -]*1|1[ -]*8[ -]*0[ -]*0|3[ -]*5(?:[ -]*"
        + "\\d){3})(?:[ -]*\\d){11})\\b");

    Matcher regexMatcher = regex.matcher(sentenceThatMightContainACreditCard);

    if (regexMatcher.find()) {
      // Credit card has been found. e.g Here's an Amex: 3782-8224 6310005
      String creditCard = regexMatcher.group();

      // Strip out spaces and dashes (if any). e.g. 378282246310005
      String strippedCreditCard = creditCard.replaceAll("[ -]+", "");

      // Take a chunk of the creditcard starting at 7th position,
      // ending at the last 4. e.g. 24631
      String subSectionOfCreditCard = strippedCreditCard.substring(6, strippedCreditCard.length() - 4);

      //Get the first 6 chars of the stripped credit card
      String prefix = strippedCreditCard.substring(0, 6);

      //Replace the subsection of credit card with 'xxx'
      String middle = String.join("", Collections.nCopies(subSectionOfCreditCard.length(), "x"));

      //Get the last 4 chars of the stripped credit card
      String suffix = strippedCreditCard.substring(strippedCreditCard.length() - 4, strippedCreditCard.length());

      // Mask the sub section of the credit card with 'x'. e.g 378282xxxxx0005
      String maskedCreditCard = prefix + middle + suffix;

      // Take the original text with credit card, and replace the found credit
      // card with a masked credit card.
      // e.g. 'Market Lane Cafe $4.50 3782-8224 6310005 Large Latte'
      // is turned into 'Market Lane Coffee $4.50 378282xxxxx0005 Large Latte'
      maskedSentence = sentenceThatMightContainACreditCard.replace(creditCard, maskedCreditCard);
    } else {
      // If credit card was not found in the text, let's return the original input.
      maskedSentence = sentenceThatMightContainACreditCard;
    }

    return maskedSentence;
  }

}
