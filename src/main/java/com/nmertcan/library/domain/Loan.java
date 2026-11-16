package com.nmertcan.library.domain;

import java.time.LocalDate;
import java.util.Objects;

public final class Loan {
    private final String id;
    private final Book book;
    private final Member member;
    private final LocalDate borrowedOn;
    private LocalDate returnedOn;

    public Loan(String id, Book book, Member member, LocalDate borrowedOn) {
        this.id = Person.required(id, "Ödünç ID");
        this.book = Objects.requireNonNull(book);
        this.member = Objects.requireNonNull(member);
        this.borrowedOn = Objects.requireNonNull(borrowedOn);
    }
    public String id() { return id; }
    public Book book() { return book; }
    public Member member() { return member; }
    public LocalDate borrowedOn() { return borrowedOn; }
    public LocalDate returnedOn() { return returnedOn; }
    public boolean active() { return returnedOn == null; }
    public void close(LocalDate date) {
        Objects.requireNonNull(date);
        if (!active()) throw new IllegalStateException("Ödünç kaydı zaten kapalı.");
        if (date.isBefore(borrowedOn)) throw new IllegalArgumentException("İade tarihi ödünç tarihinden önce olamaz.");
        returnedOn = date;
    }
}
