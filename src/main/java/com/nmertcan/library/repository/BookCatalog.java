package com.nmertcan.library.repository;

import com.nmertcan.library.domain.*;
import com.nmertcan.library.exception.LibraryException;
import java.math.BigDecimal;
import java.util.*;

public final class BookCatalog {
    private final Map<String, Book> books = new LinkedHashMap<>();

    public void add(Book book) {
        Objects.requireNonNull(book);
        if (books.putIfAbsent(book.id(), book) != null) throw new LibraryException("Kitap ID zaten kayıtlı: " + book.id());
    }
    public Book get(String id) {
        Book book = books.get(id);
        if (book == null) throw new LibraryException("Kitap bulunamadı: " + id);
        return book;
    }
    public void remove(String id) { if (books.remove(id) == null) throw new LibraryException("Kitap bulunamadı: " + id); }
    public void update(String id, String title, Author author, Category category, BigDecimal deposit) {
        get(id);
        books.put(id, new Book(id, title, author, category, deposit));
    }
    public List<Book> all() { return List.copyOf(books.values()); }
    public List<Book> byTitle(String query) { return books.values().stream().filter(b -> contains(b.title(), query)).toList(); }
    public List<Book> byAuthor(String query) { return books.values().stream().filter(b -> contains(b.author().name(), query)).toList(); }
    public List<Book> byCategory(Category category) { return books.values().stream().filter(b -> b.category() == category).toList(); }
    private boolean contains(String value, String query) {
        if (query == null || query.isBlank()) throw new IllegalArgumentException("Arama sözcüğü boş olamaz.");
        return value.toLowerCase(Locale.ROOT).contains(query.trim().toLowerCase(Locale.ROOT));
    }
}
