### 1. Java – Checked vs Unchecked Exception & Para Transferi Tercihi

**Soru:** Checked exception ile unchecked exception arasındaki fark nedir? Bir para transferi işleminde hangi tür exception'ı tercih edersin ve neden?

* **Cevap:**
* **Checked Exception:** Derleme zamanında çıkan hatalardır. Java bunları `try-catch` ile kontrol etmeyi zorunlu kılar. Çoğunlukla 3. parti dış uygulamalardan gelir (SQL bağlantı hatası vb.).
* **Unchecked Exception:** Run-time esnasında gelen hatalardır. `try-catch` ile kontrol etme zorunluluğu yoktur. Kodla ilgili hatalardır (`NullPointerException` gibi).
* **Tercih:** Para transferi esnasında checked exception'ı tercih ederim. Hem `try-catch` ile kontrol yapılmalı hem de run-time'da değil, derleme zamanında daha erken hata fark edilebilir.

---
Gemini Code Assist
* **Geri Bildirim / Not:**
* Kavramsal ayrım doğru, ama tercih kısmında dikkat etmen gereken kritik bir nokta var: Spring'in `@Transactional` mekanizması varsayılan olarak sadece `RuntimeException` (unchecked) ve `Error` durumunda rollback yapar.
* Checked exception fırlatırsan ve `rollbackFor = Exception.class` belirtmezsen, transaction commit olur — yani para transferinde checked exception seçersen ve bunu unutursan, hatalı işlem veritabanına yazılabilir.
* Bu yüzden bankacılık projelerinde genelde unchecked (custom `RuntimeException` türevi) tercih edilir ya da checked kullanılıyorsa rollback policy'si mutlaka açıkça belirtilir.
* Bu, mülakatlarda sık sorulan bir "tuzak" noktasıdır — mutlaka bilmen lazım.

---

### 2. Java – double vs BigDecimal

**Soru:** Para tutarlarını tutmak için neden double yerine BigDecimal kullanılması önerilir? Aradaki farkı teknik olarak açıkla.

* **Cevap:**
* `double`, IEEE 754 standartlarında 2 tabanlı kayan noktalı (floating-point) aritmetiğe sahiptir. 10 tabanındaki işlemleri 2 tabanına çevirir; bu da sonsuz bir sayı yığını oluşturur.
* 64-bit mimaride olduğu için belirli bir noktada keser ve bu durum kaymalara (hassasiyet kaybına) neden olur.
* `BigDecimal` sayıyı 2 tabanına çevirmez, tam yazılım kurallarına göre işlem yapar.
* Bu yüzden hesaplarda hata olmaması için `BigDecimal` kullanılır.

---

### 3. SQL – Index Yapısı ve Güncelleme Maliyeti

**Soru:** Index nedir, clustered ve non-clustered index arasındaki fark nedir? Çok sık güncellenen bir transactions tablosunda index kullanımı hakkında ne düşünürsün?

* **Cevap:**
* **Index:** İstediğimiz kolonların SQL tarafından farklı bir B-Tree yapısında sıralı olarak tutulmasıdır. Bu index ile sorgu atıldığında tüm tablo gezilmeyip sadece bu küçük B-Tree gezilerek daha hızlı işlem yapılır.
* **Clustered Index:** Tablodaki fiziksel sıralamayı değiştirir. Tablo başına sadece 1 kere tanımlanabilir; genelde Primary Key üzerinde kullanılır.
* **Non-Clustered Index:** Tabloda kalıcı fiziksel bir değişim yapmaz. İstenilen satırları/kolonları başka bir B-Tree üzerinde adresleriyle sıralar.
* **Sık Güncellenen Tabloda Index:** Performansı çok düşürür. Tabloya veri yazma veya güncelleme yapıldığında tanımlı indexlerin de güncellenmesi gerekir. Örneğin tabloda 10 index varsa, bir veri ekleme işleminde arka planda 11 işlem (tablo + 10 index) gerçekleştirilir.

---

### 4. SQL – Transaction Isolation Seviyeleri

**Soru:** Transaction Isolation Level'ler nelerdir (Read Uncommitted, Read Committed, Repeatable Read, Serializable)? Bankacılık sistemlerinde hangi isolation level tercih edilir, neden?

* **Cevap:**
* **İzolasyon Tanımı:** Eş zamanlı giren işlemlerin birbirlerinin yaptığı değişiklikleri ne kadar görebildiğidir.
* **Read Uncommitted:** Bir veri commit edilmeden de okunabilir. Veri tutarsızlığına (Dirty Read) yol açar. İşlem commit edilmeden okunup sonrasında rollback olursa, hatalı ve var olmayan bir değer okunmuş olur.
* **Read Committed:** Veri ancak commit edildikten sonra okunabilir. Çoğu SQL veritabanında default yapı budur. İlk işlemin sonucuna göre bir işlem başlatıldığında ve hemen ardından başka bir kullanıcı işlem yaparsa (Non-repeatable Read), ilk okumaya göre yapılan işlem tutarsızlık üretebilir.
* **Repeatable Read:** Okunan satır işlem bitene kadar kilitlenir ve değiştirilmesine izin verilmez. Ancak sadece okunan satırlar kilitlendiği için araya yeni bir satır eklenirse (Phantom Read) tutarsızlık oluşabilir.
* **Serializable:** İşlem yapılan tablo veya aralık işlem süresi boyunca tamamen kilitlenir; yeni istekler araya giremez. Kesin tutarlılık sağlar ancak ciddi performans kaybı yaşatır.
* **Bankacılık Tercihi:** Genel kullanımda `Read Committed` tercih edilir. Yıllık/dönemsel toplu işlemler sırasında `Repeatable Read` kullanılır. Çok kritik ve yüksek güvenlik gerektiren yerlerde nadiren `Serializable` kullanılır.

---

### 5. Hibernate – Loading Stratejileri ve N+1 Problemi

**Soru:** Lazy loading ile Eager loading arasındaki fark nedir? N+1 problemi nedir ve nasıl çözülür?

* **Cevap:**
* **Lazy Loading:** İlişkili tablo verileri (örn. `@OneToMany`) anında getirilmez; sadece kod tarafında ihtiyaç duyulup çağrıldığında yüklenir.
* **Eager Loading:** Bağlı tablo verileri ana tablo çağrıldığı anda sorguya dahil edilerek yüklenir.
* **N+1 Problemi:** Bir tablonun ilişkili olduğu verilerle birlikte çekilmesi esnasında, 1 ana tablo sorgusunun ardından ilişkili kayıtlar için döngü içinde N adet alt sorgunun çalışması durumudur.
* **Çözüm Yolları:**
* Eager loading kullanımı (duruma göre)
* `JOIN FETCH` kullanımı
* DTO Projeksiyonları kullanımı

---

### 6. Hibernate – Entity Yaşam Döngüsü & Cache Mimarisi

**Soru:** Entity yaşam döngüsündeki durumlar nelerdir (Transient, Persistent, Detached, Removed)? First-level cache ile second-level cache arasındaki fark nedir?

* **Entity Durumları:**
* **Transient:** Nesnenin bellekte (RAM) oluştuğu fakat veritabanında karşılığı olmadığı, Hibernate Session tarafından henüz takip edilmediği durumdur.
* **Persistent:** Nesnenin veritabanında bulunduğu ve aktif Hibernate Session tarafından yönetildiği durumdur. Burada Hibernate dirty checking (değişiklik takibi) yapabilir.
* **Detached:** Nesnenin veritabanında var olduğu ancak bağlı olduğu Session kapatıldığı için artık takip edilmediği durumdur. Bu aşamadaki değişiklikler otomatik algılanmaz.
* **Removed:** Veritabanındaki kaydın silinmek üzere işaretlendiği durumdur. Transaction sonunda fiziksel silme (delete) işlemi gerçekleşir.

* **Cache Mekanizmaları:**
* **First-Level Cache:** Default olarak açıktır. Session bazlıdır; sorgulanan nesneler Session önbelleğine alınır ve tekrar istendiğinde veritabanına gidilmeden buradan dönülür. Session kapandığında temizlenir, kalıcı değildir, thread-safe değildir ve basit bir Map yapısıyla çalışır.
* **Second-Level Cache:** Uygulama genelinde (SessionFactory seviyesinde) çalışır. Sorgu atıldığında önce L1 Cache'e, bulunamazsa L2 Cache'e, orada da yoksa veritabanına gidilir. Dönen veri hem L1 hem L2'ye yazılır. Session kapansa bile veri önbellekte kalır. Sürekli güncellenen verilerde tercih edilmez. Redis gibi distributed araçlarla yönetilebilir; thread-safe'tir.

---

### 7. Spring Boot – Bean Yaşam Döngüsü ve Scope Türleri

**Soru:** Spring Bean yaşam döngüsü nasıl işler? Bean scope türleri (singleton, prototype vb.) nelerdir?

* **Bean Yaşam Döngüsü:**

1. **Instantiate:** Spring, Bean constructor'ını çağırarak nesneyi belleğe alır.
2. **Bağımlılık Enjeksiyonu:** Bean'in ihtiyaç duyduğu bağımlılıklar enjekte edilir.
3. **Farkındalık (Aware) Arayüzleri:** Bean, çalıştığı Spring ortamı hakkında bilgi alır (`BeanNameAware`, `ApplicationContextAware` vb.).
4. **Ön Başlatma:** `@Value` ile işaretlenmiş alanlar doldurulur, `BeanPostProcessor` öncesi adımlar çalışır.
5. **Başlatma (Initialization):** Bağımlılıklar tamamlandıktan sonra `@PostConstruct` metodu çalıştırılır.
6. **Proxy Oluşturma:** `@Transactional`, `@Async` gibi anotasyonlar varsa Spring Boot proxy nesnesini bu aşamada oluşturur.
7. **Kullanıma Hazır:** Bean uygulama genelinde kullanılabilir duruma gelir.
8. **Yok Etme (Destruction):** Uygulama sonlandığında Bean elindeki kaynakları kapatır (`@PreDestroy`).

* **Bean Scope Türleri:**
* **Singleton:** Uygulama boyunca bean'in tek bir örneği oluşturulur; her enjeksiyonda aynı instance verilir.
* **Prototype:** Bean her çağrıldığında yeni bir instance üretilir. Stateful yapılarda tercih edilir.
* **Session:** Her web oturumu (HTTP Session) için ayrı bir bean örneği oluşturulur. Oturum kapanana kadar aktif kalır (örn. e-ticaret sepet mekanizmaları).

---

### 8. Spring Boot – @Transactional ve Propagation Türleri

**Soru:** @Transactional annotation'ı nasıl çalışır? Propagation türlerinden (REQUIRED, REQUIRES_NEW, NESTED vb.) örnek vererek bahset.

* **Çalışma Prensibi:** Metoda uygulandığında Spring AOP ile proxy nesnesi oluşturulur. Metot hatasız biterse işlem commit edilir, hata fırlatılırsa rollback yapılır.
* **Propagation Türleri:**
* **REQUIRED:** Aktif bir transaction varsa ona dahil olur; yoksa yeni bir transaction başlatır. Çok adımlı akışlarda kullanılır, alt adım patlarsa üst adım da rollback olur.
* **REQUIRES_NEW:** Mevcut bir transaction olsa dahi onu askıya alır ve tamamen bağımsız yeni bir transaction açar. Ana işlem hata alsa bile mutlak çalışması gereken durumlarda (örn. audit/log yazma) kullanılır.
* **NESTED:** Var olan transaction'a katılır ancak bir `Savepoint` oluşturur. Hata durumunda sadece savepoint noktasına rollback yapar; ana işlemi tamamen patlatmaz.
* **SUPPORTS:** Aktif transaction varsa katılır, yoksa transaction olmadan salt okuma mantığıyla çalışır.
* **MANDATORY:** Mutlaka aktif bir transaction bekler; yoksa exception fırlatır, kendisi transaction açmaz.
* **NEVER:** Aktif bir transaction altında çağrılırsa hata fırlatır. Uzun süren ve veritabanını kilitlememesi gereken işlerde kullanılır.

---

### 9. Mimari – Monolitik vs Mikroservis & Hesap Açma Servisi

**Soru:** Monolitik mimari ile mikroservis mimarisi arasındaki temel farklar nelerdir? Bir bankanın "hesap açma" servisini hangi mimariyle kurgularsın, neden?

* **Monolitik:** Hızlı geliştirme ve başlangıçta düşük maliyet sağlar. Ancak yüksek ölçeklemede bakım maliyeti artar ve bir modüldeki çökme tüm sistemi etkileyebilir.
* **Mikroservis:** İlk geliştirme maliyeti yüksektir, dağıtık mimari karmaşıktır. Ancak bağımsız ölçeklenebilirlik sağlar; bir servis çöktüğünde diğer servisler ayakta kalarak hatanın yayılmasını önler.
* **Tasarım Tercihi:** Hesap açma servisini **mikroservis** olarak kurgularım. Günde binlerce hesap açma isteği gelebilir. Bu serviste yaşanacak olası bir darboğaz veya çökmenin ana bankacılık sistemini, para transferlerini ve mevcut müşterilerin işlemlerini kilitlememesi (hata izolasyonu) gerekir.

---

### 10. İletişim – Senkron vs Asenkron İletişim & EFT/Havale Akışı

**Soru:** Senkron iletişim (REST/gRPC) ile asenkron iletişim (Kafka/RabbitMQ) arasındaki fark nedir? Bir EFT/Havale işleminde hangi iletişim yolunu tercih edersin?

* **İletişim Protokolleri:**
* **REST:** HTTP/1.1 üzerinden JSON gibi metin tabanlı, insan tarafından kolay okunabilir formatlarla çalışır.
* **gRPC:** HTTP/2 üzerinden binary (Protobuf) formatında iletişim kurar; serileştirme maliyeti düşüktür ve kod üretim desteği sunar.
* **RabbitMQ:** Kafka'ya kıyasla daha hafif senaryolar ve düşük-orta hacimli trafik için uygundur. Varsayılanda mesaj saklama garantisi Kafka gibi uzun vadeli değildir.

* **Geri Bildirim / Tamamlama Notu:**
* Kafka'dan bahsedilmedi ve EFT/Havale tercihi eksik kaldı.
* **Beklenen Yaklaşım:** EFT/Havale işleminde hibrit model uygulanır. Kullanıcı arayüzüne hızlı yanıt dönmek için istek senkron (REST/gRPC) başlatılır ("İşlem alındı" statüsü ile).
* Arka plandaki bakiye düşümü, bankalar arası iletişim ve defter güncellemeleri asenkron kuyruklar (Kafka) üzerinden event-driven olarak işletilir. Bu sayede veri kaybı önlenir ve guaranteed delivery sağlanır.

---

### 11. Senaryo – Eş Zamanlı Çift Para Çekme & Concurrency Kontrolü

**Senaryo:** Bakiye: 1000 TL. Mobil ve ATM'den neredeyse eş zamanlı 800'er TL çekme isteği geliyor. Sistem ikisine birden izin vermemeli.

* **Kilitleme Stratejisi:**
* **Pessimistic Locking** tercih edilir.
* *Gerekçe:* Optimistic lock thread-safe koruma garantisini tek başına sağlamaz; aynı anda okuyan iki thread çakışabilir. 1. thread işlemi bitirmeden 2. thread'in bakiyeyi değiştirmesine izin verilmemelidir.

* **JPA/Hibernate Uygulaması:**
* Repository üzerinde: `@Lock(LockModeType.PESSIMISTIC_WRITE)` ve kilit süresi aşımında hata fırlatması için `@QueryHints` tanımlanır.
* Service katmanında: `@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)` kullanılır.

* **Sistemik Etkiler ve Çözümler:**
* Kilitleme sadece ilgili `account_id` satırına uygulanır, diğer kullanıcıları etkilemez.
* Aynı hesap üzerinde yoğunluk varsa veritabanı connection havuzunda beklemeler ve darboğaz oluşabilir.
* Karşılıklı hesap transferlerinde deadlock riski doğabilir.
* **Yüksek Hacimli Çözüm:** Veritabanını yormamak için kilit mekanizması Redis (Distributed Lock - Redlock) seviyesine çekilebilir veya Kafka partition anahtarı olarak `account_id` verilerek aynı hesaba ait işlemlerin sıralı (single-threaded consumer) işlenmesi sağlanabilir.

---

### 12. Algoritma – Two Sum Problemi

```java
public void target(int[] array, int target) {
    Map<Integer, Integer> map = new HashMap<>();

    for (int i = 0; i < array.length; i++) {
        int complement = target - array[i];

        // Aradığımız tamamlayıcı sayı daha önce haritaya eklendi mi?
        if (map.containsKey(complement)) {
            System.out.println("Bulunan İndeksler: " + map.get(complement) + " ve " + i);
            return; // Çifti bulunca çıkmak için
        }

        // Bulunamadıysa mevcut sayıyı ve indeksini haritaya ekle
        map.put(array[i], i);
    }
}

```

---

### 13. SQL – Son 30 Günde En Az 5 İşlem Yapan Müşteriler

**Şema:**

* `CUSTOMER (customer_id, name, created_at)`
* `ACCOUNT (account_id, customer_id, balance, account_type)`
* `TRANSACTION (transaction_id, account_id, amount, transaction_date, transaction_type)`

```sql
SELECT c.name, SUM(t.amount) AS total_amount 
FROM Customer AS c
INNER JOIN Account a ON a.customer_id = c.customer_id
INNER JOIN Transaction t ON t.account_id = a.account_id
WHERE t.transaction_date >= NOW() - INTERVAL '30 day'
GROUP BY c.customer_id, c.name
HAVING COUNT(t.transaction_id) >= 5
ORDER BY total_amount DESC;

```
