package tech.claudioed.register.domain.repository;

    import org.springframework.data.repository.CrudRepository;
    import tech.claudioed.register.domain.Payment;

/** @author claudioed on 2019-03-01. Project register */
public interface PaymentRepository extends CrudRepository<Payment, String> {}
