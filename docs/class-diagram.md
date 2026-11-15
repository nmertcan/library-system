# Sınıf diyagramı

```mermaid
classDiagram
    class Person { <<abstract>>
      -String id
      -String name
    }
    class Author
    class Member { -Set~String~ activeBookIds
      +canBorrow() boolean
    }
    class Book { -String id
      -String title
      -Author author
      -Category category
      -BigDecimal deposit
    }
    class Loan { -Book book
      -Member member
      -LocalDate borrowedOn
      -LocalDate returnedOn
    }
    class Invoice { -String loanId
      -BigDecimal amount
    }
    class Payment { -String invoiceId
      -Type type
      -BigDecimal amount
    }
    class BookCatalog { -Map~String,Book~ books }
    class MemberRegistry { -Map~String,Member~ members }
    class LoanService { -Map~String,Loan~ activeByBook
      -List~Loan~ history
    }
    class BillingService { -Map~String,Invoice~ invoicesByLoan
      -List~Payment~ payments
    }
    class FeePolicy { <<interface>>
      +depositFor(Book) BigDecimal
    }
    class StandardFeePolicy
    class PercentageFeePolicy
    class Library
    class ConsoleApplication
    class LibraryException
    Person <|-- Author
    Person <|-- Member
    Book --> Author
    Book --> Category
    Loan --> Book
    Loan --> Member
    Invoice --> Loan : loanId
    Payment --> Invoice : invoiceId
    BookCatalog o-- Book
    MemberRegistry o-- Member
    LoanService o-- Loan
    BillingService o-- Invoice
    BillingService o-- Payment
    BillingService --> FeePolicy
    FeePolicy <|.. StandardFeePolicy
    FeePolicy <|.. PercentageFeePolicy
    Library *-- BookCatalog
    Library *-- MemberRegistry
    Library *-- LoanService
    Library *-- BillingService
    ConsoleApplication --> Library
    BookCatalog ..> LibraryException
    LoanService ..> LibraryException
```

`*--` hizmetlerin `Library` tarafından oluşturulup sahiplenildiğini, `o--` kayıtların ilgili hizmetçe tutulduğunu gösterir. `FeePolicy` uygulaması `Library` oluşturulurken seçilir; `BillingService` onu aynı arayüz üzerinden çağırır. Tek kopyanın aktif ödünç bilgisi `LoanService.activeByBook` haritasındadır.
