package com.worklynx.backend.billing;

import java.util.Currency;

import com.worklynx.backend.billing.enums.BillingInterval;
import com.worklynx.backend.billing.enums.BillingProduct;
import com.worklynx.backend.billing.enums.BillingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BillingPrice {

  PRO_MONTHLY(
      BillingProduct.PRO_MONTHLY,
      49900L,
      "INR",
      BillingType.RECURRING,
      BillingInterval.MONTHLY), PRO_YEARLY(
          BillingProduct.PRO_YEARLY,
          499000L,
          "INR",
          BillingType.RECURRING,
          BillingInterval.YEARLY), PRO_ONE_TIME(
              BillingProduct.PRO_ONE_TIME,
              49900L,
              "INR",
              BillingType.ONE_TIME,
              null);

  private final BillingProduct product;
  private final long amount;
  private final String currencyCode;
  private final BillingType billingType;
  private final BillingInterval billingInterval;

  public Currency getCurrency() {
    return Currency.getInstance(currencyCode);
  }

  public static BillingPrice fromProduct(BillingProduct product) {
    for (BillingPrice price : values()) {
      if (price.product == product) {
        return price;
      }
    }

    throw new IllegalArgumentException(
        "No pricing configuration found for product: " + product);
  }
}
