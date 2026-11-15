package library.service;

import library.domain.Book;
import java.math.BigDecimal;

public interface FeePolicy {
    BigDecimal depositFor(Book book);
}
