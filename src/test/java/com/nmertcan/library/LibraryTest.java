package com.nmertcan.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.nmertcan.library.domain.*;
import com.nmertcan.library.exception.LibraryException;
import com.nmertcan.library.service.Library;
import com.nmertcan.library.service.PercentageFeePolicy;
import com.nmertcan.library.service.StandardFeePolicy;
import java.math.BigDecimal;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    private Library library;
    private final Author author = new Author("a1", "Yaşar Kemal");

    @BeforeEach void setUp() {
        library = new Library(Clock.fixed(Instant.parse("2026-01-02T12:00:00Z"), ZoneOffset.UTC));
        library.addMember(new Member("u1", "Ayşe"));
        library.addMember(new Member("u2", "Ali"));
        addBook("b1", "İnce Memed", Category.ROMAN);
    }
    private void addBook(String id, String title, Category category) {
        library.addBook(new Book(id, title, author, category, new BigDecimal("20.00")));
    }
    @Test void crudAndSearch() {
        assertEquals("İnce Memed", library.findBook("b1").title());
        assertEquals(1, library.searchTitle("İNCE").size());
        assertEquals(1, library.searchAuthor("kemal").size());
        assertEquals(1, library.listCategory(Category.ROMAN).size());
        Book oldBook = library.findBook("b1");
        library.updateBook("b1", "Yeni Ad", author, Category.TARIH, new BigDecimal("30.00"));
        assertEquals("Yeni Ad", library.findBook("b1").title());
        assertEquals("İnce Memed", oldBook.title());
        assertTrue(library.listCategory(Category.ROMAN).isEmpty());
        library.removeBook("b1");
        assertThrows(LibraryException.class, () -> library.findBook("b1"));
    }
    @Test void uniqueIdsAndInvalidValues() {
        assertThrows(LibraryException.class, () -> addBook("b1", "Kopya", Category.ROMAN));
        assertThrows(IllegalArgumentException.class, () -> new Book("b2", " ", author, Category.ROMAN, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new Book("b2", "Ad", author, Category.ROMAN, new BigDecimal("-1.00")));
    }
    @Test void singleCopyAndOwnership() {
        library.borrow("b1", "u1");
        assertThrows(LibraryException.class, () -> library.borrow("b1", "u2"));
        assertThrows(LibraryException.class, () -> library.giveBack("b1", "u2"));
        assertThrows(LibraryException.class, () -> library.removeBook("b1"));
        assertThrows(LibraryException.class, () -> library.updateBook("b1", "Ad", author, Category.ROMAN, BigDecimal.ZERO));
        assertEquals(1, library.findMember("u1").activeBookIds().size());
    }
    @Test void fiveBookLimitAndReturnFreesSlot() {
        for (int i = 2; i <= 6; i++) addBook("b" + i, "Kitap " + i, Category.BILIM);
        for (int i = 1; i <= 5; i++) library.borrow("b" + i, "u1");
        assertThrows(LibraryException.class, () -> library.borrow("b6", "u1"));
        library.giveBack("b1", "u1");
        library.borrow("b6", "u1");
        assertEquals(5, library.findMember("u1").activeBookIds().size());
    }
    @Test void invoiceChargeAndFullRefund() {
        Loan loan = library.borrow("b1", "u1");
        Invoice invoice = library.invoiceFor(loan.id());
        assertEquals(new BigDecimal("20.00"), invoice.amount());
        assertEquals(new BigDecimal("20.00"), library.balanceFor("u1"));
        Payment refund = library.giveBack("b1", "u1");
        assertEquals(Payment.Type.REFUND, refund.type());
        assertEquals(invoice.id(), refund.invoiceId());
        assertEquals(new BigDecimal("0.00"), library.balanceFor("u1"));
        assertFalse(loan.active());
        assertEquals(LocalDate.of(2026, 1, 2), loan.returnedOn());
        assertThrows(LibraryException.class, () -> library.giveBack("b1", "u1"));
        assertEquals(2, library.payments().size());
    }
    @Test void returnEnablesNextBorrower() {
        library.borrow("b1", "u1");
        library.giveBack("b1", "u1");
        Loan next = library.borrow("b1", "u2");
        assertEquals("u2", next.member().id());
        assertEquals(2, library.loanHistory().size());
    }
    @Test void feePolicyPolymorphismKeepsInvoiceAndRefundConsistent() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-02T12:00:00Z"), ZoneOffset.UTC);
        Library standard = new Library(clock, new StandardFeePolicy());
        Library reduced = new Library(clock, new PercentageFeePolicy(new BigDecimal("0.50")));
        for (Library target : new Library[]{standard, reduced}) {
            target.addMember(new Member("u1", "Ayşe"));
            target.addBook(new Book("b1", "İnce Memed", author, Category.ROMAN, new BigDecimal("20.00")));
        }
        Loan standardLoan = standard.borrow("b1", "u1");
        Loan reducedLoan = reduced.borrow("b1", "u1");
        assertEquals(new BigDecimal("20.00"), standard.invoiceFor(standardLoan.id()).amount());
        assertEquals(new BigDecimal("10.00"), reduced.invoiceFor(reducedLoan.id()).amount());
        assertEquals(new BigDecimal("10.00"), reduced.giveBack("b1", "u1").amount());
        assertEquals(new BigDecimal("0.00"), reduced.balanceFor("u1"));
        assertThrows(IllegalArgumentException.class, () -> new PercentageFeePolicy(new BigDecimal("1.10")));
    }
}
