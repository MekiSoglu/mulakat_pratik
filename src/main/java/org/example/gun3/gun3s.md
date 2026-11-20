## Java – ArrayList ile LinkedList Arasındaki Farklar ve Tercih Durumları

**ArrayList:**

* **Arka Plan:** Dinamik olarak büyüyebilen bir dizi (array) kullanır.
* **Erişim Hızı:** İndeks tabanlı olduğu için rastgele erişim (random access) hızı **O(1)**'dir.
* **Ekleme/Silme:** Sona eleman eklemek (amortized) **O(1)**'dir. Ancak başa veya ortaya eleman eklendiğinde/silindiğinde sağdaki tüm elemanların kaydırılması gerektiği için zaman karmaşıklığı **O(n)**'dir.
* **Bellek Kullanımı:** Sadece verileri tuttuğu için bellek açısından daha verimlidir. (Ancak kapasite artırımlarında boşta kalan tahsis edilmiş alanlar olabilir).

**LinkedList:**

* **Arka Plan:** Çift yönlü bağlı liste (doubly linked list) kullanır. Her eleman (node) bir önceki ve bir sonraki elemanın referans adresini tutar.
* **Erişim Hızı:** İndeks erişimi yoktur. Veriye ulaşmak için baştan (veya sondan) itibaren gezinmek (traversal) zorunludur. Erişim hızı **O(n)**'dir.
* **Ekleme/Silme:** Eğer eklenecek/silinecek düğümün (node) referansı biliniyorsa ekleme/silme işlemi **O(1)**'dir. Ancak belirli bir indekse ekleme yapmak için önce o indeksi bulmak (gezinmek) gerekir, bu da toplam maliyeti **O(n)** yapar. Başa ve sona ekleme/silme **O(1)**'dir.
* **Bellek Kullanımı:** Her eleman veri ile birlikte iki adet ekstra işaretçi (pointer) tuttuğu için bellek tüketimi ArrayList'e göre çok daha yüksektir.

**Tercih Senaryosu:**
Gündelik hayatta işlemlerin %90'ı okuma ve sona ekleme olduğu için varsayılan olarak **ArrayList** tercih edilir. Modern donanımlarda işlemci önbellek (CPU Cache) yerleşimi nedeniyle ArrayList, bağlı listelerden çok daha performanslı çalışır. Çok yoğun başa/sona ekleme ve silme yapılacaksa (kuyruk yapıları), LinkedList yerine `ArrayDeque` kullanmak bellek ve hız açısından daha maliyetsizdir.

---

## Java – HashMap, TreeMap ve LinkedHashMap Arasındaki Farklar

> **Hatalı Bilgi Uyarısı:** Sunduğunuz metinde "HashMap arka planda hastable kullanır" ifadesi hatalıdır. `HashMap` ve `Hashtable` Java'da birbirinden farklı iki sınıftır. HashMap arka planda *Node dizisi (bucket array)* ve çakışma durumunda bağlı liste / kırmızı-siyah ağaç (Red-Black Tree) kullanır.

**HashMap:**

* **Arka Plan:** Hash tablosu (Bucket dizisi) kullanır.
* **Sıralama:** Elemanların ekleme sırasını veya değer sırasını tutmaz.
* **Performans:** Çakışma (collision) olmadığı durumlarda ekleme, silme ve arama işlemleri **O(1)** hızındadır.
* **Kullanım Senaryosu:** Sıralamanın önemsiz olduğu ve maksimum performans hedeflenen varsayılan durumlarda kullanılır.

**LinkedHashMap:**

* **Arka Plan:** HashMap'in Node sınıfından türeyen ve ek olarak çift yönlü bağlı liste (doubly linked list) barındıran bir yapıdır.
* **Faydaları:** Elemanların *ekleme sırasını (insertion-order)* veya *erişim sırasını (access-order)* korur.
* **LRU Cache Mantığı:** Erişim sırası (access-order) aktif edildiğinde, en son erişilen eleman sona, en az kullanılan (en eski) eleman başa alınır. Bu sayede bellek sınırı aşıldığında en eski elemanı O(1) sürede silerek basit bir önbellek (Cache / Redis alternatifi) mekanizması kurulabilir.
* **İterasyon Hızı:** HashMap'te iterasyon kapasite (bucket) boyutuna bağlıyken, LinkedHashMap'te sadece mevcut eleman sayısı kadar döner.
* **Dezavantaj:** Bağlı liste işaretçileri (before, after) tutulduğu için HashMap'e göre yaklaşık %30-%50 daha fazla bellek tüketir. REST API'den gelen JSON verilerini aynı sırayla işlemek veya sıralı loglama (FIFO) yapmak için uygundur.

**TreeMap:**

* **Arka Plan:** İkili arama ağacı (Red-Black Tree) kullanır. Hash tablosu kullanmaz.
* **Sıralama:** Anahtar (Key) değerine göre doğal sıralama (Comparable) veya verilen bir `Comparator` ile sıralı tutulur.
* **Performans:** Ekleme, silme ve arama işlemleri her zaman **O(log n)** karmaşıklığına sahiptir.
* **Kullanım Senaryosu:** `min`, `max`, `subMap` gibi aralık (range) sorguları gerektiren durumlarda kullanılır. Veritabanına gitmeden uygulama belleğinde finansal aralık veya zaman damgası hesaplamaları için idealdir. (Örn: Consistent Hashing / Hashring algoritmaları).

---

## SQL – ACID Prensipleri

Bir banka para transferi (A hesabından B hesabına 100 TL gönderimi) üzerinden açıklamalar:

* **Atomicity (Bölünemezlik):** İşlemler bir bütün olarak ele alınır. Ya tüm adımlar gerçekleşir ya da hiçbiri gerçekleşmez.
* *Örnek:* A hesabından 100 TL düşülür, B hesabına 100 TL eklenir. Eğer A'dan para düşüp, sistem tam B'ye para eklenecekken çökerse, işlem iptal edilir (Rollback) ve A'nın parası iade edilir.


* **Consistency (Tutarlılık):** İşlem öncesi ve sonrası veritabanı kurallarının (constraints) ihlal edilmemesidir.
* *Örnek:* A hesabının bakiyesi -'ye düşemez kuralı varsa, hesaptaki tutardan fazlası gönderilmek istendiğinde işlem reddedilir. Toplam para miktarı işlem öncesinde ve sonrasında tutarlı kalmalıdır.


* **Isolation (İzolasyon):** Aynı anda gerçekleşen işlemlerin birbirini etkilememesidir.
* *Örnek:* A hesabında 100 TL varken, aynı anda iki farklı yerden 100 TL'lik transfer isteği gelirse, veritabanı bunları kilitler (Lock) ve sıraya koyar. Bir işlem bitmeden diğeri bakiyeyi değiştiremez.


* **Durability (Kalıcılık):** İşlem "başarılı (Commit)" yanıtı döndüğü andan itibaren verinin kalıcı olmasıdır.
* *Örnek:* Para transferi başarılı olduktan 1 saniye sonra veritabanı sunucusunun fişi çekilse bile, sunucu yeniden başladığında o transferin kaydı transaction logları sayesinde veritabanında durmaya devam eder.



---

## SQL – Primary Key, Foreign Key ve Unique Constraint Farkları

* **Primary Key (Birincil Anahtar):** Bir tablodaki her bir kaydı benzersiz olarak tanımlayan kimliktir. Default olarak Clustered Index (Fiziksel sıralama) oluşturur. Bir tabloda yalnızca **1 adet** bulunabilir. Aynı zamanda otomatik olarak UNIQUE ve NOT NULL kurallarını içerir.
* **Foreign Key (Yabancı Anahtar):** Bir tablonun, başka bir tablodaki Primary Key (veya Unique) kolonu ile ilişki kurmasını sağlar. Tablolar arası veri bütünlüğünü korur (olmayan bir ID eklenmesini engeller). Bir tabloda **birden fazla** olabilir.
* **Unique Constraint (Benzersizlik Kısıtlaması):** Belirtilen kolon veya kolon kombinasyonlarındaki değerlerin tekrar etmesini engeller (Örn: E-posta, T.C. Kimlik No). Primary Key'den farklı olarak `NULL` değer kabul edebilir (veritabanı motoruna göre değişir). Bir tabloda **birden fazla** Unique Constraint olabilir.

---

## Hibernate – @GeneratedValue Stratejileri

* **IDENTITY:** ID üretme işi tamamen veritabanına bırakılır (Örn: MySQL `AUTO_INCREMENT`).
* *Kritik Fark:* Hibernate, kayıt işlemi (INSERT) veritabanına gidene kadar ID'yi bilemez. Bu nedenle Hibernate'in **toplu ekleme (JDBC Batch Insert)** optimizasyonu çalışmaz; her nesne anında veritabanına gönderilir.


* **SEQUENCE:** Veritabanındaki `SEQUENCE` objesini kullanır (PostgreSQL, Oracle).
* *Kritik Fark:* Hibernate, INSERT atmadan önce Sequence'den sıradaki ID bloğunu çeker. ID önceden bilindiği için entity'ler persistence context'te (önbellek) bekletilir ve tek seferde **toplu insert (Batch Insert)** yapılarak performans artırılır.


* **TABLE:** Veritabanında ayrı bir tablo oluşturulur ve ID sayaçları bu tabloda güncellenir.
* *Kritik Fark:* Hem tabloyu okumak hem de güncellemek ekstra I/O yükü getirdiği için performansı en kötü olan yöntemdir. Modern sistemlerde tercih edilmez.


* **AUTO:** Hibernate'in kullanılan veritabanı diyalektine (Dialect) göre en uygun stratejiyi kendisinin seçmesidir. MySQL için `IDENTITY`, PostgreSQL için `SEQUENCE` seçer.

---

## Hibernate – HQL ve Native SQL Farkları

* **HQL (Hibernate Query Language / JPQL):** Tablolar ve kolonlar üzerinden değil, Java'daki Entity sınıfları ve değişkenleri (property) üzerinden sorgu yazılır. Veritabanı bağımsızdır (MySQL'den Oracle'a geçildiğinde sorgular patlamaz). İlişkisel verileri `JOIN FETCH` ile çekerek **N+1 sorununu** çözer ve doğrudan DTO'ya map etme (projection) kolaylığı sağlar.
* **Native SQL:** Doğrudan veritabanına özgü, tablo isimleriyle yazılan standart SQL'dir.
* **Tercih Durumu:**
* CRUD işlemleri, Join'ler ve standart raporlamalar için her zaman **HQL/JPQL** tercih edilir.
* Karmaşık `Window Functions` (JPQL'in desteklemediği), spesifik veritabanı özellikleri (Örn: PostgreSQL JSONB operatörleri) veya ORM maliyetini ortadan kaldırmak istenen devasa toplu veri güncellemeleri (Bulk Update) için **Native SQL** kullanılır.



---

## Spring Boot – DispatcherServlet Akışı

Spring Boot uygulamasında bir HTTP isteğinin işlenme yaşam döngüsü:

1. **Client (İstemci):** HTTP isteğini (GET, POST vb.) atar.
2. **Filter Chain:** İstek Spring katmanına ulaşmadan önce web sunucusu (Tomcat vb.) seviyesinde filtrelerden geçer. (Spring Security, CorsFilter, Logging vb.)
3. **DispatcherServlet (Front Controller):** İsteği karşılayan merkez yapıdır.
* **A. HandlerMapping:** Gelen URL'in ve HTTP metodunun hangi Controller'a ait olduğunu bulur.
* **B. HandlerInterceptor (preHandle):** İstek Controller'a gitmeden önce araya giren ön kesiciler çalışır.
* **C. HandlerAdapter:** İsteği Controller'ın anlayacağı metoda dönüştürür. Gelen JSON'ı Java nesnesine bağlar (`@RequestBody`, `@PathVariable` işlemleri burada yapılır).
* **D. Controller Method:** İş mantığı (Business Logic / Service) çalıştırılır ve sonuç döner.


4. **HttpMessageConverter:** Controller'dan dönen Java nesnesi (Object), istemciye dönmek üzere Jackson kütüphanesi ile JSON'a çevrilir.
5. **HandlerInterceptor (postHandle / afterCompletion):** Yanıt dönmeden hemen önce son müdahaleler veya loglamalar yapılır.
6. **Response:** İstemciye HTTP yanıtı gönderilir.

---

## Spring Boot – Spring Profiles

Farklı ortamlar (Geliştirme, Test, Canlı) için uygulamanın yapılandırmalarını (veritabanı URL'i, log seviyeleri, portlar) değiştirmeyi sağlar.

* `application.yml`: Tüm ortamlarda geçerli temel konfigürasyon.
* `application-dev.yml`: Geliştirme (Local) ortamı ayarları (Örn: H2 DB, DEBUG log).
* `application-prod.yml`: Canlı ortam ayarları (Örn: PostgreSQL, WARN log).

**Nasıl Aktif Edilir?**

* YML dosyası içinden: `spring.profiles.active=dev`
* Sunucu başlatırken (JVM parametresi): `java -jar app.jar --spring.profiles.active=prod`
* Test sınıflarında: Sınıfın tepesine `@ActiveProfiles("test")` eklenerek.
* Kod içinde belirli servisleri ayırmak için sınıflara `@Profile("dev")` anotasyonu eklenebilir.

---

## Tasarım Deseni – Idempotency (Eşkuvvetlilik)

**Idempotency**, bir işlemin sunucuya 1 kez gönderilmesi ile 1000 kez gönderilmesinin sistemin durumunda aynı sonucu (yan etkiyi) yaratmasıdır.

*Senaryo:* Ağ hatasından dolayı aynı ödeme isteği iki kez giderse:

1. **Client-Side:** İstemci ödeme isteği yaparken benzersiz bir `Idempotency-Key` (UUID) oluşturup Header'a ekler.
2. **Server-Side:** Sunucu bu Key'i alır. Eğer bu Key daha önce işlenmişse ve veritabanında/Redis'te varsa (veya durum 'BAŞARILI' ise), işlemi tekrar yapmaz, önceki işlemin başarılı yanıtını döner.
3. **Database Level:** Ek güvenlik olarak veritabanında `transaction_id` veya `idempotency_key` kolonu `UNIQUE CONSTRAINT` yapılarak aynı isteğin fiziksel olarak iki kez yazılması (Race Condition) kesin olarak engellenir.

---

## Senaryo: Büyük Veri (Batch Processing) ve Raporlama

Büyük verileri `findAll()` ile tek seferde belleğe çekmek JVM'in Heap alanını doldurur ve `OutOfMemoryError` verir.

1. **Çözüm Yaklaşımı (Chunk Oriented Processing):**
İşlem için **Spring Batch** veya benzeri bir yığın (batch) okuma mimarisi kullanılır.
* **ItemReader:** Veriyi veritabanından sayfa sayfa (Pagination) veya Cursor/Streaming ile okur.
* **ItemProcessor:** Okunan veriyi işler.
* **ItemWriter:** Belirli bir blok boyutuna (Örn: Chunk=1000) ulaşıldığında topluca veritabanına yazar. İşlemler bellekte birikmez.


2. **Tetikleme Mekanizması:**
* Sadece tek bir sunucu varsa Spring'in `@Scheduled` anotasyonu kullanılabilir.
* Ancak çoklu sunucu (Microservice / Kubernetes) mimarilerinde birden fazla instance aynı anda uyanıp raporu mükerrer çalıştırabilir. Bunu önlemek için **ShedLock** ile veritabanı seviyesinde kilit konulmalı veya tetikleme işi Spring Boot'tan çıkarılıp **Kubernetes CronJob**, Airflow veya Quartz gibi harici orkestrasyon araçlarına devredilmelidir.


3. **Çökme Durumunda Kaldığı Yerden Devam Etme (Resilience):**
Spring Batch mimarisinde işlemler her Chunk (örneğin her 1000 kayıt) yazıldığında başarılı olarak "Commit" edilir ve `ExecutionContext` veya Batch meta tablolarına (son işlenen ID, kaçıncı sayfa vb.) kayıt atılır. Sunucu çöküp tekrar kalktığında, meta tablosuna bakar ve işin yarım kaldığını görüp son kaydedilen ID'den itibaren okumaya devam eder.

---

## Algoritma: En Sık İşlem Yapan Hesap

> **Hatalı Bilgi Uyarısı:** Metninizde bahsettiğiniz "Boyer-Moore Voting Algorithm" bu problem için **kullanılamaz**. Boyer-Moore algoritması yalnızca bir dizideki elemanların **yarısından fazlasını (n/2'den fazlasını)** oluşturan bir "Majority Element" (Baskın Eleman) varsa çalışır. Bir banka işlem logunda hiçbir hesabın toplam işlemlerin %50'sinden fazlasını tek başına yapması garanti değildir. Bu nedenle sadece en çok tekrar edeni (majority şartı olmadan) bulmak için HashTable (HashMap) kullanmak zorunludur.

**Java Çözümü (HashMap Kullanarak):**

* **Zaman Karmaşıklığı:** O(n) - Diziyi bir kere geziyoruz.
* **Alan Karmaşıklığı:** O(k) - K farklı hesap ID'si kadar bellekte yer tutarız.

```java
import java.util.HashMap;
import java.util.Map;

public int findMostFrequentAccount(int[] accountIds) {
    if (accountIds == null || accountIds.length == 0) {
        throw new IllegalArgumentException("Dizi boş olamaz");
    }

    Map<Integer, Integer> frequencyMap = new HashMap<>();
    int maxId = accountIds[0];
    int maxFrequency = 0;

    for (int accountId : accountIds) {
        // ID yoksa 0'dan, varsa mevcut değerden alıp 1 ekler
        int count = frequencyMap.getOrDefault(accountId, 0) + 1;
        frequencyMap.put(accountId, count);

        // Yeni frekans, şu ana kadarki en yüksek frekanstan büyükse günceller
        if (count > maxFrequency) {
            maxFrequency = count;
            maxId = accountId;
        }
    }

    return maxId;
}

```

Eğer hesap ID'lerinin (Account ID) alabileceği maksimum değer küçük ve biliniyorsa (Örn: 0 - 1000 arası), alan karmaşıklığını ve hızını optimize etmek için HashMap yerine basit bir sabit boyutlu `int[]` dizisi (Counting Array) kullanılabilir.

---

## SQL – Ortalama Altı Bakiyeler

**1. Correlated Subquery ile (Daha Yavaş):**
*Performans:* Dış sorgudaki (a) her bir satır için alt sorgu tekrar tekrar çalıştırıldığı için performans açısından verimsizdir.

```sql
SELECT a.account_id, a.balance, a.account_type
FROM ACCOUNT a
WHERE a.balance < (
    SELECT AVG(sub.balance)
    FROM ACCOUNT sub
    WHERE sub.account_type = a.account_type
);

```

**2. Window Function ile (Daha Hızlı):**
*Performans:* `AVG() OVER()` fonksiyonu veritabanında veriyi tek bir geçişte (partition bazında) hesaplar. Her satır için baştan sorgu atılmaz, bellekte kümelenip hesaplandığı için genellikle çok daha hızlıdır.

```sql
SELECT account_id, balance, account_type
FROM (
    SELECT
        account_id,
        balance,
        account_type,
        AVG(balance) OVER (PARTITION BY account_type) as avg_balance
    FROM ACCOUNT
) as T
WHERE balance < avg_balance;

```