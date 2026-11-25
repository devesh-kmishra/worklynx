package com.worklynx.backend.billing;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worklynx.backend.billing.enums.PaymentProvider;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByProviderAndProviderPaymentId(
      PaymentProvider provider,
      String providerPaymentId);

  List<Payment> findByOrganizationIdOrderByCreatedAtDesc(
      Long organizationId);

  Optional<Payment> findByProviderAndProviderOrderId(
      PaymentProvider provider,
      String providerOrderId);
}
