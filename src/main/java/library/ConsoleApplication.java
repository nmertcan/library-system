package library;

import library.domain.*;
import library.service.Library;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public final class ConsoleApplication {
    private final Library library;
    private final Scanner input;

    public ConsoleApplication(Library library, Scanner input) {
        this.library = library;
        this.input = input;
    }
    public static void main(String[] args) { new ConsoleApplication(new Library(), new Scanner(System.in)).run(); }

    public void run() {
        System.out.println("Kütüphane sistemine hoş geldiniz. Veriler uygulama açıkken bellekte tutulur.");
        while (true) {
            printMenu();
            String choice = prompt("Seçim");
            if (choice == null || choice.equals("0")) return;
            try { execute(choice); }
            catch (RuntimeException e) { System.out.println("Hata: " + e.getMessage()); }
        }
    }
    private void printMenu() {
        System.out.println("""

                ===== KÜTÜPHANE MENÜSÜ =====
                Kitap işlemleri
                 1  Kitap ekle
                 2  Kitap güncelle
                 3  Kitap sil
                 4  ID ile bul
                 5  Ada göre ara
                 6  Yazara göre ara/listele
                 7  Kategoriye göre listele
                 8  Tüm kitaplar

                Üye ve ödünç işlemleri
                 9  Üye ekle
                10  Ödünç ver
                11  İade al
                12  Ödünç/fatura geçmişi

                 0  Çıkış
                ===========================
                """);
    }
    private void execute(String choice) {
        switch (choice) {
            case "1" -> {
                String id = ask("Kitap ID");
                String title = ask("Ad");
                Author author = author();
                Category category = category();
                BigDecimal deposit = money();
                library.addBook(new Book(id, title, author, category, deposit));
                System.out.println("Kitap eklendi.");
            }
            case "2" -> {
                String id = ask("Güncellenecek kitap ID");
                library.updateBook(id, ask("Yeni ad"), author(), category(), money());
                System.out.println("Kitap güncellendi.");
            }
            case "3" -> { library.removeBook(ask("Silinecek kitap ID")); System.out.println("Kitap silindi."); }
            case "4" -> System.out.println(library.findBook(ask("Kitap ID")));
            case "5" -> printBooks(library.searchTitle(ask("Ad veya parçası")));
            case "6" -> printBooks(library.listAuthor(ask("Yazar adı veya parçası")));
            case "7" -> printBooks(library.listCategory(category()));
            case "8" -> printBooks(library.allBooks());
            case "9" -> { library.addMember(new Member(ask("Üye ID"), ask("Üye adı"))); System.out.println("Üye eklendi."); }
            case "10" -> {
                Loan loan = library.borrow(ask("Kitap ID"), ask("Üye ID"));
                Invoice invoice = library.invoiceFor(loan.id());
                System.out.printf("Ödünç verildi. Ödünç ID: %s | Fatura: %s | Alınan: %s TL%n", loan.id(), invoice.id(), invoice.amount());
            }
            case "11" -> {
                Payment refund = library.giveBack(ask("Kitap ID"), ask("Üye ID"));
                System.out.printf("İade alındı. %s TL geri ödendi. Fatura: %s%n", refund.amount(), refund.invoiceId());
            }
            case "12" -> {
                for (Loan loan : library.loanHistory()) {
                    Invoice invoice = library.invoiceFor(loan.id());
                    System.out.printf("%s | %s | %s | %s | %s | fatura %s TL%n", loan.id(), loan.book().title(), loan.member().name(), loan.borrowedOn(), loan.active() ? "aktif" : "iade " + loan.returnedOn(), invoice.amount());
                }
            }
            default -> System.out.println("Geçersiz seçim.");
        }
    }
    private String prompt(String label) {
        System.out.print(label + ": ");
        return input.hasNextLine() ? input.nextLine().trim() : null;
    }
    private String ask(String label) {
        String value = prompt(label);
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " boş olamaz.");
        return value;
    }
    private Author author() { return new Author(ask("Yazar ID"), ask("Yazar adı")); }
    private Category category() { return Category.valueOf(ask("Kategori (ROMAN/BILIM/TARIH/COCUK/DIGER)").toUpperCase(java.util.Locale.ROOT)); }
    private BigDecimal money() { return new BigDecimal(ask("Depozito (TL, örn. 20.00)")); }
    private void printBooks(List<Book> books) {
        if (books.isEmpty()) System.out.println("Kitap bulunamadı.");
        books.forEach(book -> System.out.println(book + (library.activeLoan(book.id()).isPresent() ? " | ÖDÜNÇTE" : " | MÜSAİT")));
    }
}
