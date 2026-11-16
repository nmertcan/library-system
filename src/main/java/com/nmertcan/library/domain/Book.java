package com.nmertcan.library.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Book {
    private final String id;
    private final String title;
    private final Author author;
    private final Category category;
    private final BigDecimal deposit;

    public Book(String id, String title, Author author, Category category, BigDecimal deposit) {
        this.id = Person.required(id, "Kitap ID");
        this.title = Person.required(title, "Kitap adı");
        this.author = Objects.requireNonNull(author, "Yazar");
        this.category = Objects.requireNonNull(category, "Kategori");
        Objects.requireNonNull(deposit, "Ücret");
        if (deposit.signum() < 0) throw new IllegalArgumentException("Ücret negatif olamaz.");
        this.deposit = deposit.setScale(2, java.math.RoundingMode.UNNECESSARY);
    }
    public String id() { return id; }
    public String title() { return title; }
    public Author author() { return author; }
    public Category category() { return category; }
    public BigDecimal deposit() { return deposit; }
    @Override public String toString() {
        return id + " | " + title + " | " + author.name() + " | " + category + " | depozito: " + deposit + " TL";
    }
}
