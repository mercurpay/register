package tech.claudioed.register.domain.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.Counter;
import issuer.IssuerServiceGrpc;
import issuer.RequestPayment;
import issuer.Transaction;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.claudioed.register.domain.Payment;
import tech.claudioed.register.domain.exception.PaymentDenied;
import tech.claudioed.register.domain.repository.PaymentRepository;
import tech.claudioed.register.domain.resource.data.PaymentRequest;
import tech.claudioed.register.domain.service.data.Card;
import tech.claudioed.register.domain.service.data.Issuer;

/**
 * @author claudioed on 2019-03-01.
 * Project register
 */
@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  private final String operationStatus;

  private final Counter paymentCounter;

  private final IssuerData issuerData;

  private final VaultService vaultService;

  public PaymentService(PaymentRepository paymentRepository,
      @Value("${register.operation}") String operationStatus,
      @Qualifier("paymentsCounter") Counter paymentCounter,
      IssuerData issuerData, VaultService vaultService) {
    this.paymentRepository = paymentRepository;
    this.operationStatus = operationStatus;
    this.paymentCounter = paymentCounter;
    this.issuerData = issuerData;
    this.vaultService = vaultService;
  }

  public Payment newPayment(@NonNull PaymentRequest request){
    final Card card = this.vaultService.token(request.getToken());
    final Issuer issuer = this.issuerData.find(card.getIssuer());
    final ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(issuer.getUrl(), issuer.getPort())
        .usePlaintext().build();
    final RequestPayment purchase = RequestPayment.newBuilder().setToken(request.getToken())
        .setValue(request.getValue().doubleValue()).setType("PURCHASE").build();
    final Transaction transaction = IssuerServiceGrpc.newBlockingStub(managedChannel)
        .requestPayment(purchase);
    final Payment payment = Payment.builder().id(UUID.randomUUID().toString())
        .requesterId(request.getRequesterId()).customerId(request.getCustomerId())
        .value(request.getValue()).status(transaction.getStatus()).build();
    if("APPROVED".equalsIgnoreCase(payment.getStatus())){
      paymentCounter.increment();
      this.paymentRepository.save(payment);
    }else{
      this.paymentRepository.save(payment);
      throw new PaymentDenied("Payment Denied", payment);
    }
    return payment;
  }

}
