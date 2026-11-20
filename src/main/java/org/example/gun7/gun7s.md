# Gün 7 – Mülakat Hazırlık Soruları ve Çözümleri

---

### 1. Java – try-with-resources Yapısı ve finally Karşılaştırması

**Soru:** Java – `try-with-resources` nedir, neden `finally` bloğunda manuel kapatmaktan daha güvenlidir?

* **Cevap:**
  * **try-with-resources Nedir?:**
    * Java 7 ile tanıtılan, `AutoCloseable` veya `Closeable` arayüzünü uygulayan kaynakların (dosya akışları, veritabanı bağlantıları, soketler vb.) işlem bitiminde otomatik olarak kapatılmasını sağlayan bir dil yapısıdır.
    * Geleneksel `try-catch-finally` bloğundaki `finally` yazma zorunluluğunu ortadan kaldırır. Kaynaklar try parantezi içinde tanımlanır: `try (Resource res = new Resource()) { ... }`.
  * **Manuel `finally` Kapatmasına Göre Güvenlik ve Avantajları:**
    * **Bastırılmış İstisnalar (Suppressed Exceptions) Yönetimi:**
      * Klasik `try-finally` bloğunda, hem `try` bloğu hem de `finally` içindeki `close()` metodu hata fırlatırsa, `finally` bloğundaki hata `try` bloğundaki asıl kök hatayı ezer ve tamamen yutar (masking/swallowing). Bu durum hata ayıklamayı zorlaştırır.
      * `try-with-resources` yapısında `try` bloğundaki hata ana istisna (primary exception) olarak korunur; `close()` metodunun fırlattığı hatalar ise ana istisnaya `Throwable.addSuppressed()` mekanizmasıyla bastırılmış hata olarak eklenir ve hiçbir hata kaybolmaz.
    * **Çoklu Kaynak Yönetimi ve Sıralama Garantisi:**
      * Klasik `finally` bloğunda birden fazla kaynak ardışık kapatılırken ilk kaynağın `close()` metodu hata fırlatırsa kod akışı kesilir ve sonraki kaynaklar kapatılamaz (kaynak sızıntısı - resource leak).
      * `try-with-resources` yapısında birden fazla kaynak tanımlandığında, kaynaklar açılış sırasının tam tersi yönünde kapatılır. Bir kaynağın kapanırken hata atması, diğer kaynakların kapatılmasını engellemez.
    * **Null Güvenliği (Null-Safety):**
      * Manuel `finally` bloğunda kaynak nesnesinin `null` olup olmadığı (`if (res != null)`) geliştirici tarafından açıkça kontrol edilmelidir; aksi takdirde `NullPointerException` meydana gelir.
      * `try-with-resources` bu kontrolü arka planda otomatik olarak gerçekleştirir.
    * **Daha Sade ve Okunabilir Kod (Boilerplate Azaltımı):**
      * İç içe geçmiş (nested) `try-finally` bloklarını ve gereksiz kod kalabalığını ortadan kaldırır.

```java
// Güvenli try-with-resources kullanımı
try (BufferedReader br = new BufferedReader(new FileReader("dosya.txt"));
     Connection conn = dataSource.getConnection()) {
    // İş kuralları yürütülür
} catch (IOException | SQLException e) {
    // Suppressed istisnalar e.getSuppressed() ile incelenebilir
    log.error("İşlem hatası", e);
}
```

---

### 2. Java – Functional Interface & Temel Arayüzler

**Soru:** Java – Functional interface nedir? `Function`, `Predicate`, `Supplier`, `Consumer` arasındaki farkları örnekle açıkla.

* **Cevap:**
  * **Functional Interface (Fonksiyonel Arayüz) Nedir?:**
    * Bünyesinde yalnızca **tek bir soyut (abstract) metot** barındıran arayüzlerdir (Single Abstract Method - SAM kuralı).
    * İçerisinde istenildiği kadar `default` veya `static` metot bulunabilir; bu durum kuralı bozmaz.
    * `@FunctionalInterface` anotasyonu zorunlu değildir ancak derleyicinin tek soyut metot kuralını denetlemesini sağlar.
    * Java 8 ile Lambda ifadeleri ve Metot Referanslarının (Method Reference) hedef türü (target type) olarak kullanılmak üzere getirilmiştir. İsimsiz iç sınıfların (anonymous inner class) yarattığı bellek ve sınıf yükleme maliyetini `invokedynamic` bayt kodu talimatıyla optimize eder.
  * **Dört Temel Fonksiyonel Arayüz:**
    * **`Predicate<T>`:**
      * Metot İmzası: `boolean test(T t)`
      * Bir girdi alır, mantıksal bir koşula göre test eder ve geriye `boolean` (`true`/`false`) döner.
      * Kullanım Alanı: Koleksiyon filtreleme işlemleri (`Stream.filter`).
    * **`Consumer<T>`:**
      * Metot İmzası: `void accept(T t)`
      * Bir girdi alır, üzerinde bir işlem gerçekleştirir ancak geriye hiçbir değer döndürmez (`void`).
      * Kullanım Alanı: Veriyi yazdırma, loglama, kuyruğa mesaj atma, veritabanına kayıt (`Stream.forEach`).
    * **`Supplier<T>`:**
      * Metot İmzası: `T get()`
      * Hiçbir girdi almaz; yalnızca talep edildiğinde yeni bir değer veya nesne üretip döner.
      * Kullanım Alanı: Tembel değerlendirme (lazy evaluation), varsayılan nesne üretimi (`Optional.orElseGet`, `Optional.orElseThrow`).
    * **`Function<T, R>`:**
      * Metot İmzası: `R apply(T t)`
      * `T` tipinde bir girdi alır, dönüştürme mantığını uygular ve `R` tipinde bir çıktı üretir.
      * Kullanım Alanı: DTO ve tip dönüşümleri, veri eşleme (`Stream.map`).

```java
// 1. Predicate: Fiyatı 1000'den büyük mü kontrolü
Predicate<Product> isExpensive = product -> product.getPrice() > 1000;

// 2. Consumer: Ürünü loglama işlemi
Consumer<Product> logProduct = product -> System.out.println("İşlenen ürün: " + product.getName());

// 3. Supplier: Ürün bulunamadığında varsayılan nesne üretimi
Supplier<Product> defaultProductSupplier = () -> new Product("Varsayılan", 0.0);

// 4. Function: Product nesnesini ProductDTO nesnesine dönüştürme
Function<Product, ProductDTO> toDto = product -> new ProductDTO(product.getId(), product.getName());

// Örnek Kullanım:
Product product = productRepository.findById(1L)
    .orElseGet(defaultProductSupplier);

if (isExpensive.test(product)) {
    logProduct.accept(product);
}

ProductDTO dto = toDto.apply(product);
```

---

### 3. Java – volatile Anahtar Kelimesi

**Soru:** Java – `volatile` anahtar kelimesi ne işe yarar? Çoklu thread ortamında neden gereklidir?

* **Cevap:**
  * **Görünürlük (Visibility) Sorunu:**
    * Modern CPU mimarilerinde her thread, performansı artırmak amacıyla ana bellekteki (RAM / Main Memory) ortak değişkenlerin değerlerini kendi CPU önbelleklerine (L1/L2/L3 Cache veya register) kopyalar.
    * Bir thread değişkende güncelleme yaptığında, bu değişiklik derhal ana belleğe yazılmayabilir. Diğer thread'ler kendi önbelleklerindeki eski (bayat/stale) veriyi okumaya devam eder. Bu durum çoklu thread ortamlarında veri uyumsuzluklarına yol açar.
  * **volatile'ın Sağladığı Garantiler:**
    * **Doğrudan Bellek Erişimi:** `volatile` olarak tanımlanan bir alana yapılan her yazma işlemi doğrudan ana belleğe yazılır; her okuma işlemi de CPU önbelleği atlanarak doğrudan ana bellekten okunur.
    * **Happens-Before İlişkisi:** Bir thread'in volatile değişkene yazdığı değer, sonrasında o değişkeni okuyan tüm thread'ler tarafından anında görünür hale gelir.
    * **Komut Yeniden Sıralama Koruması (Instruction Reordering Prevention):** Derleyici ve işlemci optimizasyon amaçlı komut sıralarını değiştirebilir. `volatile`, bellek bariyerleri (memory barrier) ekleyerek volatile okuma/yazma etrafındaki komutların sıralamasının bozulmasını engeller.
  * **Önemli Kısıtlama (Atomisite Eksikliği):**
    * `volatile` yalnızca **görünürlüğü (visibility)** garanti eder; **atomisiteyi (atomicity)** garanti etmez.
    * Örneğin `count++` işlemi aslında 3 adımdan oluşur (oku, artır, yaz - read-modify-write). İki thread aynı anda `count++` yaparsa yarış durumu (race condition) oluşur. Atomik işlemler için `AtomicInteger` sınıfı veya `synchronized` blokları kullanılmalıdır.

```java
public class Worker implements Runnable {
    // Tüm thread'ler stop flag'inin güncel değerini anında görür
    private volatile boolean running = true;

    public void run() {
        while (running) {
            // İş akışı yürütülür
        }
    }

    public void stop() {
        this.running = false; // Doğrudan ana belleğe yazılır
    }
}
```

---

### 4. Java – Enum Kullanımının Avantajları

**Soru:** Java – Enum kullanmanın avantajları nelerdir? Bir hesabın durumunu (`ACTIVE`, `FROZEN`, `CLOSED`) `String` yerine enum ile tutmanın faydası nedir?

* **Cevap:**
  * **Derleme Zamanı Tip Güvenliği (Compile-Time Type Safety):**
    * `String` kullanımında `"ACTİVE"`, `"aktif"`, `"ACTV"` gibi yazım hataları veya geçersiz değerler derleme anında anlaşılamaz; sistem çalışma zamanında (runtime) beklenmedik hatalar üretir.
    * `Enum` sabitleri derleme zamanında katı biçimde denetlenir; tanımlı olmayan bir değerin atanmasına izin verilmez.
  * **Referans Eşitliği ve Null Güvenliği (`==` Operatörü):**
    * `String` karşılaştırmalarında `equals()` kullanılmalıdır ve sol taraf null olduğunda `NullPointerException` riski taşır (`status.equals("ACTIVE")`).
    * Enum sabitleri JVM seviyesinde tekil (singleton) nesneler olduğu için güvenle `==` operatörü ile karşılaştırılabilir (`account.getStatus() == AccountStatus.ACTIVE`). Eşitliğin sağ veya sol tarafı `null` olsa dahi NPE fırlatmaz, güvenle `false` döner.
  * **Davranış ve Kural Barındırabilme Yeteneği:**
    * Java enum'ları yalnızca sabit listesi değildir; bünyelerinde alanlar (fields), kurucular (constructors), metotlar ve soyut davranışlar tanımlanabilir. Durum geçiş kuralları doğrudan enum içine yerleştirilebilir.
  * **Üstün Performanslı Koleksiyonlar (`EnumSet` ve `EnumMap`):**
    * Enum tipleri için özel olarak tasarlanan `EnumSet` arka planda bit vektörleri (bitmask) kullanır; `HashSet`'e kıyasla çok daha düşük bellek tüketir ve işlemci seviyesinde son derece hızlı çalışır.
    * `EnumMap` ise dahili olarak bir dizi (array) yapısı kullanır; karma tabanlı `HashMap`'e göre çok daha performanslıdır.
  * **JVM Tarafından Sağlanan Singleton ve Serileştirme Güvencesi:**
    * Enum'lar JVM tarafından reflection ve deserialization saldırılarına karşı korunur; her sabitin bellekte yalnızca tek bir örneği bulunması kesin olarak garanti edilir.

```java
public enum AccountStatus {
    ACTIVE,
    FROZEN,
    CLOSED;

    public boolean canWithdraw() {
        return this == ACTIVE;
    }
}
```

---

### 5. Java – record Tipleri (Java 16+)

**Soru:** Java – Java'da record nedir (Java 16+)? Normal bir class yerine ne zaman record tercih edilir?

* **Cevap:**
  * **Record Nedir?:**
    * Yalnızca veri taşımak amacıyla kullanılan, değişmez (immutable) veri taşıyıcı sınıflar tanımlamak için Java 16 ile standartlaşan özel bir sınıf türüdür.
    * Geleneksel sınıflardaki tüm alanlar örtük olarak `private final` olarak tanımlanır.
    * Derleyici arka planda canonical constructor, bileşen adlarıyla aynı isimde getter metotları (`name()` gibi; `getName()` yerine), `equals()`, `hashCode()` ve `toString()` metotlarını otomatik olarak üretir.
    * Örtük olarak `java.lang.Record` sınıfından türer; bu nedenle başka bir sınıfı `extends` edemez ve kendisi de `final` olduğu için kalıtılamaz. Ancak arayüzleri (`implements`) uygulayabilir.
  * **Kompakt Kurucu (Compact Constructor) Yeteneği:**
    * Parametre listesi tekrar yazılmadan doğrudan doğrulama (validation) ve veri normalizasyonu yapılabilir:
      ```java
      public record AccountDto(String iban, BigDecimal balance) {
          public AccountDto {
              if (balance.compareTo(BigDecimal.ZERO) < 0) {
                  throw new IllegalArgumentException("Bakiye negatif olamaz");
              }
          }
      }
      ```
  * **Normal Class Yerine Ne Zaman Record Tercih Edilir?:**
    * **DTO (Data Transfer Object) Tanımlamaları:** Katmanlar arası veya API istek/yanıt (Request/Response) modellerinde veri değişmezliğini garanti etmek için.
    * **Bileşik Anahtarlar (Composite Keys):** Map anahtarı veya Set elemanı olarak kullanılacak birden fazla alanı gruplamak için (`equals` ve `hashCode` otomatik ve tutarlı üretildiği için).
    * **Geçici Veri Gruplamaları:** Bir metottan birden fazla değer dönmek gerektiğinde veya stream operasyonları içinde ara veri yapıları oluşturmak için.
  * **Ne Zaman Tercih Edilmez?:**
    * JPA Entity olarak record kullanımı uygun değildir. Çünkü JPA spesifikasyonu parametresiz varsayılan kurucu (no-arg constructor), dinamik proxy oluşturma ve alanların mutasyonu gibi dinamik davranışlar gerektirir.

---

### 6. Hibernate – EntityManager ile Session Arasındaki Fark

**Soru:** Hibernate – `EntityManager` ile `Session` arasındaki fark nedir? (JPA standardı vs Hibernate'e özgü API)

* **Cevap:**
  * **`EntityManager` (JPA Standardı):**
    * Jakarta Persistence API (JPA) spesifikasyonunun resmi arayüzüdür.
    * Sağlayıcıdan bağımsızdır (vendor-independent). Kod tabanını Hibernate'e sıkı sıkıya bağlamaz; gerektiğinde EclipseLink veya OpenJPA gibi diğer JPA sağlayıcılarına geçişi kolaylaştırır.
    * Standart JPA yaşam döngüsü metotlarını içerir: `persist()`, `merge()`, `remove()`, `find()`.
  * **`Session` (Hibernate Core API):**
    * Hibernate'in tescilli (proprietary) ve orijinal oturum arayüzüdür.
    * Hibernate `Session` arayüzü, JPA standardı olan `EntityManager` arayüzünü genişletir (`Session implements EntityManager`).
    * Standart JPA'da bulunmayan Hibernate'e özgü gelişmiş yetenekleri barındırır:
      * `saveOrUpdate()`, `evict()`, `replicate()`, `refresh()` gibi ek yaşam döngüsü metotları.
      * Gelişmiş filtreleme (`enableFilter`), batch yapılandırması ve doğrudan JDBC bağlantı erişimi.
      * Hibernate'e özgü sorgulama motorları ve Criteria yapılandırmaları.
  * **Birlikte Kullanım:**
    * Modern Spring Boot uygulamalarında standart olarak `EntityManager` kullanılır.
    * İhtiyaç duyulduğunda `entityManager.unwrap(Session.class)` metodu çağrılarak alttaki Hibernate `Session` nesnesine güvenle erişilebilir:
      ```java
      Session session = entityManager.unwrap(Session.class);
      session.setDefaultReadOnly(true);
      ```

---

### 7. Hibernate – @Transactional Metotlarında Self-Invocation Problemi

**Soru:** Hibernate – `@Transactional` işaretli bir metodu aynı sınıf içinden çağırmak (self-invocation) neden beklenmedik davranışa yol açar? Bunun nedeni nedir (proxy mekanizması)?

* **Cevap:**
  * **Temel Neden (Spring AOP Proxy Mekanizması):**
    * Spring, `@Transactional` anotasyonunu Spring AOP dinamik vekil nesneleri (Dynamic Proxy - CGLIB veya JDK Dynamic Proxy) aracılığıyla yürütür.
    * Dışarıdan bir servis bu bean'i çağırdığında istek doğrudan hedef sınıfa değil, aradaki Proxy nesnesine gelir. Proxy nesnesi veritabanı transaction'ını başlatır (`beginTransaction`), asıl metodu çağırır ve metot bittiğinde duruma göre `commit` ya da `rollback` uygular.
    * Ancak bir metodun içinden aynı sınıfın başka bir `@Transactional` metodu çağrıldığında (`this.saveTransaction()`), çağrı **hedef nesnenin içinden yerel olarak** gerçekleşir. Araya giren bir Proxy nesnesi olmadığı için Spring'in interceptor mekanizması devreye giremez ve transaction sınırları uygulanmaz.
  * **Beklenmeyen Sonuçlar:**
    * Yeni bir transaction açılmaz veya mevcut transaction yayılma kuralları (`Propagation.REQUIRES_NEW` vb.) tamamen yok sayılır.
    * Hata durumunda rollback mekanizması beklenen şekilde tetiklenmez.
  * **Çözüm Yolları:**
    * **Sorumlulukların Ayrılması (En Doğru Yaklaşım):** Transaction gerektiren metodu bağımsız bir servis sınıfına taşımak ve ana sınıfa `@Autowired` ile enjekte etmek.
    * **Kendini Enjekte Etme (Self-Injection):** Sınıfın kendi proxy nesnesini `@Lazy` veya `ObjectProvider` ile enjekte edip çağrıyı bu referans üzerinden yapmak (`self.saveTransaction()`).
    * **Programatik Transaction Yönetimi:** `TransactionTemplate` kullanarak transaction sınırlarını açıkça kod bloklarıyla yönetmek.

---

### 8. Hibernate – Bidirectional İlişkilerde JSON Sonsuz Döngüsü

**Soru:** Hibernate – Bidirectional (`@OneToMany` / `@ManyToOne`) bir ilişkide, entity'yi doğrudan JSON'a serileştirirken sonsuz döngü (infinite recursion) neden oluşur? Nasıl önlersin?

* **Cevap:**
  * **Sonsuz Döngünün Nedeni:**
    * Çift yönlü bir ilişkide `Department` entity'si içinde `List<Employee>`, `Employee` entity'si içinde ise bir `Department` referansı bulunur.
    * Jackson (veya başka bir JSON serileştirici) `Department` nesnesini serileştirirken `employees` listesine girer; her `Employee` nesnesini serileştirirken onun `department` alanını bulur; tekrar departmana dönüp yine çalışanları serileştirmeye çalışır.
    * Bu karşılıklı döngü bellek tükenene kadar devam eder ve sistem `StackOverflowError` hatası fırlatır.
  * **Önleme Yöntemleri:**
    * **1. DTO Kullanımı (Endüstri Standardı ve Kesin Çözüm):**
      * Veritabanı entity nesneleri asla doğrudan Controller katmanından dışarıya açılmamalıdır.
      * İhtiyaç duyulan verileri içeren `DepartmentDto` ve `EmployeeSummaryDto` gibi düz nesneler oluşturularak (MapStruct vb. ile) yalnızca gerekli alanlar serileştirilir.
    * **2. `@JsonManagedReference` ve `@JsonBackReference` Kullanımı:**
      * İlişkinin ana/üst tarafına (koleksiyon sahibi `Department`) `@JsonManagedReference`, çocuk/bağlı tarafına (`Employee.department`) `@JsonBackReference` eklenir.
      * Jackson üst nesneyi serileştirirken çocukları yazar, ancak çocuktan geri üst nesneye giden köprüyü serileştirmeyerek döngüyü keser.
    * **3. `@JsonIgnore` Kullanımı:**
      * Döngüye sebep olan alanlardan birinin (örneğin `Employee.department`) üzerine `@JsonIgnore` konularak JSON çıktısından tamamen hariç tutulması sağlanır.
    * **4. `@JsonIdentityInfo` Kullanımı:**
      * Sınıf seviyesine eklenerek nesnelerin kimlikleriyle (ID) referanslanması sağlanır; aynı nesne tekrar serileştirilmek istendiğinde yalnızca ID'si yazılarak döngü engellenir.

---

### 9. Hibernate – Flush Mantığı ve FlushMode Türleri

**Soru:** Hibernate – Flush nedir, `FlushMode` (`AUTO`, `COMMIT`, `MANUAL`) ne anlama gelir? Hibernate bir sorgu çalıştırmadan önce ne zaman otomatik flush yapar?

* **Cevap:**
  * **Flush Nedir?:**
    * Persistence Context (1. seviye önbellek) içerisindeki entity durumlarının veritabanı ile senkronize edilmesi işlemidir.
    * Bellekte bekleyen `INSERT`, `UPDATE`, `DELETE` SQL komutlarının veritabanına iletilmesini sağlar.
    * **Önemli Ayrım:** Flush işlemi veritabanı seviyesinde `COMMIT` anlamına gelmez. Gönderilen SQL'ler transaction bitene kadar veritabanında bekletilir; hata olursa geri alınabilir (rollback).
  * **FlushMode Türleri:**
    * **`FlushMode.AUTO` (Varsayılan):**
      * Hibernate flush zamanlamasını kendisi yönetir. Transaction commit anında VEYA Persistence Context'teki bekleyen değişiklikleri etkileyebilecek bir sorgu çalıştırılmadan hemen önce otomatik flush yapar.
    * **`FlushMode.COMMIT`:**
      * Flush yalnızca veritabanı transaction'ı `commit` edildiğinde gerçekleşir.
      * Araya giren ara sorgularda flush yapılmaz; bu nedenle ara sorgularda henüz veritabanına yansıtılmamış en güncel değişiklikler okunamayabilir (stale data). Ancak yoğun yazma işlemlerinde performansı artırır.
    * **`FlushMode.MANUAL`:**
      * Hibernate otomatik flush mekanizmasını tamamen kapatır. Flush yalnızca geliştirici açıkça `session.flush()` çağrısı yaptığında gerçekleşir.
      * `@Transactional(readOnly = true)` kullanıldığında Hibernate arka planda benzer bir optimizasyon uygular.
  * **Hibernate Sorgu Öncesi Ne Zaman Otomatik Flush Yapar?:**
    * `FlushMode.AUTO` modundayken, çalıştırılacak yeni bir JPQL/HQL veya Native SQL sorgusunun hedef aldığı tablolar üzerinde, o ana kadar bellekte bekleyen (henüz DB'ye gönderilmemiş) değişiklikler varsa, sorgunun tutarlı ve güncel veriyi okuyabilmesi için sorgudan hemen önce otomatik flush çalıştırılır.

---

### 10. Spring Boot – Bileşen Anotasyonları (@Component, @Service, @Repository, @Controller)

**Soru:** Spring Boot – `@Component`, `@Service`, `@Repository`, `@Controller` annotation'ları teknik olarak aynı işi mi yapar? Aralarındaki fark (varsa) nedir, neden farklı isimler kullanılır?

* **Cevap:**
  * **Teknik Ortak Nokta:**
    * Hepsi birer Spring bean'i tanımlamak için kullanılır.
    * `@Service`, `@Repository` ve `@Controller` anotasyonlarının kaynak kodları incelendiğinde, hepsinin üzerinde meta-annotation olarak `@Component` tanımlandığı görülür.
    * Spring'in bileşen tarayıcısı (`Component Scanning`), bu dört anotasyonu da tarar ve ilgili sınıflardan birer bean üreterek IoC Container (ApplicationContext) içine kaydeder.
  * **Semantik ve Teknik Farklılıklar:**
    * **`@Component`:**
      * Genel amaçlı bileşendir. Özel bir mimari katmana ait olmayan yardımcı sınıflar (utility), dönüştürücüler ve genel konfigürasyon nesneleri için kullanılır.
    * **`@Service`:**
      * İş mantığı (business logic) katmanını temsil eder.
      * Doğrudan ek bir teknik davranış kazandırmaz; ancak katmanlı mimaride sınıfın amacını açıkça belirtir ve AOP pointcut'ları (örneğin işlem süresi loglama) yazarken servis katmanını hedeflemeyi kolaylaştırır.
    * **`@Repository` (Belirgin Teknik Fark):**
      * Veri erişim katmanını (DAO / Repository) temsil eder.
      * **Teknik Ekstra Özellik:** Spring'in `PersistenceExceptionTranslationPostProcessor` mekanizmasını tetikler. Veritabanına özgü alt seviye denetimli ve denetimsiz istisnaları (JDBC `SQLException`, Hibernate istisnaları), Spring'in ortak ve tutarlı `DataAccessException` istisna hiyerarşisine otomatik olarak çevirir (Exception Translation).
    * **`@Controller` / `@RestController`:**
      * Sunum ve web katmanını temsil eder. HTTP isteklerini karşılar, parametreleri ayrıştırır ve uygun yanıtı döner.
      * `@RestController`, `@Controller` ve `@ResponseBody` anotasyonlarının birleşimidir; dönen nesnelerin otomatik olarak HTTP yanıt gövdesine (JSON/XML) serileştirilmesini sağlar.

---

### 11. Spring Boot – Auto-Configuration Mekanizması & Starter Web

**Soru:** Spring Boot – "Auto-configuration" nedir? `spring-boot-starter-web` bağımlılığını eklediğinde arka planda neler otomatik yapılandırılır?

* **Cevap:**
  * **Auto-Configuration Nedir?:**
    * Spring Boot'un classpath'teki kütüphanelere, ortam ayarlarına (`application.properties`/`yml`) ve mevcut bean tanımlarına bakarak uygulamanın ihtiyaç duyduğu konfigürasyonları otomatik olarak ayağa kaldırması mekanizmasıdır (`@EnableAutoConfiguration`).
    * Bu mekanizma koşullu anotasyonlar (`@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`) ile yönetilir; geliştirici kendi bean'ini tanımladığında otomatik yapılandırma sessizce devreden çıkar.
  * **`spring-boot-starter-web` Eklendiğinde Arka Planda Yapılandırılan Temel Bileşenler:**
    * **1. Gömülü Web Sunucusu (Embedded Tomcat):**
      * Classpath'te Tomcat bulunduğu için `TomcatServletWebServerFactory` devreye girer; harici bir web sunucusu kurulumuna gerek kalmadan varsayılan olarak `8080` portunda gömülü Tomcat başlatılır.
    * **2. DispatcherServlet & Servlet Ortamı:**
      * Spring MVC'nin ön kontrolcüsü (Front Controller) olan `DispatcherServlet` ve onu servlet konteynerine bağlayan `DispatcherServletRegistrationBean` oluşturulur. Gelen tüm HTTP istekleri bu merkez servise yönlendirilir.
    * **3. WebMvcAutoConfiguration:**
      * İstek yönlendirmelerini yöneten `RequestMappingHandlerMapping` ve controller metotlarını parametrelerle çalıştıran `RequestMappingHandlerAdapter` ayağa kaldırılır. Statik dosya yolları (`/static`, `/public`) otomatik konfigüre edilir.
    * **4. JSON Serileştirme Motoru (Jackson):**
      * Classpath'te Jackson yer aldığı için `JacksonAutoConfiguration` çalışır ve varsayılan bir `ObjectMapper` bean'i üretilir.
      * HTTP istek ve yanıt gövdelerini (`@RequestBody`, `@ResponseBody`) Java nesneleri ile JSON arasında dönüştürmek için `MappingJackson2HttpMessageConverter` sisteme dahil edilir.
    * **5. Bean Validation Desteği:**
      * Classpath'te Hibernate Validator mevcutsa `ValidationAutoConfiguration` devreye girer; DTO'lar üzerindeki `@NotNull`, `@Size`, `@Email` gibi kısıtlamaları denetleyen `LocalValidatorFactoryBean` oluşturulur.

---

### 12. Spring Boot – @Value ile @ConfigurationProperties Karşılaştırması

**Soru:** Spring Boot – `@Value` ile bir property okumak ile `@ConfigurationProperties` kullanmak arasındaki fark nedir? Hangi durumda hangisini tercih edersin?

* **Cevap:**
  * **`@Value`:**
    * Basit ve tekil konfigürasyon değerlerini alan bazında enjekte etmek için kullanılır (`@Value("${app.jwt.secret}")`).
    * SpEL (Spring Expression Language) ifadelerini destekler.
    * Tip güvenliği (type-safety) zayıftır; hata durumunda çalışma zamanında patlar.
    * Gevşek bağlama (relaxed binding: `kebab-case`, `camelCase`, `SNAKE_CASE` eşleşmesi) desteği sınırlıdır.
    * JSR-380 Bean Validation desteğini doğrudan sınıf seviyesinde uygulayamaz.
  * **`@ConfigurationProperties`:**
    * Belirli bir önek (prefix) altındaki hiyerarşik yapılandırmaları tip güvenli (type-safe) bir POJO veya `record` sınıfına bağlar (`@ConfigurationProperties(prefix = "app.mail")`).
    * Gevşek bağlama yeteneğine tam sahiptir (`app.mail.host-name`, `app.mail.hostName`, `app.mail.host_name` aynı alana eşleşir).
    * `@Validated` anotasyonu ile birlikte kullanılarak uygulamanın açılış anında ayarların doğruluğunu denetleyebilir (fail-fast).
    * IDE'lerde otomatik tamamlama ve metadata desteği sunar.
  * **Tercih Kriteri:**
    * Tekil, bağımsız ve geçici bir ayar okumak gerektiğinde `@Value` tercih edilebilir.
    * Birbiriyle ilişkili, hiyerarşik, üretim ortamlarında doğrulanması kritik olan ve birden fazla ayar barındıran tüm kurumsal yapılandırmalarda kesinlikle `@ConfigurationProperties` tercih edilmelidir.

---

### 13. Spring Boot – Servlet Filter ile HandlerInterceptor Farkı

**Soru:** Spring Boot – Servlet Filter ile Spring'in HandlerInterceptor'ı arasındaki fark nedir? Hangi katmanda, hangi sırada çalışırlar?

* **Cevap:**
  * **Servlet Filter:**
    * **Bulunduğu Katman:** Servlet Konteyneri (Tomcat vb.) seviyesinde, Spring MVC'nin dışında yer alır.
    * **Çalışma Zamanı:** İstek `DispatcherServlet`'e ulaşmadan önce ve yanıt istemciye gitmeden hemen önce çalışır.
    * **Bilgi Kapsamı:** Düşük seviyelidir. İsteği hangi Controller veya metodun işleyeceğini bilmez; yalnızca ham `HttpServletRequest` ve `HttpServletResponse` nesnelerine erişir.
    * **Temel Kullanım:** Spring Security (kimlik doğrulama), CORS yönetimi, istek gövdesini (request body) önbelleğe alma/loglama, karakter kodlama, sıkıştırma (GZIP).
  * **HandlerInterceptor:**
    * **Bulunduğu Katman:** Spring MVC katmanında, `DispatcherServlet` ile `Controller` arasında yer alır.
    * **Çalışma Zamanı:** İstek `DispatcherServlet` tarafından karşılandıktan sonra ve Controller metoduna girmeden önce (`preHandle`), Controller bittikten sonra (`postHandle`) ve istek tamamlandığında (`afterCompletion`) çalışır.
    * **Bilgi Kapsamı:** Yüksek seviyelidir. İsteği karşılayan hedef `HandlerMethod`'u (Controller sınıfı ve metodu) doğrudan bilir; Spring anotasyonlarına erişebilir.
    * **Temel Kullanım:** Controller bazlı özel yetki denetimi, istek işleme sürelerini ölçme, denetim (audit) loglama.
  * **İstek ve Yanıt Akış Sırası:**
    * **Gelen İstek:** İstemci $\rightarrow$ `Servlet Filter` $\rightarrow$ `DispatcherServlet` $\rightarrow$ `HandlerInterceptor (preHandle)` $\rightarrow$ `Controller (İş Metodu)`
    * **Dönen Yanıt:** `Controller` $\rightarrow$ `HandlerInterceptor (postHandle)` $\rightarrow$ View/Response Render $\rightarrow$ `HandlerInterceptor (afterCompletion)` $\rightarrow$ `DispatcherServlet` $\rightarrow$ `Servlet Filter` $\rightarrow$ İstemci

---

### 14. Spring Boot – CORS Kavramı ve Global Yapılandırma

**Soru:** Spring Boot – CORS nedir, neden bir sorun oluşturur? Spring Boot'ta global CORS ayarı nasıl yapılır?

* **Cevap:**
  * **CORS (Cross-Origin Resource Sharing) Nedir?:**
    * Tarayıcıların varsayılan **Aynı Köken Politikası'nı (Same-Origin Policy - SOP)** güvenli bir biçimde esnetmek için kullanılan bir W3C standardıdır.
    * Farklı bir kökenden (farklı protokol, alan adı veya port) gelen web uygulamalarının sunucu kaynaklarına erişip erişemeyeceğini belirler.
  * **Neden Bir Güvenlik Riski Oluşturur?:**
    * Aynı Köken Politikası olmasaydı, kullanıcının ziyaret ettiği kötü niyetli bir web sitesi (`hacker.com`), kullanıcının tarayıcısındaki oturum bilgilerini (cookie/token) kullanarak arka planda kullanıcının banka API'sine (`api.banka.com`) gizlice yetkisiz istekler gönderebilir ve hassas verileri çalabilirdi.
    * Bu nedenle tarayıcılar farklı kökene giden isteklerde önce bir ön uçuş (Preflight - `OPTIONS`) sorgusu atar; sunucu bu kökene açıkça izin vermemişse isteği bloke eder.
  * **Spring Boot'ta Global CORS Yapılandırması:**
    * **1. Spring MVC Seviyesinde Yapılandırma:**
      ```java
      @Configuration
      public class WebConfig implements WebMvcConfigurer {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
              registry.addMapping("/api/**")
                      .allowedOrigins("https://app.banka.com")
                      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                      .allowedHeaders("*")
                      .allowCredentials(true)
                      .maxAge(3600);
          }
      }
      ```
    * **2. Spring Security Ortamında Yapılandırma:**
      * Spring Security kullanılıyorsa, Filter katmanı MVC katmanından önce çalıştığı için `CorsConfigurationSource` bean'i tanımlanmalı ve security filter chain'e `http.cors(...)` şeklinde bağlanmalıdır; aksi halde preflight `OPTIONS` istekleri güvenlik filtresinde takılır.
  * **Tüm Origin'lere (`*`) İzin Vermenin Riskleri:**
    * `allowedOrigins("*")` verilmesi, internetteki herhangi bir web sayfasının API'ye doğrudan istek atabilmesi anlamına gelir.
    * Özellikle kimlik doğrulama çerezleri (`allowCredentials = true`) ile birlikte wildcard (`*`) kullanımı güvenlik standartlarına aykırıdır ve tarayıcılar tarafından engellenir.

---

### 15. Spring Boot – Varsayılan Hata Davranışı ve Global Exception Handling

**Soru:** Spring Boot – Bir `RuntimeException` fırlatıldığında ve hiçbir `@ExceptionHandler` tanımlı değilse, Spring Boot varsayılan olarak ne döner? Hataları yakalamamanın riskleri nelerdir?

* **Cevap:**
  * **Varsayılan Davranış:**
    * Spring Boot, yakalanmayan istisnalar için HTTP 500 Internal Server Error üretir.
    * Dahili `BasicErrorController` devreye girerek tarayıcı isteklerinde varsayılan "Whitelabel Error Page" HTML sayfasını; REST istemcilerinde ise aşağıdaki şablon JSON çıktısını döner:
      ```json
      {
        "timestamp": "2026-09-16T14:18:00.000+00:00",
        "status": 500,
        "error": "Internal Server Error",
        "path": "/api/payments"
      }
      ```
  * **İstisnaları Yakalamamanın ve Yönetmemenin Riskleri:**
    * **1. Bilgi İfşası (Security / Information Disclosure):**
      * Uygun konfigüre edilmemiş ortamlarda istisnanın stack trace detayları, veritabanı tablo adları, SQL sözdizimi, dosya yolları veya kullanılan kütüphane sürümleri yanıt gövdesinde dışarıya sızabilir; bu durum saldırganlar için kritik istihbarat sağlar.
    * **2. API Sözleşmesinin (Contract) Bozulması:**
      * İstemcilerin (mobil uygulama, frontend) beklediği standart hata formatı yerine ham HTML veya öngörülemeyen gövdeler döner; istemci tarafında ayrıştırma (parsing) hatalarına yol açar.
    * **3. İzleme ve Alarm Kirliliği:**
      * Aslında iş kuralı hatası (örneğin bakiye yetersizliği) olan durumlar yakalanmadığında 500 olarak döner; APM araçlarında (Prometheus, Grafana, Datadog) sistem çöküşü alarmlarını gereksiz yere tetikler.
    * **4. Performans ve Kaynak Tüketimi:**
      * Yakalanmayan her istisna için JVM'in derin stack trace üretmesi ve loglaması yüksek CPU ve disk G/Ç maliyeti yaratır.
  * **Çözüm:**
    * Uygulama genelinde `@RestControllerAdvice` ve `@ExceptionHandler` kullanılarak tüm istisnalar merkezi olarak yakalanmalı, anlamlı HTTP durum kodlarına ve standart bir hata şablonuna (`ErrorResponse`) dönüştürülmelidir.

---

### 16. Design Pattern – Proxy Pattern ve Spring AOP

**Soru:** Design Pattern – Proxy pattern nedir? Spring AOP'nin (`@Transactional`, `@Cacheable` gibi annotation'ların arkasında) proxy pattern'i nasıl kullandığını açıkla.

* **Cevap:**
  * **Proxy Pattern Nedir?:**
    * Bir nesneye erişimi denetlemek, araya girmek veya nesnenin davranışına ek sorumluluklar kazandırmak amacıyla o nesnenin yerine geçen bir vekil (temsilci) nesne sunan yapısal tasarım kalıbıdır.
    * İstemci gerçek nesneyle iletişim kurduğunu zanneder; ancak çağrı vekil nesne tarafından karşılanır.
  * **Spring AOP'nin Proxy Kullanımı:**
    * Spring konteyneri, üzerinde AOP anotasyonu bulunan bean'ler için çalışma zamanında dinamik bir vekil nesne üretir (Arayüz uygulayan sınıflar için JDK Dynamic Proxy, somut sınıflar için CGLIB kullanılır).
    * İstemci sınıfı enjekte ettiğinde gerçek servisi değil, Spring'in ürettiği bu proxy nesnesini alır.
  * **Anotasyonların Arkasındaki Çalışma Mekanizması:**
    * **`@Transactional`:**
      * Proxy nesnesi metot çağrısını keser (intercept).
      * `PlatformTransactionManager` üzerinden yeni bir transaction başlatır (`BEGIN`).
      * Asıl hedef metodu çağırır (`target.process()`).
      * Metot başarıyla biterse `commit` çağırır; metot içinde denetlenmeyen bir istisna fırlatılırsa `rollback` yapar.
    * **`@Cacheable`:**
      * Proxy nesnesi çağrıyı yakalar; gelen metot parametrelerinden bir cache anahtarı (cache key) üretir.
      * İlgili anahtarın cache deposunda (Redis, Hazelcast, Ehcache) bulunup bulunmadığına bakar.
      * Veri cache'te varsa asıl metodu hiç çalıştırmadan sonucu doğrudan döner.
      * Veri cache'te yoksa asıl hedef metodu çalıştırır; dönen sonucu cache'e yazar ve istemciye iletir.

---

### 17. Design Pattern – Decorator ile Template Method Karşılaştırması

**Soru:** Design Pattern – Decorator pattern nedir? Template Method pattern ile farkı nedir?

* **Cevap:**
  * **Decorator Pattern:**
    * **Amacı:** Bir nesnenin kaynak kodunu veya kalıtım ağacını değiştirmeden, çalışma zamanında (runtime) nesneye dinamik olarak yeni sorumluluklar ve davranışlar eklemektir.
    * **Yapısı:** Kompozisyon (composition) ilkesine dayanır. Sarmalayıcı (wrapper) sınıf, sarmaladığı nesne ile aynı arayüzü uygular ve içinde o arayüzün bir referansını barındırır.
    * **Örnek:** Java I/O kütüphanesindeki `new BufferedReader(new InputStreamReader(new FileInputStream("dosya.txt")))` yapısı.
  * **Template Method Pattern:**
    * **Amacı:** Bir algoritmanın genel iskeletini bir soyut üst sınıfta (`abstract class`) belirlemek; algoritmanın bazı değişken adımlarının uygulamasını ise alt sınıflara (`subclasses`) bırakmaktır.
    * **Yapısı:** Kalıtım (inheritance) ilkesine dayanır. Algoritmanın adımlarının sırası üst sınıfta `final` bir şablon metot ile kilitlenir; alt sınıflar yalnızca kancaları (hooks) veya soyut adımları `@Override` eder.
  * **Temel Farklar:**
    * **Yapı:** Decorator kompozisyon kullanır ve çalışma zamanında dinamik olarak katmanlanır; Template Method ise kalıtım kullanır ve derleme zamanında katı bir şekilde belirlenir.
    * **Odak Noktası:** Decorator bir nesnenin dış yüzeyine ek özellikler giydirir; Template Method ise bir algoritmanın iç adımlarının nasıl yürütüleceğini koordine eder.

---

### 18. Design Pattern – Chain of Responsibility & Senaryo

**Soru:** Design Pattern – Chain of Responsibility pattern nedir? Bir isteğin birden fazla kontrolden (fraud $\rightarrow$ limit $\rightarrow$ bakiye) sırayla geçmesi gereken bir senaryoda nasıl kullanılır?

* **Cevap:**
  * **Chain of Responsibility (Sorumluluk Zinciri) Nedir?:**
    * Bir isteğin, birbirine bağlı bir işleyici (handler) nesneler zinciri boyunca aktarılmasını sağlayan davranışsal tasarım kalıbıdır.
    * Zincirdeki her halka gelen isteği değerlendirir; ya işlemi tamamlayıp isteği zincirdeki bir sonraki halkaya iletir ya da şartlar sağlanmıyorsa zinciri kırarak işlemi sonlandırır.
    * İstek sahibi, isteğin zincirde kimler tarafından işlendiğini bilmek zorunda kalmaz (gevşek bağ - loose coupling).
  * **Ödeme Senaryosunda Uygulama (Fraud $\rightarrow$ Limit $\rightarrow$ Bakiye):**
    * **1. Ortak İşleyici Arayüzü / Soyut Sınıfı:**
      ```java
      public abstract class PaymentCheckHandler {
          private PaymentCheckHandler next;

          public PaymentCheckHandler setNext(PaymentCheckHandler next) {
              this.next = next;
              return next;
          }

          public void check(PaymentRequest request) {
              processCheck(request);
              if (next != null) {
                  next.check(request);
              }
          }

          protected abstract void processCheck(PaymentRequest request);
      }
      ```
    * **2. Somut İşleyici Halkaları:**
      ```java
      public class FraudCheckHandler extends PaymentCheckHandler {
          @Override
          protected void processCheck(PaymentRequest request) {
              if (request.isFraudulent()) {
                  throw new SecurityException("Şüpheli işlem tespit edildi");
              }
          }
      }

      public class DailyLimitCheckHandler extends PaymentCheckHandler {
          @Override
          protected void processCheck(PaymentRequest request) {
              if (request.getAmount() > 50000) {
                  throw new LimitExceededException("Günlük limit aşıldı");
              }
          }
      }

      public class BalanceCheckHandler extends PaymentCheckHandler {
          @Override
          protected void processCheck(PaymentRequest request) {
              if (request.getAccountBalance().compareTo(request.getAmount()) < 0) {
                  throw new InsufficientBalanceException("Yetersiz bakiye");
              }
          }
      }
      ```
    * **3. Zincirin Kurulması ve Çalıştırılması:**
      ```java
      PaymentCheckHandler chain = new FraudCheckHandler();
      chain.setNext(new DailyLimitCheckHandler())
           .setNext(new BalanceCheckHandler());

      // Tek satırla tüm adımlar sırayla yürütülür
      chain.check(paymentRequest);
      ```

---

### 19. Mikroservis – Mesaj Kuyruklarında Teslimat Garantileri

**Soru:** Mikroservis/Kavram – Mesaj kuyruklarında (Kafka/RabbitMQ) "at-least-once", "at-most-once", "exactly-once" teslim garantisi (delivery guarantee) ne anlama gelir? Bankacılık işlemlerinde hangisi tercih edilir, neden?

* **Cevap:**
  * **Teslimat Garantisi Türleri:**
    * **At-Most-Once (En Fazla Bir Kez):**
      * Mesaj tüketiciye gönderilir ve hemen teslim edildi sayılır (auto-ack).
      * Tüketici mesajı işlerken çökerse veya ağ koparsa mesaj kaybolur; asla yeniden iletilmez.
      * Yinelenen mesaj oluşmaz ancak mesaj kaybı riski yüksektir. Metrik, sensör verisi veya sistem logları gibi veri kaybının tolere edilebildiği yerlerde kullanılır.
    * **At-Least-Once (En Az Bir Kez):**
      * Mesaj tüketiciye verilir; ancak tüketici işlemi bitirip onay (ACK) verene kadar kuyruktan silinmez.
      * Tüketici işlemi tamamlamadan çökerse veya onay mesajı ağda kaybolursa mesaj tekrar gönderilir.
      * Mesaj kaybı yaşanmaz; ancak aynı mesajın iki kez işlenme (duplicate message) riski bulunur.
    * **Exactly-Once (Tam Olarak Bir Kez):**
      * Mesajın kaybolmadan ve yinelenmeden tam olarak bir kez işlenmesini garanti eder.
      * Uçtan uca iki fazlı onay (2PC) ve dağıtık transaction maliyeti getirdiğinden yüksek gecikmeye (latency) yol açar ve sistem karmaşıklığını ciddi oranda artırır.
  * **Bankacılık İşlemlerinde Tercih ve Nedeni:**
    * Bankacılıkta kesinlikle **`At-Least-Once` + Uygulama Seviyesinde Idempotency (Tekilleştirme)** kurgusu tercih edilir.
    * **Gerekçe:** Finansal sistemlerde bir para transferi veya bakiye güncelleme mesajının kaybolması (`at-most-once`) kesinlikle kabul edilemez. Tam `exactly-once` garantisi ise dış mikroservislerin veya üçüncü parti sistemlerin dahil olduğu heterojen mimarilerde pratik olarak sürdürülemez.
    * Bu nedenle `at-least-once` ile mesaj kaybı sıfıra indirilir; olası yinelenen mesajlar ise veritabanı kısıtları ve tekil işlem kimlikleri (`idempotency_key`) aracılığıyla tüketici seviyesinde süzülür.

---

### 20. Mikroservis – API Versiyonlama Yöntemleri

**Soru:** Mikroservis/Kavram – API versiyonlama nedir, neden gereklidir? Yaygın versiyonlama yöntemleri (URL path, header, query param) nelerdir, artı/eksileri nedir?

* **Cevap:**
  * **API Versiyonlama Nedir ve Neden Gereklidir?:**
    * API sözleşmesinde geriye dönük uyumsuz (breaking change) bir değişiklik yapıldığında (örneğin zorunlu bir alan eklenmesi veya alan türünün değişmesi), eski istemcileri (mobil uygulamalar, entegre harici servisler) bozmadan yeni özellikleri sunabilme yöntemidir.
    * Eski istemcilerin zaman içinde yeni versiyona kademeli olarak geçişine olanak tanır.
  * **Yaygın Versiyonlama Yöntemleri:**
    * **1. URL Path Versiyonlama (`/api/v1/accounts`, `/api/v2/accounts`):**
      * *Artıları:* En yaygın ve şeffaf yöntemdir. Tarayıcıdan kolayca test edilebilir; API Gateway ve proxy seviyesinde yönlendirmesi (routing) son derece basittir.
      * *Eksileri:* URI'nin kaynağın saf kimliğini temsil etmesi gerektiği REST prensibine tam uymaz; URL ve route kirliliği yaratır.
    * **2. Custom / HTTP Header Versiyonlama (`X-Version: 2` veya `Accept: application/vnd.banka.v2+json`):**
      * *Artıları:* URL adresi temiz ve tekil kalır. REST ilkelerine daha uygundur; içerik pazarlığı (content negotiation) ile çalışır.
      * *Eksileri:* Tarayıcı adres çubuğundan doğrudan test edilemez (Postman/Curl gerekir). API Gateway, CDN ve proxy katmanlarında önbellekleme (caching) ve yönlendirme kuralları daha karmaşıktır.
    * **3. Query Parameter Versiyonlama (`/api/accounts?version=2`):**
      * *Artıları:* Uygulaması kolaydır. Parametre belirtilmediğinde varsayılan bir sürüme yönlendirmek pratiktir.
      * *Eksileri:* Sayfalama, sıralama ve filtreleme sorgu parametreleri ile karışabilir; bazı CDN ve cache sunucuları varsayılan ayarlarda query parametrelerini önbelleğe alırken atlayabilir.

---

### 21. Senaryo 1 – Duplicate Mesaj & Idempotent Consumer Tasarımı

**Soru:** Bir mikroservis Kafka'dan "ödeme tamamlandı" event'ini bazen ağ hatası nedeniyle 2 kez alıyor (`at-least-once` garanti). Servis bu event'i aldığında müşteri hesabına bakiye ekliyor — event 2 kez işlenirse bakiye yanlışlıkla 2 kat artıyor. Bu sorunu nasıl önlersin? "Idempotent consumer" tasarımını nasıl kurgularsın?

* **Cevap:**
  * **Çözüm Stratejisi:**
    * Her ödeme mesajına üretici (producer) tarafında küresel olarak tekil bir kimlik (`eventId` veya `idempotencyKey` - UUID) atanır.
    * Tüketici (consumer) tarafında, işlenen mesaj kimliklerinin tutulduğu bir idempotency tablosu tanımlanır.
  * **Veritabanı Şeması:**
    ```sql
    CREATE TABLE processed_events (
        event_id VARCHAR(64) PRIMARY KEY,
        event_type VARCHAR(64) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    ```
  * **İşlem Mantığı ve Atomisite:**
    * Bakiye güncelleme işlemi ile `processed_events` tablosuna yapılan kayıt ekleme işlemi **aynı veritabanı transaction'ı** (`@Transactional`) içinde yürütülür.
    * Bir event ilk kez geldiğinde bakiye güncellenir ve `event_id` tabloya yazılır.
    * Aynı event ağ kopması nedeniyle 2. kez geldiğinde, veritabanının `PRIMARY KEY` kısıtlaması nedeniyle `DuplicateKeyException` (veya `DataIntegrityViolationException`) fırlatılır.
    * Transaction anında geri alınır (rollback); böylece bakiye ikinci kez artırılmaz.
    * Consumer bu özel istisnayı yakalar, işlemin zaten başarılı biçimde tamamlanmış olduğunu anlar ve Kafka'ya başarılı onayını (`ACK`) gönderir. Böylece mesaj kuyrukta takılı kalmaz ve sistem tutarlılığı korunur.

---

### 22. Senaryo 2 – Self-Invocation Rollback Problemi

**Soru:** `PaymentService` sınıfındaki `processPayment()` metodu, aynı sınıf içindeki `@Transactional` işaretli `saveTransaction()` metodunu `this.saveTransaction()` şeklinde çağırıyor. Canlıya çıktıktan sonra, hata durumunda rollback'in beklendiği gibi çalışmadığı fark ediliyor. Bu neden oluyor? Nasıl çözersin?

* **Cevap:**
  * **Neden Oluyor?:**
    * Spring'in deklaratif transaction yönetimi Spring AOP Proxy deseni üzerine kuruludur.
    * Bir metodun transactional çalışabilmesi için çağrının Spring'in araya koyduğu Proxy nesnesi üzerinden gelmesi gerekir.
    * `this.saveTransaction()` çağrısı yapıldığında istek doğrudan mevcut sınıf örneği içinde yürütülür ve Spring Proxy nesnesi tamamen atlanır. Proxy devreye giremediği için transaction başlatılamaz, rollback mekanizması çalışmaz.
  * **Çözüm:**
    * **Önerilen Çözüm (Ayrık Servis Mimarisi):**
      * Tek Sorumluluk İlkesi (SRP) gereği veritabanına kayıt atma işlemi bağımsız bir servise (`TransactionRecordService`) taşınır.
      * `PaymentService`, bu yeni servisi `@Autowired` ile enjekte ederek metodunu çağırır. İstek Spring Proxy üzerinden geçeceği için transaction ve rollback kusursuz çalışır.
      * ```java
        @Service
        public class TransactionRecordService {
            @Transactional(propagation = Propagation.REQUIRES_NEW)
            public void saveTransaction(TransactionData data) {
                // Veritabanı işlemleri
            }
        }
        ```
    * **Alternatif Çözüm (Programatik Transaction):**
      * Sınıfı bölmek istenmiyorsa `TransactionTemplate` kullanılarak transaction sınırları kod bloğu içinde yönetilebilir:
      * ```java
        transactionTemplate.execute(status -> {
            // Rollback garantili işlemler
            return null;
        });
        ```

---

### 23. Senaryo 3 – JSON Serileştirmede Sonsuz Döngü ve StackOverflow

**Soru:** `Customer` entity'sinde `List<Account>` var, her `Account`'ta da geriye `Customer` referansı var (bidirectional ilişki). Bu entity'yi doğrudan Controller'dan dönünce API sürekli `StackOverflowError` veriyor. Neden oluyor, nasıl çözersin?

* **Cevap:**
  * **Neden Oluyor?:**
    * Çift yönlü ilişkilerde Jackson serileştiricisi `Customer` nesnesini JSON'a çevirirken `accounts` listesindeki `Account` nesnelerine gider; oradan tekrar `customer` alanına referans verir ve bu işlem çağrı yığını (call stack) dolana kadar döngüsel olarak devam eder.
  * **Çözüm Adımları:**
    * **1. En Temiz Çözüm: DTO (Data Transfer Object) Kullanımı:**
      * Entity nesneleri doğrudan controller katmanından dönülmemeli, API sözleşmesi için özel DTO sınıfları tanımlanmalıdır:
      * ```java
        public record CustomerResponseDto(Long id, String name, List<AccountSummaryDto> accounts) {}
        public record AccountSummaryDto(Long id, String iban, BigDecimal balance) {}
        ```
    * **2. Jackson Referans Anotasyonları:**
      * Entity'lerin doğrudan dönülmesi gerekiyorsa:
        * Üst sınıfta (`Customer`): `@JsonManagedReference` koleksiyon alanına eklenir.
        * Alt sınıfta (`Account`): `@JsonBackReference` departman/müşteri referansına eklenir. Geriye giden referans serileştirilmeden atlanır.
    * **3. `@JsonIgnore`:**
      * `Account` entity'si içindeki `customer` alanının üzerine `@JsonIgnore` eklenerek JSON çıktısına dahil edilmemesi sağlanır.

---

### 24. Senaryo 4 – API Versiyonlama ve Geriye Dönük Uyumluluk

**Soru:** `/api/accounts` endpoint'inin response formatını değiştirmen gerekiyor (`balance` alanı artık ayrı bir obje `{amount, currency}` olacak). Ancak mobil uygulamanın eski versiyonları hâlâ eski formatı bekliyor ve onları anında güncelleyemezsin. Bu durumu nasıl yönetirsin?

* **Cevap:**
  * **Yönetim Stratejisi:**
    * **1. URL Tabanlı Versiyonlama ile Yeni Endpoint Açma:**
      * Mevcut endpoint `/api/v1/accounts` olarak korunur ve eski mobil istemciler için dokunulmadan hizmet vermeye devam eder.
      * Yeni nesne yapısını içeren format `/api/v2/accounts` adresinde yayınlanır.
    * **2. Eski Endpoint'i Kullanımdan Kaldırma (Deprecation) Politikası:**
      * Eski v1 controller metodunun üzerine Java seviyesinde `@Deprecated` eklenir.
      * Yanıt HTTP başlıklarına standart IETF başlıkları eklenerek istemcilere bildirim sağlanır:
        * `Deprecation: @true`
        * `Sunset: Wed, 16 Sep 2027 00:00:00 GMT` (API'nin kapatılacağı tarih)
    * **3. Kod Tekrarını Önleme (Business Katmanı Paylaşımı):**
      * V1 ve V2 controller metotları aynı servis metodunu çağırır. Servisten dönen ortak domain modeli;
        * V1 için `AccountV1Response` (`balance: BigDecimal`),
        * V2 için `AccountV2Response` (`balance: { amount, currency }`)
          şeklinde ilgili DTO'lara dönüştürülerek sunulur.

---

### 25. Senaryo 5 – Enum Tabanlı Durum Makinesi (State Machine)

**Soru:** Bir hesabın durumu `ACTIVE` $\rightarrow$ `FROZEN` $\rightarrow$ `CLOSED` şeklinde ilerleyebiliyor. `CLOSED` bir hesap asla tekrar `ACTIVE`'e dönemez, `FROZEN` bir hesap direkt `CLOSED`'a geçemez (önce `ACTIVE`'e dönmesi lazım). Şu an kodda bu geçiş kuralları hiçbir yerde kontrol edilmiyor, herhangi bir statüden herhangi birine geçiş yapılabiliyor. Bu geçiş kurallarını kod seviyesinde nasıl güvenceye alırsın?

* **Cevap:**
  * **Tasarım:**
    * Durum geçiş kuralları doğrudan `enum` tanımının içine gömülerek kendi kendini doğrulayan bir Durum Makinesi (State Machine) oluşturulur.
    * Her durum, kendisinden sonra geçilebilecek izinli durumlar kümesini (`Set<AccountStatus>`) tanımlar.

```java
public enum AccountStatus {
    CLOSED {
        @Override
        public Set<AccountStatus> nextAllowedStatuses() {
            return Collections.emptySet(); // Kapalı hesaptan hiçbir duruma geçilemez
        }
    },
    FROZEN {
        @Override
        public Set<AccountStatus> nextAllowedStatuses() {
            return EnumSet.of(ACTIVE); // Dondurulmuş hesap yalnızca aktif duruma dönebilir
        }
    },
    ACTIVE {
        @Override
        public Set<AccountStatus> nextAllowedStatuses() {
            return EnumSet.of(FROZEN, CLOSED); // Aktif hesap dondurulabilir veya kapatılabilir
        }
    };

    public abstract Set<AccountStatus> nextAllowedStatuses();

    public boolean canTransitionTo(AccountStatus targetStatus) {
        return nextAllowedStatuses().contains(targetStatus);
    }
}
```

* **Domain Sınıfında Kullanım:**

```java
public class Account {
    private AccountStatus status;

    public void changeStatus(AccountStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Geçersiz durum geçişi: %s -> %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }
}
```

---

### 26. Senaryo 6 – CORS Hatası ve Güvenli Yapılandırma

**Soru:** Bankanın yeni web arayüzü (`app.banka.com`) API'yi (`api.banka.com`) çağırdığında tarayıcıda CORS hatası alıyor, istek API'ye hiç ulaşmıyor. Bu neden oluyor? Spring Boot'ta bunu nasıl çözersin? Tüm origin'lere (`*`) izin vermek neden riskli olabilir?

* **Cevap:**
  * **Neden Oluyor?:**
    * Tarayıcıların Güvenlik Politikası (Same-Origin Policy) gereği protokol, host veya porttan herhangi biri farklıysa bu bir Cross-Origin istek sayılır (`app.banka.com` ile `api.banka.com` farklı alt alan adlarıdır).
    * Tarayıcı asıl istek öncesinde bir `OPTIONS` (Preflight) isteği atar; sunucu bu kökene izin veren `Access-Control-Allow-Origin` başlığını dönmediği için tarayıcı isteğin API'ye gitmesini engeller.
  * **Spring Boot Çözümü:**

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://app.banka.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

* **Tüm Origin'lere (`*`) İzin Vermenin Riskleri:**
  * Saldırganların kontrolündeki herhangi bir web sitesi (`sahte-site.com`), kullanıcının tarayıcısı üzerinden banka API'sine istek atabilir.
  * Kullanıcının kimlik doğrulama oturumu açıksa, saldırgan kullanıcı adına sorgular çalıştırabilir ve hassas bankacılık verilerini okuyabilir.

---

### 27. Senaryo 7 – Fraud ve Ödeme Kontrol Zinciri (Chain of Responsibility)

**Soru:** Bir ödeme isteği işlenmeden önce sırasıyla (1) fraud kontrolü, (2) günlük limit kontrolü, (3) bakiye kontrolü yapılması gerekiyor. Şu an bunların hepsi `PaymentService` içinde art arda if-else olarak yazılmış, yeni bir kontrol eklemek gerektiğinde kod gittikçe karmaşıklaşıyor. Bu yapıyı nasıl yeniden tasarlarsın?

* **Cevap:**
  * **Tasarım Yaklaşımı:**
    * Kontrolleri `Chain of Responsibility` desenine göre bağımsız sınıflara ayırarak zincir şeklinde kurgulamak.
    * Her sınıf tek bir kontrol kuralından sorumlu olur (Single Responsibility Principle) ve yeni bir kural eklendiğinde mevcut sınıflara dokunulmaz (Open/Closed Principle).

```java
// 1. Temel İşleyici Sınıfı
public abstract class PaymentValidator {
    private PaymentValidator next;

    public PaymentValidator linkWith(PaymentValidator next) {
        this.next = next;
        return next;
    }

    public void validate(PaymentContext context) {
        doValidate(context);
        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void doValidate(PaymentContext context);
}

// 2. Somut Doğrulayıcılar
@Component
public class FraudValidator extends PaymentValidator {
    @Override
    protected void doValidate(PaymentContext context) {
        if (context.isSuspiciousIp()) {
            throw new PaymentValidationException("Güvenlik riski: Şüpheli IP adresi");
        }
    }
}

@Component
public class DailyLimitValidator extends PaymentValidator {
    @Override
    protected void doValidate(PaymentContext context) {
        if (context.getRequestedAmount() > context.getRemainingDailyLimit()) {
            throw new PaymentValidationException("Günlük işlem limiti aşıldı");
        }
    }
}

@Component
public class BalanceValidator extends PaymentValidator {
    @Override
    protected void doValidate(PaymentContext context) {
        if (context.getCurrentBalance().compareTo(context.getRequestedAmount()) < 0) {
            throw new PaymentValidationException("Hesap bakiyesi yetersiz");
        }
    }
}

// 3. Servis Katmanında Kullanım
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final FraudValidator fraudValidator;
    private final DailyLimitValidator limitValidator;
    private final BalanceValidator balanceValidator;

    @PostConstruct
    public void initChain() {
        fraudValidator.linkWith(limitValidator).linkWith(balanceValidator);
    }

    public void processPayment(PaymentContext context) {
        fraudValidator.validate(context); // Zincir sırayla tüm kontrolleri çalıştırır
        // Ödeme işlemi gerçekleştirilir
    }
}
```

---

### 28. Senaryo 8 – Dağıtık Sistemlerde Kısmi Başarısızlık (Partial Failure)

**Soru:** Bir "para transferi" isteği önce "Hesap Servisi"ni (bakiyeyi düşür), sonra "Bildirim Servisi"ni (SMS gönder) senkron REST ile sırayla çağırıyor. Hesap Servisi başarılı oluyor ama Bildirim Servisi zaman aşımına uğruyor. Kullanıcıya hata dönülüyor, ama parası zaten düşmüş durumda. Bu tutarsızlığı nasıl önlersin? Bildirimin "en az bir kez" garantiyle mi, yoksa best-effort mu olması gerektiğini tartış.

* **Cevap:**
  * **Temel Mimari Hata:**
    * Kritik bir finansal işlem (bakiye düşme) ile ikincil bir yan işlem (SMS bildirimi) senkron bir zincirde sıkı sıkıya bağlanmıştır. SMS servisindeki bir gecikme ana finansal işlemin durumunu belirsizliğe sürüklemektedir.
  * **Çözüm Mimarisi (Asenkron Event-Driven & Transactional Outbox Pattern):**
    * **1. İşlemlerin Ayrıştırılması:** Bakiye düşürme işlemi tamamlandığında istemciye anında "İşleminiz Alındı / Başarılı" yanıtı dönülmelidir; SMS gönderimi asla senkron akışta beklenmemelidir.
    * **2. Transactional Outbox Deseni:**
      * Para transferi veritabanına yazılırken aynı transaction içinde bir `outbox_events` tablosuna `MoneyTransferredEvent` kaydı eklenir (Atomik işlem).
      * Bir arka plan servisi (veya CDC aracı - Debezium) bu tabloyu okuyup olayı Kafka veya RabbitMQ kuyruğuna aktarır.
    * **3. Asenkron Tüketim:**
      * Bildirim Servisi kuyruktan mesajı okur ve SMS'i gönderir.
  * **Teslimat Garantisi Tartışması:**
    * **Best-Effort Yetersizdir:** Finansal regülasyonlar (BDDK) ve müşteri güvenliği açısından para çıkışı bilgilendirmesi kaybolmamalıdır.
    * **At-Least-Once + Retry:** Bildirim işlemi `at-least-once` garantisi ve exponential backoff ile yeniden deneme (retry) politikasıyla çalışmalıdır.
    * SMS sağlayıcısı geçici olarak kapalıysa mesaj Dead Letter Queue (DLQ) yapısına alınarak incelenmeli, ancak ana transfer işlemi asla geri alınmamalıdır.

---

### 29. Algoritma – Binary Search (O(log n))

**Soru:** Sıralı bir işlem tutarları dizisinde belirli bir tutarı arayan, $O(\log n)$ zaman karmaşıklığında bir fonksiyon yaz.

* **Çözüm:**
  * Sıralı dizilerde arama aralığını her adımda yarıya indirerek $O(\log n)$ zamanda sonuca ulaşılır.
  * `(left + right) / 2` ifadesinde oluşabilecek olası integer taşmasını (overflow) önlemek için `left + (right - left) / 2` formülü kullanılır.

```java
public class BinarySearch {

    /**
     * Sıralı dizide aranan hedef değerin indeksini döner.
     * Bulunamazsa -1 döner.
     */
    public static int binarySearch(int[] sortedAmounts, int target) {
        if (sortedAmounts == null || sortedAmounts.length == 0) {
            return -1;
        }

        int left = 0;
        int right = sortedAmounts.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (sortedAmounts[mid] == target) {
                return mid; // Hedef değer bulundu
            } else if (sortedAmounts[mid] < target) {
                left = mid + 1; // Sağ yarıda ara
            } else {
                right = mid - 1; // Sol yarıda ara
            }
        }

        return -1; // Bulunamadı
    }
}
```

* **Karmaşıklık Analizi:**
  * **Zaman Karmaşıklığı:** $O(\log n)$ – Arama uzayı her adımda yarıya bölünür.
  * **Alan Karmaşıklığı:** $O(1)$ – Ekstra bellek alanı kullanılmaz.

---

### 30. Algoritma – Kadane's Algorithm (Maximum Subarray Sum)

**Soru:** Bir hesabın günlük net kâr/zarar değerlerini temsil eden bir dizide, en yüksek toplam kazancı sağlayan ardışık gün aralığını (subarray) bulan bir fonksiyon yaz.

* **Çözüm:**
  * Kadane Algoritması, dinamik programlama yaklaşımıyla diziyi tek bir geçişte ($O(n)$) tarar.
  * Her adımda şu karar verilir: Mevcut eleman, önceki alt dizi toplamına mı eklensin yoksa tek başına yeni bir alt dizi mi başlatsın?

```java
public class KadaneAlgorithm {

    /**
     * En yüksek kâr getiren ardışık günlerin aralığını ve toplam kazancını döner.
     * Dönen dizi formatı: [başlangıç_indeksi, bitiş_indeksi, maksimum_toplam]
     */
    public static int[] findMaxProfitRange(int[] dailyNetChange) {
        if (dailyNetChange == null || dailyNetChange.length == 0) {
            return new int[]{-1, -1, 0};
        }

        int maxSoFar = dailyNetChange[0];
        int currentMax = dailyNetChange[0];

        int start = 0;
        int end = 0;
        int tempStart = 0;

        for (int i = 1; i < dailyNetChange.length; i++) {
            // Yeni bir alt dizi başlatmak önceki toplama eklemekten daha mı karlı?
            if (dailyNetChange[i] > currentMax + dailyNetChange[i]) {
                currentMax = dailyNetChange[i];
                tempStart = i;
            } else {
                currentMax += dailyNetChange[i];
            }

            // Şimdiye kadarki en yüksek toplam güncellendi mi?
            if (currentMax > maxSoFar) {
                maxSoFar = currentMax;
                start = tempStart;
                end = i;
            }
        }

        return new int[]{start, end, maxSoFar};
    }
}
```

* **Karmaşıklık Analizi:**
  * **Zaman Karmaşıklığı:** $O(n)$ – Dizi tek bir döngü ile taranır.
  * **Alan Karmaşıklığı:** $O(1)$ – Yalnızca birkaç sayaç ve gösterici değişken tutulur.

---

### 31. Algoritma – Missing Number (Eksik İşlem ID'sini Bulma)

**Soru:** 1'den n'e kadar olması gereken işlem ID'lerini tutan bir dizide, hata nedeniyle bir ID kaydedilmemiş. Ekstra bellek kullanmadan ($O(1)$ alan karmaşıklığı) eksik ID'yi bulan bir fonksiyon yaz.

* **Çözüm:**
  * İki popüler yaklaşım mevcuttur:
    * **1. Matematiksel Toplam (Gauss Formülü):** 1'den n'e kadar sayıların toplamı $\frac{n \times (n + 1)}{2}$ formülü ile hesaplanır ve dizideki elemanların toplamı çıkarılır. Büyük $n$ değerlerinde `long` tipi kullanılarak integer overflow engellenmelidir.
    * **2. Bitwise XOR Yaklaşımı (Tavsiye Edilen):** Bir sayının kendisiyle XOR işlemi 0'dır ($x \oplus x = 0$). 1'den n'e kadar tüm sayılar ile dizideki tüm elemanlar XOR'landığında, çifti olan tüm sayılar birbirini sıfırlar; geriye yalnızca eksik sayı kalır. Aritmetik taşma (overflow) riski kesinlikle yoktur.

```java
public class MissingNumber {

    /**
     * XOR yaklaşımı ile eksik ID'yi bulma (Overflow riski yoktur).
     */
    public static int findMissingTransactionId(int[] transactionIds, int n) {
        int xorAll = 0;

        // 1'den n'e kadar tüm sayıları XOR'la
        for (int i = 1; i <= n; i++) {
            xorAll ^= i;
        }

        // Dizideki mevcut sayıları XOR'la
        for (int id : transactionIds) {
            xorAll ^= id;
        }

        return xorAll; // Çifti olmayan eksik ID kalır
    }

    /**
     * Gauss Toplamı Formülü ile eksik ID'yi bulma.
     */
    public static int findMissingTransactionIdGauss(int[] transactionIds, int n) {
        long expectedSum = (long) n * (n + 1) / 2;
        long actualSum = 0;

        for (int id : transactionIds) {
            actualSum += id;
        }

        return (int) (expectedSum - actualSum);
    }
}
```

* **Karmaşıklık Analizi:**
  * **Zaman Karmaşıklığı:** $O(n)$ – Dizi tek turda taranır.
  * **Alan Karmaşıklığı:** $O(1)$ – Sadece tek bir değişken kullanılır.

---

### 32. Algoritma – Sliding Window (Fixed Size k - Hareketli Ortalama)

**Soru:** Bir hesabın son N günlük işlem tutarları dizisinde, $k$ günlük hareketli ortalama (moving average) listesini hesaplayan bir fonksiyon yaz.

* **Çözüm:**
  * Sabit boyutlu Kayan Pencere (Sliding Window) tekniği kullanılır.
  * Her adımda $k$ boyutlu pencereyi baştan hesaplamak yerine ($O(n \times k)$), pencere bir adım sağa kaydırılırken pencereden çıkan eski eleman çıkarılır ve yeni giren eleman toplama eklenir ($O(n)$).

```java
public class MovingAverage {

    /**
     * k boyutlu hareketli ortalama dizisi üretir.
     */
    public static double[] movingAverage(int[] dailyAmounts, int k) {
        if (dailyAmounts == null || k <= 0 || dailyAmounts.length < k) {
            return new double[0];
        }

        int n = dailyAmounts.length;
        double[] result = new double[n - k + 1];
        double windowSum = 0;

        // 1. İlk k boyutundaki ilk pencerenin toplamını hesapla
        for (int i = 0; i < k; i++) {
            windowSum += dailyAmounts[i];
        }
        result[0] = windowSum / k;

        // 2. Pencereyi birer eleman kaydırarak ilerle
        for (int i = k; i < n; i++) {
            windowSum += dailyAmounts[i] - dailyAmounts[i - k]; // Yeni elemanı ekle, eskiyi çıkar
            result[i - k + 1] = windowSum / k;
        }

        return result;
    }
}
```

* **Karmaşıklık Analizi:**
  * **Zaman Karmaşıklığı:** $O(n)$ – Dizi üzerinden yalnızca bir kez geçilir.
  * **Alan Karmaşıklığı:** $O(n - k + 1)$ – Yalnızca sonuç dizisi boyutu kadar alan tutulur.
