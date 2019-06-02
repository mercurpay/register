package tech.claudioed.register.domain.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.Counter;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import issuer.IssuerServiceGrpc;
import issuer.RequestPayment;
import issuer.Transaction;
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

import java.util.UUID;

/** @author claudioed on 2019-03-01. Project register */
@Slf4j
@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  private final NotifyCrmPublisher notifyCrmPublisher;

  private final IssuerData issuerData;

  private final VaultService vaultService;

  private final Counter deniedPaymentsCounter;

  private final Counter approvedPaymentCounter;

  private final Tracer tracer;

  public PaymentService(
      PaymentRepository paymentRepository,
      @Qualifier("deniedPaymentsCounter") Counter deniedPaymentsCounter,
      @Qualifier("approvedPaymentsCounter") Counter approvedPaymentCounter,
      NotifyCrmPublisher notifyCrmPublisher,
      IssuerData issuerData,
      VaultService vaultService,
      Tracer tracer) {
    this.paymentRepository = paymentRepository;
    this.deniedPaymentsCounter = deniedPaymentsCounter;
    this.approvedPaymentCounter = approvedPaymentCounter;
    this.notifyCrmPublisher = notifyCrmPublisher;
    this.issuerData = issuerData;
    this.vaultService = vaultService;
    this.tracer = tracer;
  }

  public Payment newPayment(@NonNull PaymentRequest request) {
    Span paymentSpan =
        tracer
            .buildSpan("process-payment")
            .asChildOf(this.tracer.activeSpan())
            .start()
            .setTag("order-id", request.getOrderId())
            .setTag("customer-id", request.getCustomerId())
            .setTag("requester-id", request.getRequesterId());
    try (Scope scope = tracer.scopeManager().activate(paymentSpan, false)) {
      log.info("Registering payment {} ", request.toString());
      Span decryptSpan = tracer.buildSpan("decrypt-token").asChildOf(paymentSpan).start();
      final Card card = this.vaultService.token(request.getToken());
      paymentSpan.setTag("card", card.cardData());
      decryptSpan.finish();
      Span issuerSpan = tracer.buildSpan("get-issuer-data").asChildOf(paymentSpan).start();
      final Issuer issuer = this.issuerData.find(card.getIssuer());
      final ManagedChannel managedChannel =
          ManagedChannelBuilder.forAddress(issuer.getUrl(), issuer.getPort())
              .usePlaintext()
              .build();
      issuerSpan.finish();
      final RequestPayment purchase =
          RequestPayment.newBuilder()
              .setToken(request.getToken())
              .setValue(request.getValue().doubleValue())
              .setType("PURCHASE")
              .build();
      final Transaction transaction =
          IssuerServiceGrpc.newBlockingStub(managedChannel).requestPayment(purchase);
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
        this.approvedPaymentCounter.increment();
        this.paymentRepository.save(payment);
      } else {
        this.deniedPaymentsCounter.increment();
        this.paymentRepository.save(payment);
        throw new PaymentDenied("Payment Denied", payment);
      }
      this.notifyCrmPublisher.publish(
          OrderData.builder().payment(payment).crmId(request.getCrmId()).build());
      paymentSpan.finish();
      return payment;
    }
  }
}
