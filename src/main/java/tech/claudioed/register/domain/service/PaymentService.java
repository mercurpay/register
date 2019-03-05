package tech.claudioed.register.domain.service;

import io.micrometer.core.instrument.Counter;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.claudioed.register.domain.Payment;
import tech.claudioed.register.domain.exception.PaymentDenied;
import tech.claudioed.register.domain.repository.PaymentRepository;
import tech.claudioed.register.domain.resource.data.PaymentRequest;

/**
 * @author claudioed on 2019-03-01.
 * Project register
 */
@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  private final String operationStatus;

  private final Counter paymentCounter;

  public PaymentService(PaymentRepository paymentRepository,
      @Value("${register.operation}") String operationStatus,
      @Qualifier("paymentsCounter") Counter paymentCounter) {
    this.paymentRepository = paymentRepository;
    this.operationStatus = operationStatus;
    this.paymentCounter = paymentCounter;
  }

  public Payment newPayment(@NonNull PaymentRequest request){
    final Payment payment = Payment.builder().id(UUID.randomUUID().toString())
        .requesterId(request.getRequesterId()).customerId(request.getCustomerId())
        .value(request.getValue()).status(this.operationStatus).build();
    if("APPROVED".equals(this.operationStatus)){
      paymentCounter.increment();
      this.paymentRepository.save(payment);
    }else{
      this.paymentRepository.save(payment);
      throw new PaymentDenied("Payment Denied", payment);
    }
    return payment;
  }

}
