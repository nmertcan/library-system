package library.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Invoice {
    private final String id;
    private final String loanId;
    private final String memberId;
    private final BigDecimal amount;
    private final LocalDate issuedOn;

    public Invoice(String id, String loanId, String memberId, BigDecimal amount, LocalDate issuedOn) {
        this.id = Person.required(id, "Fatura ID");
        this.loanId = Person.required(loanId, "Ödünç ID");
        this.memberId = Person.required(memberId, "Üye ID");
        this.amount = Objects.requireNonNull(amount);
        if (amount.signum() < 0) throw new IllegalArgumentException("Fatura tutarı negatif olamaz.");
        this.issuedOn = Objects.requireNonNull(issuedOn);
    }
    public String id() { return id; }
    public String loanId() { return loanId; }
    public String memberId() { return memberId; }
    public BigDecimal amount() { return amount; }
    public LocalDate issuedOn() { return issuedOn; }
}
