package com.nmertcan.library.service;

import com.nmertcan.library.domain.Book;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class PercentageFeePolicy implements FeePolicy {
    private final BigDecimal rate;

    public PercentageFeePolicy(BigDecimal rate) {
        this.rate = Objects.requireNonNull(rate, "Oran");
        if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("Oran 0 ile 1 arasında olmalı.");
    }
    @Override public BigDecimal depositFor(Book book) {
        return book.deposit().multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
