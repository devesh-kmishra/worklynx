package com.worklynx.backend.billing;

import org.springframework.stereotype.Service;

import com.worklynx.backend.billing.enums.BillingProduct;

@Service
public class BillingPricingService {

  public BillingPrice getPrice(BillingProduct product) {
    return BillingPrice.fromProduct(product);
  }
}
