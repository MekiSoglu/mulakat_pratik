# Gün 5 – Mülakat Hazırlık Soruları ve Çözümleri

---

### 1. Java – Thread vs Runnable & ExecutorService Kullanımı

**Soru:** Java – Thread ile Runnable arasındaki fark nedir? ExecutorService kullanmak, doğrudan `new Thread()` oluşturmaktan neden daha iyi bir pratiktir?

* **Cevap:**
  * **Thread:** Java'da doğrudan bir sınıftır (`Thread`). Java'da çoklu kalıtım (multiple inheritance) desteklenmediği için `Thread` sınıfını extend eden bir sınıf başka hiçbir sınıfı extend edemez. Bir `Thread` nesnesi çalıştırılıp işi bittiğinde sonlanır ve yeniden başlatılamaz. İş tanımı (task) ile iş parçacığı mekanizmasını (worker) birbirine sıkı sıkıya bağlar.
  * **Runnable:** Bir fonksiyonel arayüzdür (`@FunctionalInterface`). İçerisinde yalnızca `run()` metodunun gövdesini tanımlar. Kendi başına bir thread yaratmaz; yalnızca yürütülecek işi tanımlar. Bu sayede sınıf başka sınıfları extend edebilir, lambda ifadeleriyle pratikçe kullanılabilir ve thread havuzlarında (Thread Pool) tekrar tekrar çalıştırılabilir (Separation of Concerns).
  * **`run()` vs `start()` Metotları:**
    * `.run()`: Yeni bir thread oluşturmaz. Çağrıldığı mevcut thread (örneğin main thread) içerisinde senkron olarak normal bir metot gibi doğrudan çalışır.
    * `.start()`: JVM seviyesinde yeni bir işletim sistemi thread'i tahsis eder, thread yaşam döngüsünü başlatır ve `run()` metodunu bu yeni thread üzerinde asenkron olarak yürütür.
  * **Doğrudan `new Thread()` Yerine `ExecutorService` Kullanmanın Avantajları:**
    * **Maliyet ve Kaynak Yönetimi:** Her `new Thread()` çağrısı işletim sistemi seviyesinde bellek (stack memory) tahsisi ve CPU context switch maliyeti yaratır. Kontrolsüz thread üretimi `OutOfMemoryError` (OOM) veya sistem kilitlenmelerine yol açar.
    * **Thread Havuzu (Thread Pool):** `ExecutorService`, önceden oluşturulmuş belirli sayıdaki thread'i havuzda hazır tutar ve işi biten thread'i yok etmek yerine yeni bir iş için tekrar kullanır.
    * **İş Kuyruğu (Work Queue) ve Yük Dengeleme:** Gelen görevler bir kuyrukta (`BlockingQueue`) toplanır ve müsait olan thread'ler tarafından sırayla tüketilir.
    * **Gelişmiş Görev Yönetimi (`Runnable` vs `Callable`):**
      * `Runnable`: Geriye bir değer dönmez ve checked exception fırlatamaz.
      * `Callable`: Geriye bir sonuç (`Future`) dönebilir ve checked exception fırlatabilir.
      * `ExecutorService`, görevleri iptal etme (`cancel`), zaman aşımı koyma ve paralel işlerin tamamlanmasını bekleme gibi zengin bir yönetim API'si sunar.

---

### 2. Java – Static vs Instance Değişken/Metot & Utility Sınıfı Tasarımı

**Soru:** Java – static değişken/metot ile instance değişken/metot arasındaki fark nedir? Bir `AccountUtils` sınıfında para formatlama metodunu neden static yaparsın?

* **Cevap:**
  * **Static Değişken ve Metotlar:**
    * Sınıfın kendisine aittir; nesne (`new`) oluşturulmadan doğrudan sınıf adı üzerinden çağrılır (`AccountUtils.formatMoney(...)`).
    * Bellekte Metaspace / Heap seviyesinde sınıf yüklendiğinde tek bir kopya olarak oluşturulur ve uygulama yaşam döngüsü boyunca kalır.
    * Static metotlar içinden doğrudan instance (static olmayan) değişkenlere erişilemez çünkü `this` referansı bulunmaz. Ancak instance metotlar static alan ve metotlara rahatça erişebilir.
  * **Ne Zaman Static İhtiyacı Olur?:**
    * `static final` ile uygulama genelinde değişmeyecek sabitleri (constants) tanımlamak için.
    * Singleton tasarım deseni uygulamalarında tekil nesne referansını tutmak için.
    * Oluşturulması yüksek maliyetli nesneleri tek bir kez oluşturup tekrar kullanmak için (örneğin `Pattern` / Regex nesneleri).
    * Sınıf düzeyinde ortak konfigürasyon veya logger nesneleri tanımlamak için (`private static final Logger log = ...`).
    * Tüm nesneler arasında paylaşılan ortak sayaç veya durumları yönetmek için.
  * **Instance (Field) Değişken ve Metotlar:**
    * Nesnenin kendisine aittir. Her `new` çağrısında Heap bellekte o nesneye özel yeni bir alan açılır.
    * Nesnenin yaşam döngüsü Garbage Collector'ın (GC) referansı toplamasına kadar sürer.
  * **`AccountUtils` Para Formatlama Metodunun Static Yapılma Nedenleri:**
    * **Durumsuzluk (Stateless):** Metot sadece kendisine parametre olarak verilen para tutarını formatlar; sınıf içinde saklanan herhangi bir duruma (state) ihtiyaç duymaz.
    * **Bellek ve GC Performansı:** Her formatlama işleminde yeni bir nesne (`new AccountUtils()`) oluşturulmadığı için gereksiz bellek tüketimi ve GC baskısı önlenir.
    * **Erişim Kolaylığı:** Kodun her yerinden nesne oluşturma zahmetine girmeden doğrudan sınıf adı üzerinden çağrılabilir.
  * **Utility Sınıfı Tasarım Kuralları:**
    * Nesne durumuna bağlı olmayan genel operasyonları barındırırlar.
    * Dışarıdan veya içeriden nesne oluşturulmasını engellemek için constructor'ı `private` yapılmalı, sınıf `final` işaretlenmeli ve tüm yardımcı metotlar `static` tanımlanmalıdır.

---

### 3. SQL – CTE (Common Table Expression) & Recursive CTE

**Soru:** SQL – CTE (Common Table Expression, WITH ifadesi) nedir? Recursive CTE ne işe yarar — örneğin bir müşteri hiyerarşisinde (üst hesap/alt hesap) kullanımına örnek verebilir misin?

* **Cevap:**
  * **CTE (Common Table Expression):**
    * `WITH` ifadesiyle tanımlanan, tek bir SQL sorgusu (SELECT, INSERT, UPDATE veya DELETE) süresince geçerli olan geçici ve adlandırılmış bir sonuç kümesidir.
    * Karmaşık ve iç içe geçmiş alt sorguları (subquery) parçalayarak sorgunun okunabilirliğini, bakımını ve modülerliğini artırır.
  * **Recursive CTE (Özyinelemeli CTE):**
    * Hiyerarşik, ağaç veya grafik yapısındaki verileri (örneğin organizasyon şeması, kategori ağacı, üst hesap / alt hesap ilişkileri) sorgulamak için kendi kendini yineleyen CTE yapısıdır.
    * Temel olarak iki kısımdan oluşur:
      1. **Anchor Member (Kök / Başlangıç Sorgusu):** Hiyerarşinin en üst seviyesini seçen temel sorgudur.
      2. **UNION ALL:** Başlangıç sorgusu ile özyinelemeli adımı birbirine bağlar.
      3. **Recursive Member (Özyinelemeli Sorgu):** Bir önceki adımın çıktısını mevcut tabloyla JOIN yaparak bir alt seviyeye iner. Yeni eşleşme kalmayana kadar kendini çalıştırmaya devam eder.
  * **Müşteri / Hesap Hiyerarşisi Örneği:**

```sql
-- Ana hesap ve ona bağlı tüm alt hesapları hiyerarşik seviyesiyle getiren Recursive CTE
WITH RECURSIVE AccountHierarchy AS (
    -- 1. Anchor Member: En üst seviyedeki ana hesap (parent_account_id değeri NULL olan)
    SELECT 
        account_id, 
        parent_account_id, 
        account_name, 
        1 AS level
    FROM accounts
    WHERE parent_account_id IS NULL AND account_id = 100

    UNION ALL

    -- 2. Recursive Member: Bir önceki seviyedeki hesabın alt hesaplarını bulan sorgu
    SELECT 
        a.account_id, 
        a.parent_account_id, 
        a.account_name, 
        ah.level + 1 AS level
    FROM accounts a
    INNER JOIN AccountHierarchy ah ON a.parent_account_id = ah.account_id
)
SELECT * FROM AccountHierarchy ORDER BY level, account_id;
```

---

### 4. SQL – Composite (Çok Kolonlu) Index & Kolon Sırası Önemi

**Soru:** SQL – Composite (çok kolonlu) index nedir? `(customer_id, transaction_date)` şeklinde bir index'te kolon sırası neden önemlidir?

* **Cevap:**
  * **Composite (Bileşik) Index:**
    * Bir tablodaki birden fazla kolonun tek bir B-Tree indeks yapısı altında birleştirilerek indekslenmesidir.
    * Sorgularda `WHERE`, `JOIN`, `ORDER BY` veya `GROUP BY` koşullarında birlikte kullanılan kolonlar için tek kolonlu indekslere göre çok daha yüksek sorgu performansı sağlar.
    * Eğer sorgulanan tüm kolonlar bu indekste mevcutsa, veritabanı asıl tabloya gitmeden doğrudan indeksten yanıt döner (Covering Index / Index-Only Scan).
  * **Kolon Sırasının Önemi ve En Soldaki Önek Kuralı (Leftmost Prefix Rule):**
    * B-Tree indeksinde veriler **soldan sağa** doğru sıralanır. `(customer_id, transaction_date)` indeksinde ağacın ana dalları `customer_id` değerine göre dizilir; aynı `customer_id` değerine sahip kayıtlar ise kendi içinde `transaction_date` değerine göre sıralanır.
    * **İndeksin Kullanım Durumları:**
      * `WHERE customer_id = 10` $\rightarrow$ İndeks verimli şekilde kullanılır.
      * `WHERE customer_id = 10 AND transaction_date = '2026-01-01'` $\rightarrow$ İndeksin her iki kolonu da tam verimle kullanılır.
      * `WHERE customer_id = 10 AND transaction_date >= '2026-01-01'` $\rightarrow$ `customer_id` ile nokta atışı yapılır, tarih için aralık taraması (range scan) verimli çalışır.
      * `WHERE transaction_date = '2026-01-01'` (yalnızca tarih verildiğinde) $\rightarrow$ En soldaki kolon (`customer_id`) sorguda yer almadığı için B-Tree kökten hedefe inemez; indeks doğrudan kullanılamaz (Full Table Scan veya Full Index Scan yapılır).
  * **Tasarım İlkesi (Cardinality):**
    * Eşitlik (`=`) filtresinde en sık kullanılan ve seçiciliği (cardinality / benzersiz değer oranı) yüksek olan kolon en başa (en sola) yazılmalıdır.

---

### 5. Hibernate – Toplu İşlemlerde (Batch Processing) Performans Optimizasyonu

**Soru:** Hibernate – Toplu insert/update işlemlerinde (örn. 10.000 kayıt) performans nasıl optimize edilir? `hibernate.jdbc.batch_size` ne işe yarar?

* **Cevap:**
  * **`hibernate.jdbc.batch_size` Parametresi:**
    * Hibernate'in SQL INSERT/UPDATE ifadelerini veritabanına tek tek göndermek yerine, belirlenen sayı kadar (örneğin 50) gruplayarak tek bir JDBC batch isteğiyle toplu olarak göndermesini sağlar.
    * Bu sayede ağ üzerindeki gidiş-dönüş (network round-trip) sayısı dramatik şekilde azalır ve işlem hızı katbekat artar.
  * **Kritik Nokta: Primary Key Üretim Stratejisi (`GenerationType`):**
    * `GenerationType.IDENTITY` kullanılırsa veritabanı ID'yi satır eklendiği anda ürettiği için Hibernate, entity'yi persist eder etmez ID'sini öğrenmek zorundadır. Bu nedenle `IDENTITY` kullanıldığında JDBC batching **tamamen devre dışı kalır**.
    * Toplu işlemlerde batching'in çalışması için `GenerationType.SEQUENCE` (veya uygun `allocationSize` değerine sahip sequence) kullanılmalıdır.
  * **Bellek Yönetimi: `flush()` ve `clear()` Kullanımı:**
    * Hibernate'in First-Level Cache'i (Persistence Context / Session), yönettiği tüm entity'leri bellekte tutar. 10.000 kaydı doğrudan döngüde persist etmek belleği tüketir ve `OutOfMemoryError` (OOM) hatasına yol açar.
    * Her batch adımında (örneğin 50 işlemde bir) `entityManager.flush()` (biriken SQL'leri DB'ye gönderir) ve ardından `entityManager.clear()` (Session'ı boşaltarak entity'leri detached yapar ve belleği serbest bırakır) çağrılmalıdır.
  * **Örnek Kod Akışı:**

```java
int batchSize = 50;
for (int i = 0; i < accounts.size(); i++) {
    entityManager.persist(accounts.get(i));
    if (i > 0 && i % batchSize == 0) {
        entityManager.flush(); // Değişiklikleri veritabanına gönder
        entityManager.clear(); // Persistence context'i temizle, belleği rahatlat
    }
}
entityManager.flush();
entityManager.clear();
```

  * **Büyük Hacimli Veriler İçin Alternatifler:**
    * Çok daha yüksek hacimlerde Hibernate'in dirty checking ve proxy overhead'inden kaçınmak için `StatelessSession` veya doğrudan Spring'in `JdbcTemplate.batchUpdate()` mekanizması tercih edilir.

---

### 6. Hibernate – @EntityGraph & N+1 Problemi Çözümü

**Soru:** Hibernate – `@EntityGraph` nedir? N+1 problemini `JOIN FETCH` dışında nasıl çözer, ne zaman tercih edilir?

* **Cevap:**
  * **N+1 Problemi:**
    * Bir entity'nin ilişkili alt koleksiyonları (örn. `Customer` $\rightarrow$ `Accounts`) varsayılan olarak `LAZY` yüklendiğinde; 1 ana sorguyla N adet müşteri çekilirse, her bir müşterinin hesaplarına erişilmek istendiğinde arka planda N adet ilave SQL sorgusu atılır (toplamda 1 + N sorgu). Bu durum veritabanını aşırı yükler.
  * **`@EntityGraph` Nedir?:**
    * JPA 2.1 ile gelen, belirli bir sorgu anında hangi ilişkili nesnelerin `EAGER` (birlikte) yükleneceğini deklaratif olarak belirleyen bir mekanizmadır.
    * Arka planda `LEFT OUTER JOIN` oluşturarak ilişkili verileri tek bir SQL sorgusunda çeker.
  * **`JOIN FETCH` ile Farkları ve Tercih Edilme Nedenleri:**
    * **Temiz Repository Kodları:** Spring Data JPA türetilmiş sorgu metotları (`findByStatus`, `findAllById` vb.) üzerine doğrudan `@EntityGraph(attributePaths = {"accounts"})` konularak JPQL sorgusu yazmaya gerek kalmadan ilişki yüklenebilir.
    * **Dinamik Yükleme Stratejisi:** Entity sınıfı üzerinde aynı ilişki `LAZY` olarak kalırken, sadece ihtiyaç duyulan sorguda geçici olarak `EAGER` gibi davranması sağlanır.
    * **Graph Tipleri:**
      * `FETCH`: Yalnızca graph'ta belirtilen alanlar EAGER yüklenir, diğer tüm alanlar LAZY kabul edilir.
      * `LOAD`: Graph'ta belirtilenler EAGER yüklenir; belirtilmeyenler ise entity üzerinde tanımlı varsayılan stratejilerine göre davranır.
    * **Sayfalama (Pagination) Güvenliği:** Koleksiyon içeren `JOIN FETCH` JPQL sorgularında Spring Data JPA veritabanı seviyesinde `LIMIT/OFFSET` uygulayamayıp tüm kayıtları belleğe çekip bellekte sayfalama yapar (`HHH000104` uyarısı). `@EntityGraph` ve DTO projeksiyonları bu riskleri daha iyi yönetmeyi sağlar.

---

### 7. Spring Boot – Actuator, Health Check & İzleme Stratejileri

**Soru:** Spring Boot – Spring Boot Actuator nedir, ne işe yarar? Bir servisin "ayakta mı, veritabanına bağlanabiliyor mu" diye kontrol edilmesi (health check) nasıl yapılır?

* **Cevap:**
  * **Spring Boot Actuator:**
    * Uygulamanın üretim (production) ortamındaki sağlık durumunu, metriklerini (CPU, bellek, HTTP istekleri), thread dökümlerini, log seviyelerini ve ortam değişkenlerini HTTP veya JMX uç noktaları (`/actuator`) üzerinden izlemeyi ve yönetmeyi sağlayan resmi Spring Boot kütüphanesidir.
  * **Health Check Mekanizması (`/actuator/health`):**
    * `management.endpoint.health.show-details=always` konfigürasyonu ile detaylı bileşen durumu açılır.
    * Spring Boot, projede tanımlı bileşenleri otomatik algılar:
      * `DataSourceHealthIndicator`: Veritabanına bağlantı testi (connection pool'dan bağlantı alıp basit bir test sorgusu çalıştırarak) yapar.
      * `DiskSpaceHealthIndicator`, `RedisHealthIndicator` vb.
    * Gerekirse `HealthIndicator` arayüzü implemente edilerek üçüncü parti bağımlılıklar için özel sağlık kontrolleri (Custom Health Check) yazılabilir.
  * **Kubernetes Ortamında Health Check (Probes):**
    * **Liveness Probe (`/actuator/health/liveness`):** Uygulama sürecinin (JVM) kilitlenip kilitlenmediğini (deadlock vb.) kontrol eder. Başarısız olursa Kubernetes pod'u kapatıp yeniden başlatır (restart). *Veritabanı kontrolü buraya konulmamalıdır; aksi takdirde DB'deki geçici bir kesintide tüm pod'lar sonsuz restart döngüsüne girer.*
    * **Readiness Probe (`/actuator/health/readiness`):** Uygulamanın gelen istekleri karşılamaya hazır olup olmadığını (DB bağlantısı, cache yüklenmesi) kontrol eder. Başarısız olursa pod yeniden başlatılmaz; sadece load balancer trafiği o pod'a kesilir.
  * **Sentetik İzleme (Synthetic Monitoring / Black-Box):**
    * Harici izleme araçlarının gerçek bir kullanıcı gibi sistemi uçtan uca test etmesidir.
    * **Veri Temizliği ve İzolasyon:**
      * Test isteklerine özel başlıklar veya test kullanıcı bayrakları (`is_test=true`) atanır.
      * İşlemler test transaction'ı içinde yürütülüp yanıt kodu 200 olsa dahi veritabanına commit yerine `rollback` uygulanabilir.
      * Çok adımlı test senaryolarında oluşturulan kayıtlar için TTL (Time-To-Live) tanımlanır veya test sonunda temizleme (cleanup) tetiklenir.
      * Katı finansal sistemlerde test verileri ana tablolara değil, izole gölge tablolara (shadow tables) yazılır.

---

### 8. Spring Boot / Kavram – Rate Limiting & Token Bucket Algoritması

**Soru:** Spring Boot / Kavram – Rate Limiting nedir? Token Bucket algoritması nasıl çalışır? Bir bankanın dış API'sini (örn. fintech partnerlerine açık hesap sorgulama API'si) aşırı istekten korumak için nasıl kullanılır?

* **Cevap:**
  * **Rate Limiting:**
    * Bir API'ye veya sisteme belirli bir zaman aralığında (örneğin saniye/dakika başına) yapılabilecek istek sayısını sınırlandırarak sistemi aşırı yükten, kötüye kullanımdan ve DoS saldırılarından koruyan mekanizmadır.
  * **Token Bucket Algoritması:**
    * Belirli bir kapasiteye (`capacity`) sahip sanal bir kova tanımlanır.
    * Kovaya sabit bir hızla (örneğin saniyede 10 token) sürekli token eklenir (refill rate). Kova dolduğunda yeni gelen token'lar taşar ve yok sayılır.
    * İstemciden gelen her istek kovadan 1 token harcar:
      * Kovada yeterli token varsa istek kabul edilir ve hemen işlenir.
      * Kovada token kalmamışsa istek reddedilir (`HTTP 429 Too Many Requests`).
    * **Avantajı:** Ani trafik dalgalanmalarına (burst traffic) kovadaki mevcut token miktarı kadar anında izin verirken, uzun vadede ortalama istek hızını belirlenen limit içinde tutar.
  * **Banka Dış API Senaryosunda Uygulama:**
    * **Konum:** Rate limiting kontrolü doğrudan backend servislerinde değil, en dış katmanda yer alan **API Gateway** (Spring Cloud Gateway, Kong vb.) seviyesinde yapılmalıdır.
    * **Kimlik Doğrulama:** Gelen fintech partneri isteğindeki `API Key` veya `Client ID` üzerinden tanınır.
    * **Katmanlı Kova Modeli:**
      1. *Partner Bazlı Kova:* Her partnerin abonelik paketine göre ayrı kovası vardır (örn. saniyede 50 istek). Limiti aşarsa istemciye `429 Too Many Requests` dönülür.
      2. *Sistem Geneli Kova:* Tüm sistemin toplam kaldırabileceği maksimum kapasiteyi korur. Bu kapasite aşılırsa downstream servisleri korumak için `503 Service Unavailable` dönülür.
    * **Dağıtık Mimari ve Redis Entegrasyonu:**
      * Birden fazla gateway instance'ı olduğu için kova sayaçları merkezi **Redis Cluster** üzerinde tutulur.
      * Çoklu thread veya farklı gateway sunucularının aynı anda token tüketmeye çalışması durumunda race condition oluşmaması için kontrol ve token düşme işlemi **Redis Lua Script** ile atomik olarak yürütülür.
    * **Aşırı Yük Yönetimi ve Asenkronlaştırma:**
      * Resilience4j Circuit Breaker ile sistem aşırı yüklendiğinde servis geçici olarak devre dışı (open) bırakılır.
      * Anlık sorgular dışındaki kritik yazma/işlem istekleri doğrudan veritabanı yerine Kafka veya RabbitMQ kuyruklarına alınarak sistemin kaldırabileceği hızda asenkron işlenir.

---

### 9. Design Pattern – Observer Pattern & Spring Event Mekanizması

**Soru:** Design Pattern – Observer pattern nedir? Spring'in `ApplicationEventPublisher` / `@EventListener` mekanizması bu pattern'i nasıl uygular? Örnek bir kullanım senaryosu ver.

* **Cevap:**
  * **Observer Pattern:**
    * Bir nesnede (Subject / Publisher) bir durum değişikliği veya olay gerçekleştiğinde, bu nesneye kayıtlı tüm bağımlı nesnelerin (Observers / Subscribers) otomatik olarak bilgilendirilmesini sağlayan davranışsal (behavioral) bir tasarım desenidir.
    * Nesneler arasındaki sıkı bağımlılığı (tight coupling) ortadan kaldırır; yayıncı, dinleyicilerin kim olduğunu veya ne iş yaptığını bilmek zorunda kalmaz (Loose Coupling).
  * **Spring'deki Uygulama Bileşenleri:**
    * **Event (Olay Nesnesi):** Meydana gelen olaya ait verileri taşıyan POJO sınıfıdır (örneğin `MoneyTransferCompletedEvent`).
    * **`ApplicationEventPublisher` (Yayıncı):** Spring konteyneri tarafından sağlanan ve event nesnesini sisteme fırlatan (`publisher.publishEvent(event)`) bileşendir.
    * **`@EventListener` (Dinleyici):** Yayınlanan event tipini parametre olarak alan ve event fırlatıldığında otomatik tetiklenen metot anotasyonudur.
    * **`@Async`:** Dinleyicilerin ana akışı bloke etmeden ayrı bir thread'de asenkron çalışmasını sağlar.
    * **`@TransactionalEventListener`:** Event'in ana veritabanı transaction'ının durumuna göre (örneğin `phase = TransactionPhase.AFTER_COMMIT`) dinlenmesini sağlar; böylece işlem rollback olursa yan etkiler (SMS gönderimi vb.) çalıştırılmaz.
  * **Kullanım Senaryosu (Para Transferi):**
    * Kullanıcı para transferi işlemini tamamladığında `TransferService` yalnızca bir `MoneyTransferCompletedEvent` yayınlar.
    * Bu event'i dinleyen bağımsız bileşenler tetiklenir:
      * `SmsNotificationService`: Kullanıcıya bilgilendirme SMS'i gönderir.
      * `EmailNotificationService`: Dekont e-postasını hazırlar ve iletir.
      * `AuditLogService`: Yapılan işlemi denetim ve uyumluluk veritabanına kaydeder.
    * Bu sayede `TransferService`'e yeni bir bildirim kanalı eklendiğinde transfer kodunda hiçbir değişiklik yapılması gerekmez (Single Responsibility ve Open/Closed ilkelerine tam uyum).

---

### 10. Mikroservis – Dual Write Problemi & Transactional Outbox Pattern

**Soru:** Mikroservis/Kavram – Outbox Pattern nedir? Bir mikroservisin hem veritabanına yazması hem de aynı anda mesaj kuyruğuna event göndermesi (dual write problemi) neden risklidir, Outbox Pattern bunu nasıl çözer?

* **Cevap:**
  * **Dual Write Problemi ve Riskleri:**
    * Bir mikroservisin hem ilişkisel veritabanına kayıt atması hem de bir mesaj kuyruğuna (Kafka, RabbitMQ) event göndermesi iki farklı dağıtık sistem üzerinde işlem yapmayı gerektirir. Bunlar tek bir yerel veritabanı transaction'ı altında atomik olarak yönetilemez.
    * **Senaryo A (DB Commit, Mesaj Gönderilemedi):** Veritabanı işlemi başarıyla commit edilir, fakat mesaj kuyruğuna yazılırken ağ kopar veya servis çöker. Veritabanındaki değişiklik diğer mikroservislere bildirilemez, sistemler arasında tutarsızlık oluşur.
    * **Senaryo B (Mesaj Gönderildi, DB Rollback):** Mesaj önce kuyruğa gönderilir, fakat ardından veritabanı transaction'ı rollback olur. Diğer servisler gerçekleşmemiş bir işlem için "hayalet event" (ghost event) alarak hatalı işlemler yapar.
  * **Outbox Pattern Çözümü:**
    * Ana veritabanı içerisinde iş tablolarının yanında bir `OUTBOX` tablosu oluşturulur.
    * İş mantığı kaydı (örneğin sipariş kaydı) ile kuyruğa gönderilecek event kaydı (`OUTBOX` tablosuna satır olarak) **aynı yerel ACID veritabanı transaction'ı (`@Transactional`)** içinde yazılır.
    * Eğer veritabanı işleminde hata çıkarsa her iki kayıt da rollback olur; işlem başarılı olursa her ikisi de atomik olarak kaydedilir.
  * **Outbox Tablosundaki Mesajların Kuyruğa Aktarılması:**
    1. **Polling Publisher (Zamanlanmış Görev):** Arka planda çalışan bir scheduler, belirli aralıklarla `OUTBOX` tablosundaki işlenmemiş (`status = 'PENDING'`) kayıtları okur, Kafka'ya gönderir ve durumlarını `PROCESSED` olarak günceller.
    2. **Transaction Log Tailing (CDC - Debezium):** Veritabanının write-ahead log'larını (WAL / binlog) dinleyen bir Change Data Capture (CDC) aracı, `OUTBOX` tablosuna atılan her insert'ü anında okuyarak doğrudan Kafka topic'ine aktarır. Uygulamaya ek sorgu yükü getirmez.
  * **Tüketici Tarafı (Consumer):**
    * Mesajların en az bir kez teslimatı (At-Least-Once Delivery) garanti edilir. Mükerrer gönderim ihtimaline karşı tüketen servisler `Idempotent Consumer` mantığıyla çalışmalıdır.

---

### 11. Senaryo 1 – Fintech Partneri İçin API Gateway Seviyesinde Rate Limiting

**Soru:** Bankanın hesap sorgulama API'sini kullanan bir fintech partneri, saniyede yüzlerce istek göndererek sistemi yavaşlatıyor. Bu durumu nasıl önlersin? Rate limiting'i API Gateway seviyesinde mi, uygulama seviyesinde mi uygularsın? Limit aşıldığında istemciye ne dönersin (hangi HTTP status kodu, hangi header'lar)?

* **Cevap:**
  * **Rate Limiting Katmanı (API Gateway Seviyesi):**
    * Rate limiting kesinlikle **API Gateway** seviyesinde uygulanmalıdır.
    * **Neden Gateway?:** Aşırı isteklerin backend servislerine, thread havuzlarına ve veritabanı bağlantı havuzlarına ulaşmadan en dış sınırda filtrelenmesi gerekir. Böylece backend sunucuları gereksiz CPU ve bellek tüketiminden korunur.
  * **Uygulanacak Limit Stratejileri:**
    1. **Partner Bazlı Limit (Client-Level):**
       * Fintech partneri `API Key` veya `Client ID` üzerinden tanımlanır.
       * Token Bucket algoritması ile partner başına saniyelik/dakikalık istek limiti (örneğin 50 req/sec) belirlenir.
       * Partner bu limiti aştığında isteği backend'e iletilmeden doğrudan reddedilir.
    2. **Sistem Seviyesinde Global Limit:**
       * Sistemin toplam taşıma kapasitesi aşıldığında genel sistem sağlığını korumak adına `503 Service Unavailable` dönülür ve downstream bağımlılıklar için Resilience4j devre kesici (Circuit Breaker) devreye alınır.
  * **Limit Aşıldığında Dönecek HTTP Yanıtı ve Header'lar:**
    * **HTTP Durum Kodu:** `429 Too Many Requests`
    * **Standart HTTP Header'ları:**
      * `Retry-After: 30` (İstemcinin kaç saniye sonra tekrar istek atabileceğini belirtir).
      * `X-RateLimit-Limit: 50` (İzin verilen maksimum istek kapasitesi).
      * `X-RateLimit-Remaining: 0` (Mevcut zaman diliminde kalan istek hakkı).
      * `X-RateLimit-Reset: 1715698800` (Kotanın sıfırlanacağı Unix epoch zaman damgası).
  * **Dağıtık Sayaç Yönetimi:**
    * API Gateway birden fazla pod/instance olarak çalıştığı için sayaçlar merkezi bir **Redis Cluster** üzerinde tutulur.
    * Eşzamanlı isteklerde yarış durumu (Race Condition) oluşmaması için Redis üzerinde **Lua Script** çalıştırılarak token kontrolü ve eksiltme atomik olarak yapılır.

---

### 12. Senaryo 2 – Dual Write & Outbox Akışı

**Soru:** "Hesap Servisi" bir işlem yaptıktan sonra hem veritabanına kayıt atıyor hem de Kafka'ya event gönderiyor. Veritabanı yazması başarılı oluyor ama uygulama Kafka'ya göndermeden hemen önce çöküyor. Bu senaryoda ne gibi bir veri tutarsızlığı oluşur? Outbox Pattern'i bu soruna nasıl bir çözüm sunar? Kısaca akışını anlat.

* **Cevap:**
  * **Oluşan Veri Tutarsızlığı:**
    * Veritabanı yazması başarılı olduğu için hesap bakiyesi güncellenmiş veya para hesaptan çekilmiştir.
    * Ancak Kafka'ya event gönderilemeden uygulama çöktüğü için bu olayı bekleyen bağımlı servisler (örneğin Muhasebe/Ledger Servisi, Bildirim Servisi, Fraud/Dolandırıcılık Tespit Servisi) işlemden haberdar olamaz.
    * Sonuç olarak bankanın ana veritabanı ile yan sistemleri arasında kalıcı veri tutarsızlığı (data inconsistency) meydana gelir.
  * **Outbox Pattern Çözümü ve Akışı:**
    1. **Tek Bir Atomik Transaction:** Hesap Servisi veritabanına bakiye kaydını atarken, aynı SQL transaction'ı (`@Transactional`) içinde `outbox_events` tablosuna da gönderilecek Kafka mesajını yazar.
    2. **Çökme Durumunda Güvenlik:** Uygulama veritabanına yazdıktan sonra hemen çökse bile, event veritabanındaki `outbox_events` tablosunda kalıcı olarak saklanmış durumdadır.
    3. **Asenkron Mesaj İletimi (CDC veya Poller):**
       * Debezium gibi bir Değişiklik Verisi Yakalama (CDC) aracı, veritabanının transaction log'larından (WAL/binlog) `outbox_events` tablosundaki yeni satırı yakalar ve Kafka topic'ine basar.
       * Veya arka plandaki bir Scheduled Worker servisi periyodik olarak bekleyen kayıtları okuyup Kafka'ya iletir.
    4. **Tutarlılık Garantisi:** Veritabanına yazılan her işlem kesinlikle Kafka'ya en az bir kez (at-least-once) ulaştırılmış olur.

---

### 13. Senaryo 3 – Cache Invalidation & Kur Sabitleme (Rate Locking)

**Soru:** Döviz kuru bilgisini `@Cacheable` ile 10 dakika cache'liyorsun. Ancak Merkez Bankası kuru güncellediğinde, senin sistemin hâlâ 8 dakika önceki eski kuru dönüyor ve bir müşteri yanlış kurdan işlem yapıyor. Bu tutarsızlığı nasıl önlersin? (TTL kısaltma, event-driven cache invalidation, cache-aside pattern gibi seçenekleri değerlendir.) Kritik finansal veriler için cache kullanmak ne zaman riskli olabilir?

* **Cevap:**
  * **Çözüm Seçeneklerinin Değerlendirilmesi:**
    * **TTL Kısaltma:** Cache süresini 10 dakikadan 30 saniyeye indirmek bayat veri risk penceresini daraltır ancak tamamen sıfırlamaz; üstelik veritabanına olan yükü gereksiz yere artırır. Finansal doğruluk için tek başına yetersizdir.
    * **Event-Driven Cache Invalidation (En Uygun Yaklaşım):** Merkez Bankası veya ana kur sağlayıcısı kuru güncellediğinde bir webhook veya mesajlaşma (Kafka/RabbitMQ) olayı tetiklenir. Uygulama bu eventi dinleyerek `@CacheEvict` ile ilgili cache anahtarını derhal siler veya `@CachePut` ile yeni kuru doğrudan cache'e yazar. Bu sayede kur değiştiği anda cache tutarlı hale gelir.
    * **Cache-Aside Pattern:** Uygulama okurken önce cache'e bakar, yoksa DB'den okur ve cache'e yazar. Veri güncellendiğinde ise yazma işlemi sırasında cache açıkça temizlenir (eviction).
  * **Kritik Finansal Verilerde Cache Riski:**
    * Cache mekanizmaları **yalnızca okuma ve arayüzde gösterim** amaçlı kullanılmalıdır.
    * Para transferi, döviz alım-satım emrinin onaylanması veya bakiye güncellemesi gibi nihai finansal işlemler kesinlikle cache'teki veriye güvenilerek commit edilmemelidir.
  * **Kur Sabitleme Deseni (Rate Locking / Quote Pattern):**
    * Müşteri döviz alım-satım ekranını açtığında ona belirli bir süre (örneğin 30-60 saniye) geçerli olan özel bir teklif (`Quote`) üretilir ve benzersiz bir `quote_id` ile Redis'e TTL verilerek kaydedilir.
    * Müşteri işlemi onayladığında sistem `quote_id`'nin süresinin dolup dolmadığını kontrol eder. Süre dolmamışsa teklif edilen kurdan işlem yapılır ve `quote_id` silinerek tekrar kullanımı engellenir (idempotency). Süre dolmuşsa işlem reddedilir ve güncel kurla yeni teklif istenir.

---

### 14. Senaryo 4 – Thread Pool Tükenmesi (Thread Starvation) & Bulkhead Pattern

**Soru:** Ödeme servisi, her isteği işlerken senkron olarak dış bir kredi kartı sağlayıcısını (3. parti API) çağırıyor. Bu sağlayıcı yavaşladığında (yanıt süresi 30 saniyeye çıkıyor), sunucudaki tüm thread'ler bu isteklerde bekliyor ve yeni gelen istekler işlenemez hale geliyor — tüm sistem etkileniyor. Bu soruna ne ad verilir (thread starvation/pool exhaustion)? Nasıl önlersin? Bulkhead pattern nedir, bu senaryoda nasıl yardımcı olur? Timeout mekanizması eklemenin önemi nedir?

* **Cevap:**
  * **Sorunun Tanımı:**
    * Bu duruma **Thread Starvation** (Thread Açlığı) veya **Thread Pool Exhaustion** (Thread Havuzunun Tükenmesi) denir. Tek bir harici servisteki gecikmenin tüm sunucu kaynaklarını tüketerek diğer bağımsız servisleri de çökertmesine ise **Kademeli Çöküş (Cascading Failure)** adı verilir.
  * **Önleme Yöntemleri:**
    1. **Katı Zaman Aşımları (Timeouts):** Dış servis HTTP çağrılarında `Connect Timeout` (örneğin 1-2 sn) ve `Read Timeout` (örneğin 3 sn) kesinlikle tanımlanmalıdır. Hiçbir thread'in dış servisten 30 saniye yanıt beklemesine izin verilmemelidir.
    2. **Circuit Breaker (Resilience4j):** Dış sağlayıcının yavaşladığı veya hata verdiği tespit edildiğinde devre "OPEN" durumuna geçirilir. Gelen istekler dış servise hiç gönderilmeden anında hata veya alternatif bir akışa yönlendirilir (Fail-Fast), thread'lerin askıda kalması önlenir.
    3. **Bulkhead Pattern (Gemi Kompartımanı Mantığı):**
       * Gemilerde su alan bir bölmenin tüm gemiyi batırmasını önleyen su geçirmez bölmeler gibi; uygulamanın genel thread havuzundan harici kredi kartı servisi için **ayrı ve izole edilmiş bir thread havuzu** (örneğin 20 thread) tahsis edilir.
       * Kredi kartı sağlayıcısı yavaşlasa bile yalnızca ona ayrılan 20 thread tıkanır; Tomcat'in ana thread havuzundaki diğer yüzlerce thread hesap sorgulama, bakiye görüntüleme vb. diğer tüm istekleri sorunsuz işlemeye devam eder.
    4. **Non-Blocking Asenkron İstemciler:** Bloklayıcı `RestTemplate` yerine reactive `WebClient` veya Java 21 Virtual Threads kullanılarak I/O bekleme sürelerinde işletim sistemi thread'lerinin kilitlenmesi engellenir.

---

### 15. Algoritma & Veri Yapısı – En Yüksek K Tutarlı İşlemi Bulma (Top K Transactions)

**Soru:** Bir hesapta yapılan işlem tutarlarını temsil eden bir dizi var. Bankanın risk ekibi, en yüksek tutarlı K işlemi hızlıca görmek istiyor.
* Fonksiyon imzası: `int[] findTopKTransactions(int[] amounts, int k)`
* Diziyi tamamen sıralamadan ($O(n \log n)$ yerine daha verimli), en yüksek K elemanı bulan bir fonksiyon yaz.
* Hangi veri yapısını (PriorityQueue/heap) kullanman gerektiğini açıkla, zaman karmaşıklığını belirt ($O(n \log k)$ hedefleniyor).
* Neden tüm diziyi sıralamanın (`Arrays.sort` + son k eleman) bu yönteme göre daha yavaş/verimsiz olduğunu açıkla.

* **Cevap:**
  * **Kullanılan Veri Yapısı:** `PriorityQueue<Integer>` (Varsayılan olarak Min-Heap).
  * **Çalışma Mantığı:**
    * En büyük $K$ elemanı bulmak için boyutu $K$ ile sınırlı bir **Min-Heap** kullanılır.
    * Min-Heap'in en tepesindeki eleman (`peek()`), o an havuzda bulunan $K$ eleman arasındaki **en küçük** değerdir.
    * Dizi taranırken yeni eleman tepedekinden büyükse, tepedeki küçük eleman havuzdan çıkarılır (`poll()`) ve yeni eleman eklenir (`offer()`).
    * Dizi taraması bittiğinde havuzda tam olarak dizinin en büyük $K$ elemanı kalır.
  * **Java Çözümü:**

```java
import java.util.PriorityQueue;

public class TransactionService {

    public int[] findTopKTransactions(int[] amounts, int k) {
        if (amounts == null || k <= 0) {
            return new int[0];
        }
        if (k >= amounts.length) {
            return amounts.clone();
        }

        // Min-Heap: Boyutu k olan öncelik kuyruğu
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int amount : amounts) {
            if (minHeap.size() < k) {
                minHeap.offer(amount);
            } else if (amount > minHeap.peek()) {
                minHeap.poll(); // Havuzdaki en küçük elemanı çıkar
                minHeap.offer(amount); // Daha büyük olan yeni elemanı ekle
            }
        }

        // Sonuç dizisine aktarma
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll();
        }

        return result;
    }
}
```

  * **Zaman ve Alan Karmaşıklığı:**
    * **Zaman Karmaşıklığı:** $O(n \log k)$. Boyutu en fazla $k$ olan heap'e eleman ekleme ve çıkarma maliyeti $\log k$'dır. $n$ adet eleman gezildiği için toplam süre $O(n \log k)$ olur.
    * **Alan (Space) Karmaşıklığı:** $O(k)$. Bellekte yalnızca $k$ adet eleman tutulur.
  * **Neden `Arrays.sort` ($O(n \log n)$) Yerine Bu Yöntem Tercih Edilir?:**
    * Tüm diziyi sıralamak (`Arrays.sort`) $O(n \log n)$ zaman alır.
    * Özellikle bankacılıkta $n$ çok büyük (örneğin 10.000.000 işlem) ve $k$ küçük (örneğin en yüksek 10 işlem) olduğunda:
      * $\log k \approx \log 10$ neredeyse sabit bir çarpan gibi kalır ve algoritma lineer $O(n)$ hızında tamamlanır.
      * Milyonlarca ilgisiz veriyi sıralama maliyeti ve bellek tüketimi tamamen ortadan kalkar.

---

### 16. Spring Boot'ta Rate Limiting: Tek Instance vs Dağıtık (Distributed) Mimari

**Soru:** Spring Boot mimarisinde Rate Limiting uygularken tek instance ile çoklu instance (dağıtık) ortamlar arasındaki farklar nelerdir?

* **Cevap:**
  * **Tek Instance Ortamı (In-Memory Rate Limiting):**
    * Uygulama tek bir sunucu üzerinde koşuyorsa Bucket4j kütüphanesi ve `ConcurrentHashMap` kullanılarak in-memory rate limiting yapılabilir.
    * IP veya kullanıcı ID bazlı token kovaları JVM belleğinde saklanır.
    * `ConcurrentHashMap`'in thread-safe yapısı ve atomik operasyonları sayesinde race condition oluşmaz ve harici bir ağ maliyeti olmadan sıfır gecikmeyle (ultra-low latency) çalışır.
  * **Çoklu Instance / Dağıtık Ortam Problemleri:**
    * Uygulama yatayda ölçeklendiğinde (Horizontal Pod Autoscaling) yük dengeleyici (Load Balancer) gelen istekleri pod'lara dağıtır (örneğin Round-Robin).
    * Her pod sadece kendi yerel RAM'ini bildiği için, kullanıcıya tanımlanan limit pod sayısıyla çarpılır (örneğin kullanıcıya tanınan 100 istek hakkı, 5 pod varsa pratikte 500 isteğe kadar çıkabilir). Bu durum gerçek kullanımın takip edilememesine ve sistemin korunamamasına yol açar.
  * **Redis Tabanlı Dağıtık Çözüm:**
    * **Merkezi Durum Yönetimi:** Tüm pod'lar kendi yerel bellekleri yerine merkezi bir **Redis Cluster**'a bağlanır.
    * **Race Condition Engelleme:** Redis tek iş parçacıklı (single-threaded) yapısıyla komutları sırayla işletir. Kontrol ve token düşme işlemleri Redis **Lua Script** aracılığıyla atomik olarak çalıştırılır; böylece aynı anda gelen paralel isteklerde veri tutarsızlığı yaşanmaz.
    * **Bellek Optimizasyonu (TTL):** Belirli süre işlem yapmayan kullanıcıların sayaç anahtarları Redis'in otomatik anahtar sonlandırma (TTL - Time To Live) mekanizmasıyla silinir, Redis belleğinin şişmesi engellenir.