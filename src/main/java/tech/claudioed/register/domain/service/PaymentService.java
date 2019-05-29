package tech.claudioed.register.domain.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.Counter;
import issuer.IssuerServiceGrpc;
import issuer.RequestPayment;
import issuer.Transaction;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.claudioed.register.OrderData;
import tech.claudioed.register.domain.Payment;
import tech.claudioed.register.domain.exception.PaymentDenied;
import tech.claudioed.register.domain.repository.PaymentRepository;
import tech.claudioed.register.domain.resource.data.PaymentRequest;
import tech.claudioed.register.domain.service.data.Card;
import tech.claudioed.register.domain.service.data.Issuer;

/** @author claudioed on 2019-03-01. Project register */
@Slf4j
@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  private final Counter paymentCounter;

  private final NotifyCrmPublisher notifyCrmPublisher;

  private final IssuerData issuerData;

  private final VaultService vaultService;

  public PaymentService(
      PaymentRepository paymentRepository,
      @Qualifier("paymentsCounter") Counter paymentCounter,
      NotifyCrmPublisher notifyCrmPublisher,IssuerData issuerData, VaultService vaultService) {
    this.paymentRepository = paymentRepository;
    this.paymentCounter = paymentCounter;
    this.notifyCrmPublisher = notifyCrmPublisher;
    this.issuerData = issuerData;
    this.vaultService = vaultService;
  }

  public Payment newPayment(@NonNull PaymentRequest request) {
    log.info("Registering payment {} ", request.toString());
    final Card card = this.vaultService.token(request.getToken());
    final Issuer issuer = this.issuerData.find(card.getIssuer());
    final ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(issuer.getUrl(), issuer.getPort())
        .usePlaintext().build();
    final RequestPayment purchase = RequestPayment.newBuilder().setToken(request.getToken())
        .setValue(request.getValue().doubleValue()).setType("PURCHASE").build();
    final Transaction transaction = IssuerServiceGrpc.newBlockingStub(managedChannel)
        .requestPayment(purchase);
    final Payment payment =
        Payment.builder()
            .id(UUID.randomUUID().toString())
            .requesterId(request.getRequesterId())
            .customerId(request.getCustomerId())
            .value(request.getValue())
            .status(transaction.getStatus())
            .orderId(request.getOrderId())
            .build();
    if ("APPROVED".equalsIgnoreCase(payment.getStatus())) {
      paymentCounter.increment();
      this.paymentRepository.save(payment);
    } else {
      this.paymentRepository.save(payment);
      throw new PaymentDenied("Payment Denied", payment);
    }
    this.notifyCrmPublisher.publish(
        OrderData.builder().payment(payment).crmId(request.getCrmId()).build());
    return payment;
  }
}
