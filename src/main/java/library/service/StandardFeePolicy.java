package library.service;

import library.domain.Book;
import java.math.BigDecimal;

public final class StandardFeePolicy implements FeePolicy {
    @Override public BigDecimal depositFor(Book book) { return book.deposit(); }
}
