package com.nmertcan.library.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Member extends Person {
    public static final int MAX_LOANS = 5;
    private final Set<String> activeBookIds = new LinkedHashSet<>();

    public Member(String id, String name) { super(id, name); }
    public Set<String> activeBookIds() { return Collections.unmodifiableSet(activeBookIds); }
    public boolean canBorrow() { return activeBookIds.size() < MAX_LOANS; }
    public void addLoan(String bookId) {
        if (!canBorrow()) throw new IllegalStateException("En fazla 5 kitap ödünç alınabilir.");
        if (!activeBookIds.add(bookId)) throw new IllegalStateException("Kitap zaten bu üyede.");
    }
    public void removeLoan(String bookId) {
        if (!activeBookIds.remove(bookId)) throw new IllegalStateException("Kitap bu üyede değil.");
    }
    @Override public String toString() { return id() + " - " + name(); }
}
