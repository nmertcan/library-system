package com.nmertcan.library.service;

import com.nmertcan.library.domain.Book;
import java.math.BigDecimal;

public interface FeePolicy {
    BigDecimal depositFor(Book book);
}
