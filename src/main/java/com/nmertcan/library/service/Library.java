package com.nmertcan.library.service;

import com.nmertcan.library.domain.*;
import com.nmertcan.library.exception.LibraryException;
import com.nmertcan.library.repository.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.*;

public final class Library {
    private final BookCatalog catalog = new BookCatalog();
    private final MemberRegistry members = new MemberRegistry();
    private final BillingService billing;
    private final LoanService loans;

    public Library() { this(Clock.systemDefaultZone(), new StandardFeePolicy()); }
    public Library(Clock clock) { this(clock, new StandardFeePolicy()); }
    public Library(Clock clock, FeePolicy feePolicy) {
        billing = new BillingService(feePolicy);
        loans = new LoanService(catalog, members, billing, clock);
    }
    public void addBook(Book book) { catalog.add(book); }
    public void removeBook(String id) {
        catalog.get(id);
        if (loans.isBorrowed(id)) throw new LibraryException("Ödünçteki kitap silinemez.");
        catalog.remove(id);
    }
    public void updateBook(String id, String title, Author author, Category category, BigDecimal deposit) {
        catalog.get(id);
        if (loans.isBorrowed(id)) throw new LibraryException("Ödünçteki kitap güncellenemez.");
        catalog.update(id, title, author, category, deposit);
    }
    public Book findBook(String id) { return catalog.get(id); }
    public List<Book> allBooks() { return catalog.all(); }
    public List<Book> searchTitle(String text) { return catalog.byTitle(text); }
    public List<Book> searchAuthor(String text) { return catalog.byAuthor(text); }
    public List<Book> listCategory(Category category) { return catalog.byCategory(category); }
    public List<Book> listAuthor(String text) { return catalog.byAuthor(text); }
    public void addMember(Member member) { members.add(member); }
    public Member findMember(String id) { return members.get(id); }
    public List<Member> allMembers() { return members.all(); }
    public Loan borrow(String bookId, String memberId) { return loans.borrow(bookId, memberId); }
    public Payment giveBack(String bookId, String memberId) { return loans.giveBack(bookId, memberId); }
    public Optional<Loan> activeLoan(String bookId) { return loans.activeLoan(bookId); }
    public List<Loan> loanHistory() { return loans.history(); }
    public Invoice invoiceFor(String loanId) { return billing.invoiceFor(loanId); }
    public List<Invoice> invoices() { return billing.invoices(); }
    public List<Payment> payments() { return billing.payments(); }
    public BigDecimal balanceFor(String memberId) { return billing.balanceFor(memberId); }
}
