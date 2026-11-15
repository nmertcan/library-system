package library.service;

import library.domain.*;
import library.exception.LibraryException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public final class BillingService {
    private final FeePolicy feePolicy;
    private final Map<String, Invoice> invoicesByLoan = new LinkedHashMap<>();
    private final List<Payment> payments = new ArrayList<>();

    public BillingService(FeePolicy feePolicy) { this.feePolicy = Objects.requireNonNull(feePolicy); }

    public Invoice charge(Loan loan, LocalDate date) {
        if (invoicesByLoan.containsKey(loan.id())) throw new LibraryException("Ödünç için fatura zaten var.");
        Invoice invoice = new Invoice("INV-" + loan.id(), loan.id(), loan.member().id(), feePolicy.depositFor(loan.book()), date);
        invoicesByLoan.put(loan.id(), invoice);
        payments.add(new Payment(invoice.id(), Payment.Type.CHARGE, invoice.amount(), date));
        return invoice;
    }
    public Payment refund(Loan loan, LocalDate date) {
        Invoice invoice = invoiceFor(loan.id());
        if (payments.stream().anyMatch(p -> p.invoiceId().equals(invoice.id()) && p.type() == Payment.Type.REFUND))
            throw new LibraryException("İade ödemesi zaten yapıldı.");
        Payment refund = new Payment(invoice.id(), Payment.Type.REFUND, invoice.amount(), date);
        payments.add(refund);
        return refund;
    }
    public Invoice invoiceFor(String loanId) {
        Invoice invoice = invoicesByLoan.get(loanId);
        if (invoice == null) throw new LibraryException("Fatura bulunamadı: " + loanId);
        return invoice;
    }
    public List<Invoice> invoices() { return List.copyOf(invoicesByLoan.values()); }
    public List<Payment> payments() { return List.copyOf(payments); }
    public BigDecimal balanceFor(String memberId) {
        Set<String> invoiceIds = new HashSet<>();
        invoicesByLoan.values().stream().filter(i -> i.memberId().equals(memberId)).forEach(i -> invoiceIds.add(i.id()));
        return payments.stream().filter(p -> invoiceIds.contains(p.invoiceId()))
                .map(p -> p.type() == Payment.Type.CHARGE ? p.amount() : p.amount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
