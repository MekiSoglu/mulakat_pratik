# Gün 6 – Mülakat Hazırlık Soruları ve Çözümleri

---

### 1. Java – Stream API & filter(), map(), collect() Kullanımı

**Soru:** Java – Stream API nedir? `filter()`, `map()`, `collect()` metotlarının ne işe yaradığını kısaca açıkla. Bir `List<Account>` içinden bakiyesi 1000'den büyük olanların isimlerini almak için nasıl bir stream zinciri yazarsın?

* **Cevap:**
  * **Stream API Nedir?:**
    * Java 8 ile hayatımıza giren, koleksiyonlar ve veri dizileri üzerinde fonksiyonel ve bildirimsel (declarative) veri işleme imkânı sunan bir akış soyutlamasıdır.
    * Veriyi kendi içinde depolamaz; bir veri kaynağından (Collection, Array, I/O kanalı) aldığı elemanları bir ardışık boru hattı (pipeline) boyunca işleyerek sonucu üretir.
    * Tembel (Lazy) değerlendirme ile çalışır; terminal operasyon çağrılana kadar ara işlemler yürütülmez.
  * **Temel Metotlar:**
    * **`filter(Predicate<T>)`:** Akıştaki her bir elemanı belirtilen mantıksal koşula (`Predicate` - true/false) göre test eder. Yalnızca koşulu sağlayan (`true`) elemanların akışta ilerlemesine izin verir, sağlamayanları eler (Intermediate operation).
    * **`map(Function<T, R>)`:** Akıştaki her bir elemanı alıp verilen fonksiyona göre dönüştürür (`1-to-1 transformation`). Elemanı başka bir türe veya özelliğe çevirir (Intermediate operation).
    * **`collect(Collector)`:** Akış boyunca işlenen elemanları toparlayıp bir koleksiyona (`List`, `Set`, `Map`) veya tekil bir sonuca dönüştüren sonlandırıcı (Terminal operation) işlemdir.
  * **Örnek Stream Zinciri:**

```java
List<String> highBalanceAccountNames = accounts.stream()
    .filter(account -> account.getBalance() > 1000)
    .map(Account::getName)
    .toList(); // Java 16+ (veya .collect(Collectors.toList()))
```

---

### 2. Java – Comparable ile Comparator Arasındaki Fark

**Soru:** Java – `Comparable` ile `Comparator` arasındaki fark nedir? Bir `Account` listesini bakiyeye göre sıralamak istersen hangisini kullanırsın?

* **Cevap:**
  * **`Comparable<T>`:**
    * `java.lang` paketinde yer alır.
    * Bir sınıfın "doğal sıralama" (natural ordering) kuralını belirler.
    * Sınıfın kaynak kodunu değiştirmeyi ve sınıf tanımına `implements Comparable<T>` eklemeyi gerektirir.
    * `compareTo(T o)` metodunu `@Override` ederek tek bir sıralama kuralı tanımlanabilir.
  * **`Comparator<T>`:**
    * `java.util` paketinde yer alır.
    * Sınıfın kaynak koduna dokunmadan, dışarıdan özel sıralama stratejileri (custom sorting) tanımlamak için kullanılır.
    * `compare(T o1, T o2)` metodunu kullanır; lambda veya metot referansları (`Comparator.comparing(...)`) ile çok pratik biçimde yazılabilir.
    * Farklı alanlara göre birden fazla sıralayıcı tanımlanabilir ve zincirlenebilir (`thenComparing`).
  * **Bakiye Sıralamasında Tercih:**
    * `Comparator` tercih edilir. Çünkü mevcut `Account` sınıfının kaynak koduna müdahale etmeden ve sınıfın doğal sıralamasını bozmadan, o anki akış içinde esnek ve okunabilir bir biçimde sıralama yapmayı sağlar.
  * **Örnek Kod:**

```java
List<Account> sortedAccounts = accounts.stream()
    .sorted(Comparator.comparingDouble(Account::getBalance))
    .toList();
```

---

### 3. SQL – DISTINCT ile GROUP BY Arasındaki Fark

**Soru:** SQL – `DISTINCT` ile `GROUP BY` arasındaki fark nedir? Aynı müşterinin birden fazla hesap türüne (`account_type`) sahip olup olmadığını bulmak için hangisini kullanırsın?

* **Cevap:**
  * **`DISTINCT`:**
    * `SELECT` ifadesinde belirtilen kolon kombinasyonuna göre dönen sonuç kümesindeki birebir aynı (yinelenen/duplicate) satırları eler ve satırların tekil olmasını sağlar.
    * Satırları gruplara ayırmaz veya üzerlerinde toplu özetleme (aggregation) fonksiyonları çalıştırmaz.
  * **`GROUP BY`:**
    * Belirtilen kolon(lar)ın ortak değerlerine göre satırları gruplara ayırır.
    * Her bir grup üzerinde toplama ve özetleme fonksiyonlarının (`COUNT`, `SUM`, `AVG`, `MIN`, `MAX`) çalıştırılmasını sağlar.
    * Gruplanmış sonuç kümesini filtrelemek için `HAVING` ifadesiyle birlikte kullanılır.
  * **Birden Fazla Hesap Türüne Sahip Müşterileri Bulma:**
    * Bu işlem için `GROUP BY` kullanılır. Çünkü müşterileri ID bazında gruplayıp, her bir grubun sahip olduğu farklı hesap türü sayısını hesaplamak gerekir.
    * Burada `COUNT(account_type)` yerine `COUNT(DISTINCT account_type)` kullanılması kritiktir; aksi takdirde aynı türden birden fazla hesabı olan (örneğin iki adet vadesiz hesabı olan) müşteriler yanlışlıkla seçilirdi.
  * **Örnek SQL Sorgusu:**

```sql
SELECT 
    customer_id, 
    COUNT(DISTINCT account_type) AS distinct_account_type_count
FROM ACCOUNT
GROUP BY customer_id
HAVING COUNT(DISTINCT account_type) > 1;
```

---

### 4. SQL – ON DELETE CASCADE, SET NULL, RESTRICT & Müşteri-Hesap İlişkisi

**Soru:** SQL – Foreign key tanımlarken `ON DELETE CASCADE`, `ON DELETE SET NULL`, `ON DELETE RESTRICT` seçenekleri ne anlama gelir? Bir CUSTOMER silindiğinde ACCOUNT tablosunda ne olmasını isteriz, neden?

* **Cevap:**
  * **Foreign Key Silme Kuralları:**
    * **`ON DELETE CASCADE`:** Üst (parent) tablodaki bir kayıt silindiğinde, ona referans veren alt (child) tablodaki tüm ilgili satırlar da veritabanı tarafından otomatik olarak fiziksel olarak silinir.
    * **`ON DELETE SET NULL`:** Üst tablodaki bir kayıt silindiğinde, alt tablodaki foreign key kolonunun değeri otomatik olarak `NULL` yapılır. (Alt tablodaki foreign key kolonunun `NULLABLE` olması gerekir).
    * **`ON DELETE RESTRICT` (veya `NO ACTION`):** Alt tabloda üst tabloya bağlı herhangi bir kayıt varsa, üst tablodaki satırın silinmesini engeller ve veritabanı seviyesinde hata fırlatır.
  * **Bankacılık Senaryosunda Müşteri - Hesap İlişkisi:**
    * Finansal ve yasal mevzuatlar (BDDK, MASAK, denetim standartları) gereği finansal kayıtlar, hesaplar ve parasal hareketler veritabanından **fiziksel olarak asla silinmez (hard delete yapılmaz)**.
    * Bu nedenle veritabanı seviyesinde **`ON DELETE RESTRICT`** kuralı tanımlanmalıdır. Böylece hesabı bulunan bir müşteri yanlışlıkla silinmek istendiğinde veritabanı işlemi doğrudan engeller ve veri bütünlüğü korunur.
    * Müşteri ilişkisi sonlandırılmak istendiğinde **Soft Delete** mekanizması uygulanır; müşteri ve hesapların statüsü `status = 'CLOSED'` / `is_deleted = true` olarak güncellenir ve tarihsel denetim izleri korunmaya devam eder.

---

### 5. Hibernate – CascadeType.REMOVE ile orphanRemoval = true Arasındaki Fark

**Soru:** Hibernate – `CascadeType.REMOVE` ile `orphanRemoval = true` arasındaki fark nedir? Bir Customer - Account ilişkisinde hangisini ne zaman kullanırsın?

* **Cevap:**
  * **`CascadeType.REMOVE`:**
    * Yalnızca ana nesne (parent) açıkça silindiğinde (`entityManager.remove(customer)`) devreye girer ve ilişkili alt nesneleri (`Account`) de veritabanından siler.
    * Ancak ana nesne silinmeyip yalnızca ana nesnenin koleksiyonundan bir eleman çıkarıldığında (`customer.getAccounts().remove(account)`), çocuk nesne veritabanından silinmez; yalnızca aralarındaki yabancı anahtar ilişkisi kopar ve veritabanında "yetim (orphan)" bir kayıt olarak kalmaya devam eder.
  * **`orphanRemoval = true`:**
    * `CascadeType.REMOVE`'un tüm yeteneklerini barındırmasının yanı sıra, parent nesne yaşamaya devam ederken **koleksiyondan bir çocuk nesne çıkarıldığında (`remove()` veya `clear()`)** yetim kalan bu nesneyi veritabanından da otomatik olarak siler (`DELETE` SQL'i üretir).
  * **Customer - Account İlişkisinde Kullanım:**
    * Bankacılıkta hesaplar kendi kimliği, bakiyesi ve finansal geçmişi olan bağımsız varlıklardır. Müşterinin hesap listesinden bir hesabı çıkarmak, o hesabın veritabanından kalıcı olarak silinmesi anlamına gelmemelidir.
    * Bu sebeple `orphanRemoval = true`, sipariş kalemleri (`OrderItem`) gibi yalnızca üst nesneyle var olabilen zayıf varlıklarda (Weak Entity) tercih edilir; hesap gibi bağımsız varlıklarda veri kaybı riskine karşı genellikle kullanılmaz.
    * Eğer müşteri silindiğinde hesaplarının da kaskat silinmesi hedeflenen bir tasarım kurgulanıyorsa `CascadeType.REMOVE` kullanılır; ancak finansal sistemlerde fiziksel silme yerine soft delete ve durum güncellemesi tercih edilmelidir.

---

### 6. Hibernate – @Transactional(readOnly = true) Performans Avantajları

**Soru:** Hibernate – `@Transactional(readOnly = true)` kullanmanın performans avantajı nedir? Hibernate bu durumda ne yapmayı atlar?

* **Cevap:**
  * **Dirty Checking (Kirli Kontrolü) Devre Dışı Bırakılır:**
    * Hibernate, Persistence Context (Session / First Level Cache) içine yüklediği entity'lerin snapshot kopyalarını tutmaz. Transaction commit anında entity alanlarının değişip değişmediğini kontrol eden "dirty check" döngüsünü çalıştırmayı atlar. Yoğun okuma işlemlerinde ciddi CPU ve bellek tasarrufu sağlar.
  * **Session Flush Atlanır:**
    * Hibernate `FlushMode.MANUAL` moduna geçer. Transaction sonunda gereksiz flush operasyonu yapılmaz, veritabanına boşuna UPDATE sorgusu gönderme kontrolleri yapılmaz.
  * **JDBC / Veritabanı Optimizasyonu:**
    * Alttaki JDBC bağlantısı üzerinde `connection.setReadOnly(true)` çağrılır. Veritabanı yönetim sistemi (RDBMS) kilit (lock) yönetimini, geri alma günlüklerini (undo/redo logs) ve transaction isolation maliyetlerini optimize eder.
  * **Read-Replica (Okuma Kopyası) Yönlendirmesi:**
    * Dağıtık mimarilerde yönlendirici veri kaynağı (Routing DataSource), `readOnly = true` ile işaretlenmiş işlemleri ana veritabanı (Primary/Master) yerine doğrudan salt-okunur kopyalara (Read Replica) yönlendirerek ana veritabanı yükünü azaltır.

---

### 7. Spring Boot – @WebMvcTest ile @SpringBootTest Arasındaki Fark

**Soru:** Spring Boot – `@WebMvcTest` ile `@SpringBootTest` arasındaki fark nedir? Sadece bir Controller'ı test etmek istersen hangisini tercih edersin, neden?

* **Cevap:**
  * **`@SpringBootTest`:**
    * Uygulamanın tüm Spring ApplicationContext'ini ayağa kaldırır.
    * Tanımlı tüm `@Component`, `@Service`, `@Repository` bean'lerini, konfigürasyonları ve veritabanı bağlantı havuzunu tam olarak yükler.
    * Uçtan uca (E2E) ve kapsamlı entegrasyon testleri için uygundur; ancak başlatma maliyeti yüksektir ve testler daha yavaş koşar.
  * **`@WebMvcTest`:**
    * Yalnızca web/sunum katmanını ayağa kaldıran bir dilim testidir (Slice Test).
    * Sadece belirtilen Controller sınıfını ve web ile ilgili bileşenleri (`@ControllerAdvice`, filtreler, converter'lar, Spring Security yapılandırması) yükler.
    * `@Service` ve `@Repository` gibi iş katmanı bileşenlerini ayağa kaldırmaz; bu bağımlılıkların `@MockBean` ile simüle edilmesi gerekir.
    * `MockMvc` ortamını otomatik yapılandırır, çok hafiftir ve saniyeler içinde koşar.
  * **Tercih ve Gerekçesi:**
    * Sadece bir Controller test edilecekse kesinlikle **`@WebMvcTest`** tercih edilir.
    * **Gerekçe:** Testler çok daha hızlı koşar. Gereksiz servis ve veritabanı bean'leri yüklenmez. Controller katmanının HTTP durum kodları, URL eşlemeleri, JSON serileştirme/deserileştirme doğruluğu ve istek doğrulama (validation) kuralları izole bir ortamda güvenle test edilir.

---

### 8. Spring Boot – HTTP Status Kodları & Bankacılık Eşleştirmeleri

**Soru:** Spring Boot – Bir REST API'de doğru HTTP status kodu kullanımı neden önemlidir? 200, 201, 204, 400, 404, 409 durumlarını birer örnekle (bankacılık senaryosuyla) eşleştir.

* **Cevap:**
  * **Doğru HTTP Durum Kodu Kullanımının Önemi:**
    * **İstemci Entegrasyonu ve Standartlaşma:** İstemciler (mobil, web, harici API tüketicileri) yanıt gövdesini string olarak parse etmek yerine standart HTTP kodlarına göre karar alır (örneğin 401'de login ekranına yönlendirme, 404'te bulunamadı mesajı).
    * **Ağ ve Gateway Yönetimi:** API Gateway, Reverse Proxy ve CDN sistemleri önbellekleme (caching), circuit breaker (devre kesici) ve retry (yeniden deneme) stratejilerini HTTP durum kodlarına göre yönetir.
    * **İzlenebilirlik ve Hata Analizi:** Log ve metrik izleme araçları (Prometheus, Grafana, ELK) 4xx (istemci kaynaklı) hatalar ile 5xx (sunucu çökmesi) hatalarını ayırt ederek sistem sağlığı hakkında doğru alarmlar üretir.
  * **Durum Kodları ve Bankacılık Senaryoları:**
    * **`200 OK`:** İstek başarıyla karşılandı ve beklenen veri yanıt gövdesinde döndürüldü.
      * *Örnek:* Bir müşterinin hesap detaylarının ve bakiyesinin listelenmesi (`GET /api/accounts/101`).
    * **`201 Created`:** İstek başarılı oldu ve sunucuda yeni bir kaynak oluşturuldu.
      * *Örnek:* Müşteri için yeni bir vadeli mevduat hesabı açılması (`POST /api/accounts`).
    * **`204 No Content`:** İstek başarıyla işlendi ancak istemciye geri dönecek bir yanıt gövdesi bulunmuyor.
      * *Örnek:* Tanımlı bir otomatik fatura ödeme talimatının sistemden silinmesi (`DELETE /api/standing-orders/55`).
    * **`400 Bad Request`:** İstemcinin gönderdiği istek geçersiz, eksik veya hatalı sözdizimine sahip.
      * *Örnek:* Para transferi isteğinde transfer tutarının negatif girilmesi veya IBAN formatının geçersiz olması (`POST /api/transfers`).
    * **`404 Not Found`:** İstenen kaynak sunucuda bulunamadı.
      * *Örnek:* Sistemde kayıtlı olmayan bir hesap ID'si ile bakiye sorgulaması yapılması (`GET /api/accounts/999999`).
    * **`409 Conflict`:** İstek, sunucunun mevcut durumuyla veya iş kurallarıyla çakışıyor.
      * *Örnek:* Aynı TC Kimlik Numarası ile zaten kayıtlı bir müşteri varken mükerrer müşteri kaydı açılmaya çalışılması veya aynı para transferi isteğinin aynı idempotency key ile mükerrer gönderilmesi / iyimser kilit (optimistic locking) çakışması.

---

### 9. Design Pattern – Factory Method Pattern & Kredi Hesaplama Senaryosu

**Soru:** Design Pattern – Factory Method pattern nedir? Bir kredi başvurusunda farklı kredi türlerine (konut, taşıt, ihtiyaç) göre farklı hesaplama nesneleri üretmen gerekseydi nasıl kullanırdın?

* **Cevap:**
  * **Factory Method Pattern Nedir?:**
    * Nesne oluşturma mantığını istemci (client) kodundan soyutlayarak alt sınıflara veya özel fabrika sınıflarına devreden yaratımsal (creational) bir tasarım desenidir.
    * İstemci `new` operatörüyle somut sınıflara sıkı sıkıya bağlanmak yerine ortak bir arayüz (interface) üzerinden nesne elde eder; böylece kod bağımlılıkları azalır (Loose Coupling) ve sisteme yeni sınıflar eklemek kolaylaşır (Open/Closed Principle).
  * **Kredi Başvurusu Senaryosunda Uygulama:**
    1. **Ortak Arayüz (`LoanCalculator`):** Tüm kredi türlerinin uygulaması gereken `calculate(LoanRequest request)` metodunu tanımlar.
    2. **Somut Sınıflar:** `HousingLoanCalculator` (Konut), `VehicleLoanCalculator` (Taşıt), `ConsumerLoanCalculator` (İhtiyaç) bu arayüzü implemente eder.
    3. **Spring Boot ile Fabrika Entegrasyonu:** Spring'in Dependency Injection desteğiyle tüm `LoanCalculator` implementasyonları bir `Map<LoanType, LoanCalculator>` yapısına enjekte edilir. Runtime anında gelen kredi türüne (`LoanType`) göre uygun hesaplama nesnesi seçilerek çalıştırılır.
  * **Örnek Kod Tasarımı:**

```java
// 1. Ortak Arayüz
public interface LoanCalculator {
    LoanType getType();
    CalculationResult calculate(LoanRequest request);
}

// 2. Somut Hesaplayıcılar
@Component
public class HousingLoanCalculator implements LoanCalculator {
    @Override public LoanType getType() { return LoanType.HOUSING; }
    @Override public CalculationResult calculate(LoanRequest request) {
        // Konut kredisi faiz, vergi (BSMV muafiyeti vb.) ve taksit hesabı
        return new CalculationResult(...);
    }
}

@Component
public class VehicleLoanCalculator implements LoanCalculator {
    @Override public LoanType getType() { return LoanType.VEHICLE; }
    @Override public CalculationResult calculate(LoanRequest request) {
        // Taşıt kredisi faiz ve taksit hesabı
        return new CalculationResult(...);
    }
}

// 3. Factory Sınıfı
@Component
public class LoanCalculatorFactory {
    private final Map<LoanType, LoanCalculator> calculatorMap;

    public LoanCalculatorFactory(List<LoanCalculator> calculators) {
        this.calculatorMap = calculators.stream()
            .collect(Collectors.toMap(LoanCalculator::getType, Function.identity()));
    }

    public LoanCalculator getCalculator(LoanType loanType) {
        LoanCalculator calculator = calculatorMap.get(loanType);
        if (calculator == null) {
            throw new IllegalArgumentException("Desteklenmeyen kredi türü: " + loanType);
        }
        return calculator;
    }
}
```

---

### 10. Kavram – Database Migration (Flyway / Liquibase)

**Soru:** Kavram – Database migration (Flyway/Liquibase) nedir, ne işe yarar? Bir projede şema değişikliklerini elle SQL scripti çalıştırmak yerine migration tool kullanmak neden önemlidir?

* **Cevap:**
  * **Database Migration Nedir?:**
    * Veritabanı şema yapısının (tablolar, kolonlar, kısıtlar, indeksler) ve başlangıç verilerinin kaynak kod gibi versiyonlanması, Git üzerinde izlenmesi ve ortamlara otomatik olarak uygulanması sürecidir.
  * **Flyway / Liquibase Nasıl Çalışır?:**
    * Veritabanında bir meta tablosu (Flyway için `flyway_schema_history`, Liquibase için `DATABASECHANGELOG`) oluşturarak çalıştırılan script'lerin adını, sırasını, yürütülme tarihini ve sağlama toplamını (checksum/hash) kaydeder.
    * Uygulama başlarken bu tabloyu kontrol eder; henüz çalıştırılmamış script'leri sıra numarasına göre çalıştırır. Daha önce çalıştırılmış bir script üzerinde sonradan değişiklik yapılmışsa checksum hatası vererek tutarlılığı güvenceye alır.
  * **Migration Tool Kullanmanın Önemi:**
    * **Ortamlar Arası Tutarlılık (Environment Parity):** Geliştirme (Local), Test, Kabul (UAT) ve Canlı (Production) ortamlarının tamamında veritabanı şemasının birebir aynı versiyonda olmasını garanti eder.
    * **CI/CD ve Dağıtım Otomasyonu:** Canlıya çıkışlarda geliştiricinin veya DBA'in elle SQL çalıştırma zorunluluğunu ortadan kaldırır; uygulama deployment sürecinde şema otomatik güncellenir.
    * **Denetim ve İzlenebilirlik (Audit):** Hangi şema değişikliğinin ne zaman, hangi sürümle ve kim tarafından uygulandığı şeffaf bir şekilde kayıt altına alınır.
    * **İnsan Hatasını Önleme:** Script'lerin unutulması, atlanması veya yanlış sırayla yürütülmesi risklerini tamamen engeller.

---

### 11. Senaryo 1 – Veritabanı Migration & Milyonlarca Kayıtta Kolon Ekleme

**Soru:** `Account` tablosuna yeni bir `account_status` (ACTIVE/FROZEN/CLOSED) kolonu eklemen isteniyor. Üretim ortamında zaten milyonlarca kayıt var. Bu değişikliği nasıl yönetirsin (Flyway/Liquibase kullanımı)? Var olan kayıtlar için bu kolonun varsayılan değeri ne olmalı, NULL bırakmak neden riskli olabilir?

* **Cevap:**
  * **Varsayılan Değer ve NULL Bırakmanın Riskleri:**
    * **Varsayılan Değer:** Mevcut hesapların operasyonel işleyişini aksatmamak adına varsayılan değer `'ACTIVE'` olarak belirlenmelidir.
    * **NULL Bırakmanın Riskleri:**
      * Java/uygulama tarafında `NullPointerException` (NPE) veya durum kontrollerinde (switch/case) beklenmeyen mantıksal açıklara yol açar.
      * SQL'de üç değerli mantık (Three-Valued Logic: True, False, Unknown) nedeniyle `WHERE account_status != 'CLOSED'` gibi filtrelemelerde NULL olan satırlar sonuç kümesine dahil edilmez; hesaplar görünmez hale gelebilir.
      * İndeks verimliliğini düşürür ve veri tutarlılığını bozar.
  * **Milyonlarca Kayıtlı Üretim Ortamında Değişiklik Yönetimi (Zero-Downtime Migration):**
    * Modern veritabanlarında (PostgreSQL 11+, MySQL 8.0.12+) `ADD COLUMN ... DEFAULT 'ACTIVE' NOT NULL` işlemi metadata seviyesinde anlık (instant DDL) çalışır ve tabloyu kitlemez.
    * Eski veritabanı sürümlerinde tablonun kilitlenmesini (exclusive table lock) önlemek için **Genişlet-Daralt (Expand/Contract)** deseni uygulanır:
      1. Kolon önce `NULL` kabul edecek ve varsayılan değeri olacak şekilde eklenir (tablo kilitlenmez).
      2. Eski kayıtlar arka planda küçük parçalar halinde (batch update, örn. 10.000'er satırlık chunk'lar) `account_status = 'ACTIVE'` olarak güncellenir.
      3. Tüm satırlar doldurulduktan sonra kolona `NOT NULL` kısıtı eklenir.
  * **Flyway ve Liquibase Örnekleri:**

```sql
-- Flyway: V2__add_account_status_to_account.sql
ALTER TABLE account
ADD COLUMN account_status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL;
```

```xml
<!-- Liquibase: changelog.xml -->
<changeSet id="20260915-01" author="backend-team">
    <addColumn tableName="account">
        <column name="account_status" type="VARCHAR(20)" defaultValue="ACTIVE">
            <constraints nullable="false"/>
        </column>
    </addColumn>
</changeSet>
```

---

### 12. Senaryo 2 – Unit Test Yazma (@Mock, @InjectMocks & Mockito)

**Soru:** `AccountService` içindeki `getBalance()` metodunu test etmen isteniyor. Bu metot `AccountRepository`'ye bağımlı. Bu bağımlılığı gerçek veritabanına bağlanmadan nasıl test edersin (Mockito ile mock'lama)? `@Mock` ve `@InjectMocks` annotation'ları ne işe yarar?

* **Cevap:**
  * **Gerçek Veritabanına Bağlanmadan Test Etme:**
    * Mockito kütüphanesi kullanılarak `AccountRepository` nesnesi taklit edilir (mock'lanır).
    * `when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount))` kalıbı ile veritabanına sorgu atılmadan repo metodunun döneceği değer programlanır.
  * **Anotasyonların İşlevleri:**
    * **`@Mock`:** Test edilecek sınıfın bağımlı olduğu bileşenin (burada `AccountRepository`) sahte (mock) bir kopyasını oluşturur. Gerçek metotları çalıştırmaz; davranışları test içerisinde `when(...).thenReturn(...)` ile tanımlanır.
    * **`@InjectMocks`:** Test edilen asıl sınıfın (`AccountService`) gerçek bir örneğini oluşturur ve `@Mock` ile işaretlenmiş olan sahte bağımlılıkları bu sınıfın içine (constructor, setter veya alan enjeksiyonu ile) otomatik olarak enjekte eder.
  * **Örnek Unit Test Kodu:**

```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldReturnBalanceWhenAccountExists() {
        // Given
        Long accountId = 1L;
        Account mockAccount = new Account(accountId, new BigDecimal("2500.00"));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        // When
        BigDecimal balance = accountService.getBalance(accountId);

        // Then
        assertEquals(new BigDecimal("2500.00"), balance);
        verify(accountRepository, times(1)).findById(accountId);
    }
}
```

---

### 13. Senaryo 3 – Bean Validation & Global Exception Handling

**Soru:** Bir client, `POST /api/accounts` isteğinde eksik/yanlış formatlı bir JSON gönderiyor (örn. `balance` alanı string olarak `"abc"` geliyor). Sistem şu anda kullanıcıya anlamsız bir `500 Internal Server Error` dönüyor. Bu durumu nasıl düzeltirsin? Bean Validation (`@Valid`, `@NotNull` vb.) ve merkezi exception handling'i nasıl devreye sokarsın?

* **Cevap:**
  * **DTO Seviyesinde Bean Validation:**
    * İstemciden gelen DTO sınıfındaki alanlar uygun kısıtlayıcı anotasyonlarla (`@NotNull`, `@DecimalMin`, `@NotBlank`) işaretlenir.
  * **Controller Seviyesinde `@Valid` Tetikleme:**
    * Controller metodunun `@RequestBody` parametresi önüne `@Valid` konularak istek geldiğinde doğrulama mekanizması devreye sokulur.
  * **Hata Ayrımı ve Merkezi Hata Yönetimi (`@RestControllerAdvice`):**
    * Sistemde iki farklı hata meydana gelebilir:
      1. *Validasyon Hatası (Kurala uymama):* Alan eksik veya eksi değer girilmişse `MethodArgumentNotValidException` fırlatılır.
      2. *JSON Serileştirme / Tip Uyuşmazlığı Hatası:* Sayısal alana string `"abc"` gönderilmişse henüz validation katmanına girmeden Jackson deserialization aşamasında `HttpMessageNotReadableException` fırlatılır.
    * `@RestControllerAdvice` sınıfı içinde her iki istisna da yakalanarak `500` hatası yerine anlamlı alan detayları içeren `400 Bad Request` yanıtı dönülür.
  * **Örnek Kod Yapısı:**

```java
// 1. DTO
public class CreateAccountRequest {
    @NotBlank(message = "Hesap adı zorunludur.")
    private String name;

    @NotNull(message = "Bakiye alanı boş bırakılamaz.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Bakiye sıfır veya pozitif olmalıdır.")
    private BigDecimal balance;
    // Getter - Setter
}

// 2. Controller
@PostMapping("/api/accounts")
public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
}

// 3. Global Exception Handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validasyon Hatası");
        body.put("details", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonFormatExceptions(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Geçersiz İstek Formatı");
        body.put("message", "Gönderilen veri formatı veya veri tipi geçersiz.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
```

---

### 14. Senaryo 4 – Loglama ve İzlenebilirlik (Correlation ID & MDC)

**Soru:** Prod ortamında bir hata oluşuyor ama loglar o kadar karışık ki hangi isteğin bu hataya sebep olduğunu bulamıyorsun. Her isteği ayırt edebilmek için ne gibi bir çözüm getirirsin? (İpucu: her isteğe bir request id / correlation id atayıp loglara eklemek.)

* **Cevap:**
  * **1. Correlation ID Tanımlama ve Yönetimi:**
    * İstek API Gateway'e veya servise ulaştığında bir HTTP Filter / Interceptor aracılığıyla `X-Correlation-ID` header'ı kontrol edilir.
    * Gelen istekte bu header mevcutsa korunur; yoksa servise giriş anında yeni bir benzersiz ID (`UUID.randomUUID().toString()`) üretilir.
  * **2. MDC (Mapped Diagnostic Context) ile Thread Bağlamına Ekleme:**
    * `MDC.put("correlationId", correlationId);` çağrısıyla bu ID değeri Logback/SLF4J'in ThreadLocal tabanlı hafızasına yazılır.
    * **Kritik Kural:** Web sunucusundaki thread havuzlarında (Thread Pool) thread'ler tekrar tekrar kullanıldığı için memory leak ve log karışmasını önlemek amacıyla `finally` bloğu içinde `MDC.clear();` kesinlikle çağrılmalıdır.
  * **3. Log Formatının (Pattern) Güncellenmesi:**
    * `logback-spring.xml` dosyasında log desenine `%X{correlationId}` eklenir:
      `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{correlationId}] - %msg%n`
  * **4. Servisler Arası Dağıtık İletim (Propagation):**
    * Mikroservis mimarisinde başka bir servise istek atılırken (`FeignClient`, `RestTemplate`, `WebClient`) bu header giden HTTP isteğine eklenir.
    * İstemciye dönülen yanıtta da `X-Correlation-ID` header'ı geri döndürülür; böylece kullanıcı hata aldığında destek ekibine bu referans numarasını iletebilir.
  * **5. Merkezi Log İzleme (ELK / OpenSearch / Grafana Loki):**
    * Tüm loglar merkezi sisteme aktarılır. Destek ve mühendislik ekipleri tek bir `correlationId` ile sorgu atarak isteğin sisteme girişinden çıkışına kadar geçtiği tüm mikroservis loglarını eksiksiz bir zaman çizelgesinde görüntüler.
  * **Örnek Filter Kodu:**

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(MDC_KEY, correlationId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear(); // Thread havuza dönmeden önce bağlam mutlaka temizlenmeli
        }
    }
}
```

---

### 15. Algoritma & Veri Yapısı – Tekrar Eden İşlem Kontrolü (Duplicate Transactions)

**Soru:** Bir hesaba ait işlem ID'lerini (`transaction id`) tutan bir dizi var. Sistemde bir hata nedeniyle bazı işlemler birden fazla kez kaydedilmiş olabilir.
* Fonksiyon imzası: `boolean hasDuplicateTransaction(int[] transactionIds)`
* Dizide tekrar eden bir eleman var mı kontrol eden bir fonksiyon yaz.
* Hangi veri yapısını kullanman gerektiğini açıkla (`HashSet` düşün), zaman ve alan karmaşıklığını belirt.

* **Cevap:**
  * **Kullanılan Veri Yapısı:** `HashSet<Integer>`
  * **Çalışma Mantığı:**
    * `HashSet`, elemanları hash tablosu üzerinde depolar ve benzersizlik sağlar.
    * `HashSet.add(element)` metodu, eleman kümede zaten varsa `false`, eleman kümeye ilk kez eklendiyse `true` döner.
    * Bu özellik sayesinde `contains()` ve `add()` metotlarını ayrı ayrı çağırmak yerine doğrudan `if (!seen.add(id))` kontrolüyle tek adımda mükerrerlik tespit edilir.
  * **Java Çözümü:**

```java
import java.util.HashSet;
import java.util.Set;

public class TransactionService {

    public boolean hasDuplicateTransaction(int[] transactionIds) {
        if (transactionIds == null || transactionIds.length <= 1) {
            return false;
        }

        Set<Integer> seen = new HashSet<>();
        for (int id : transactionIds) {
            // id zaten kümede varsa add() metodu false döner
            if (!seen.add(id)) {
                return true; // Tekrar eden işlem bulundu
            }
        }

        return false; // Hiç tekrar eden bulunamadı, tüm işlemler benzersiz
    }
}
```

  * **Zaman ve Alan Karmaşıklığı:**
    * **Zaman Karmaşıklığı:** Ortalama $O(n)$. Dizi bir kez taranır ($n$ eleman), `HashSet.add()` işlemi ortalama $O(1)$ sürede tamamlanır. (En kötü durumda hash çakışmalarında $O(n^2)$ olabilir).
    * **Alan (Space) Karmaşıklığı:** $O(n)$. Tekrar eden eleman olmaması durumunda dizideki tüm elemanlar ($n$ adet) `HashSet` içinde saklanır.
  * **Alternatif Yaklaşım (Bellek Tasarrufu İçin):**
    * Dizi yerinde sıralanabilir (`Arrays.sort`), ardından yan yana olan elemanlar (`transactionIds[i] == transactionIds[i+1]`) kontrol edilir. Bu yöntem $O(n \log n)$ zaman alır ancak ek bellek maliyeti $O(1)$ olur. Hız öncelikli senaryolarda `HashSet` yaklaşımı tercih edilir.

---

### 16. SQL – Birden Fazla Hesap Türüne Sahip Müşterileri Listeleme

**Soru:** Aşağıdaki tablo şemalarını kullanarak; birden fazla farklı `account_type`'a sahip (örneğin hem "VADESIZ" hem "VADELI" hesabı olan) müşterileri, isimleri ve sahip oldukları farklı hesap türü sayısıyla birlikte listeleyen bir SQL sorgusu yaz.

```text
CUSTOMER (customer_id, name, created_at)
ACCOUNT (account_id, customer_id, balance, account_type)
TRANSACTION (transaction_id, account_id, amount, transaction_date, transaction_type)
```

* **Cevap:**
  * **Sorgu Mantığı:**
    * `CUSTOMER` ve `ACCOUNT` tabloları `customer_id` üzerinden `INNER JOIN` ile bağlanır.
    * Müşteri bazında gruplama yapmak için `GROUP BY c.customer_id, c.name` kullanılır.
    * Müşterinin sahip olduğu farklı hesap türlerini saymak için `COUNT(DISTINCT a.account_type)` fonksiyonu kullanılır.
    * Yalnızca farklı hesap türü sayısı 1'den büyük olan müşterileri getirmek için `HAVING COUNT(DISTINCT a.account_type) > 1` filtresi uygulanır.
  * **SQL Çözümü:**

```sql
SELECT 
    c.customer_id,
    c.name,
    COUNT(DISTINCT a.account_type) AS distinct_account_type_count
FROM CUSTOMER c
INNER JOIN ACCOUNT a ON c.customer_id = a.customer_id
GROUP BY 
    c.customer_id, 
    c.name
HAVING 
    COUNT(DISTINCT a.account_type) > 1;
```