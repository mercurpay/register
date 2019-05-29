package tech.claudioed.register.domain.service;

import io.micrometer.core.instrument.Timer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.claudioed.register.domain.service.data.Card;
import vault.DataByToken;
import vault.Token;
import vault.VaultServiceGrpc;

@Service
public class VaultService {

  private final VaultServiceGrpc.VaultServiceBlockingStub vaultService;

  private final Tracer tracer;

  private final Timer vaultTimer;

  public VaultService(VaultServiceGrpc.VaultServiceBlockingStub vaultService, Tracer tracer,
                      @Qualifier("vaultTimer") Timer vaultTimer) {
    this.vaultService = vaultService;
    this.tracer = tracer;
    this.vaultTimer = vaultTimer;
  }

  public Card token(@NonNull String token) {
    return this.vaultTimer.record(() ->{
      final Token tokenRequest = Token.newBuilder().setValue(token).build();
      Span vaultSpan = tracer
              .buildSpan("vault").asChildOf(tracer.activeSpan())
              .withTag("token", token)
              .start();
      try (val scope = tracer.scopeManager().activate(vaultSpan, true)) {
        final DataByToken data = this.vaultService.fromToken(tokenRequest);
        vaultSpan.setTag("customer",data.getCustomerId()).setTag("issuer",data.getIssuer());
        vaultSpan.finish();
        return Card.builder()
                .card(data.getCard())
                .customer(data.getCustomerId())
                .issuer(data.getIssuer())
                .build();
      }
    });
  }

}
