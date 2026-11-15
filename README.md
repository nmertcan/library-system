# Library System

Java 17 ve Maven ile yazılmış, bellek içi çalışan konsol kütüphane uygulaması. Nesne yönelimli tasarım ilkelerini ve temel kütüphane işlemlerini örnekler.

## Çalıştırma

Java 17+ ve Maven 3.8+ gerekir. Proje klasöründe:

```sh
mvn test
mvn compile exec:java
```

Program açılınca önce `9` ile üye, `1` ile kitap ekleyin. Ardından `10` ile ödünç verip `11` ile iade alın. `0` çıkış yapar. Kayıtlar bellekte tutulur; program kapanınca silinir.

## İş kuralları

- Her kitap ID'si tek fiziksel kopyayı temsil eder. Aktif ödünç varken ikinci kişiye verilemez.
- Her üyenin en fazla 5 aktif kitabı olabilir. İade, üyeye yeniden yer açar.
- Kitap ekleme, güncelleme ve silme; ID ile bulma; ada/yazara göre kısmi arama; kategori/yazara göre listeleme menüdedir.
- Ödünç verildiğinde kitap için belirtilen TL depozito tutarında fatura ve tahsilat kaydı oluşur. İadede aynı tutar tamamen geri ödenir. Aynı ödünç ikinci kez iade edilemez.
- Ödünçteki kitap silinemez veya değiştirilemez. Ödünç geçmişi ve fatura kayıtları korunur.
- Para `BigDecimal` ile iki ondalık hane olarak tutulur. Negatif veya ikiden fazla ondalıklı ücret kabul edilmez.

## Tasarım

`domain` sınıfları kitap, kişi, ödünç ve ödeme kavramlarını tanımlar. `repository` kayıtları `Map` içinde saklar. `service` iş kurallarını yürütür. `ConsoleApplication` yalnızca kullanıcı etkileşiminden sorumludur. `Person` soyut sınıfından `Author` ve `Member` türer. `Library`, katalog, üye kaydı, ödünç ve fatura hizmetlerini bir araya getirir (composition). `BillingService`, `FeePolicy` arayüzünü kullanır: `StandardFeePolicy` kitap depozitosunu, `PercentageFeePolicy` belirlenen oranını hesaplar. Böylece aynı ödünç akışı farklı politika nesneleriyle çalışır (polymorphism); varsayılan konsol davranışı standart depozitodur. `Member` aktif kitap ID'lerini `Set`, ödünç geçmişi ve ödemeler sıralı `List`, ID ile hızlı erişim gereken kayıtlar `Map` olarak tutar.

Sınıf ilişkileri: [docs/class-diagram.md](docs/class-diagram.md).

## Testler

`mvn test` ile JUnit 5 testleri çalışır. Testler CRUD/arama, doğrulama, tek kopya, üye sınırı, sahiplik, fatura/tam geri ödeme, yeniden ödünç verme ve ücret politikası polymorphism akışlarını kapsar.
