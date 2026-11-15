# Test sonuçları

15 Kasım 2026, Europe/Istanbul.

Ortam: Java 25 JDK üzerinde `maven.compiler.release=17` ile derleme; Maven 3.9.9; JUnit 5.11.4.

`mvn test`: **BUILD SUCCESS**. 7 test çalıştı; 0 başarısız, 0 hata, 0 atlanan test. Ücret politikası polymorphism testi eklendikten sonra testler çevrimdışı Maven çalıştırmasıyla tekrar geçti.

Konsol denemesi: üye ekleme → kitap ekleme → ödünç verme → 20.00 TL fatura/tahsilat → iade → 20.00 TL geri ödeme akışı başarıyla tamamlandı.

README'deki `mvn compile exec:java` başlatma komutu da doğrulandı; menü açıldı ve `0` ile sorunsuz kapandı.

Testlenen konular: CRUD ve arama; yinelenen ID ve geçersiz veri; tek kopya/sahiplik; beş kitap sınırı; fatura ve tam geri ödeme; iade sonrası farklı üyeye ödünç verme; iki farklı ücret politikasının aynı arayüz üzerinden kullanılması.
