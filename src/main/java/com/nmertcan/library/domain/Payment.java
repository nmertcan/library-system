package com.nmertcan.library.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Payment {
    public enum Type { CHARGE, REFUND }
    private final String invoiceId;
    private final Type type;
    private final BigDecimal amount;
    private final LocalDate date;

    public Payment(String invoiceId, Type type, BigDecimal amount, LocalDate date) {
        this.invoiceId = Person.required(invoiceId, "Fatura ID");
        this.type = Objects.requireNonNull(type);
        this.amount = Objects.requireNonNull(amount);
        if (amount.signum() < 0) throw new IllegalArgumentException("Tutar negatif olamaz.");
        this.date = Objects.requireNonNull(date);
    }
    public String invoiceId() { return invoiceId; }
    public Type type() { return type; }
    public BigDecimal amount() { return amount; }
    public LocalDate date() { return date; }
}
