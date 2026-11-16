package com.nmertcan.library.service;

import com.nmertcan.library.domain.*;
import com.nmertcan.library.exception.LibraryException;
import com.nmertcan.library.repository.*;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

public final class LoanService {
    private final BookCatalog catalog;
    private final MemberRegistry members;
    private final BillingService billing;
    private final Clock clock;
    private final Map<String, Loan> activeByBook = new HashMap<>();
    private final List<Loan> history = new ArrayList<>();

    public LoanService(BookCatalog catalog, MemberRegistry members, BillingService billing, Clock clock) {
        this.catalog = Objects.requireNonNull(catalog);
        this.members = Objects.requireNonNull(members);
        this.billing = Objects.requireNonNull(billing);
        this.clock = Objects.requireNonNull(clock);
    }
    public Loan borrow(String bookId, String memberId) {
        Book book = catalog.get(bookId);
        Member member = members.get(memberId);
        if (activeByBook.containsKey(bookId)) throw new LibraryException("Kitap başka bir üyede: " + bookId);
        if (!member.canBorrow()) throw new LibraryException("Üye 5 kitap sınırına ulaştı.");
        Loan loan = new Loan(UUID.randomUUID().toString(), book, member, LocalDate.now(clock));
        billing.charge(loan, LocalDate.now(clock));
        member.addLoan(bookId);
        activeByBook.put(bookId, loan);
        history.add(loan);
        return loan;
    }
    public Payment giveBack(String bookId, String memberId) {
        Loan loan = activeByBook.get(bookId);
        if (loan == null) throw new LibraryException("Kitap ödünçte değil: " + bookId);
        if (!loan.member().id().equals(memberId)) throw new LibraryException("Kitap bu üyede değil.");
        LocalDate today = LocalDate.now(clock);
        Payment refund = billing.refund(loan, today);
        loan.close(today);
        loan.member().removeLoan(bookId);
        activeByBook.remove(bookId);
        return refund;
    }
    public boolean isBorrowed(String bookId) { return activeByBook.containsKey(bookId); }
    public Optional<Loan> activeLoan(String bookId) { return Optional.ofNullable(activeByBook.get(bookId)); }
    public List<Loan> history() { return List.copyOf(history); }
}
