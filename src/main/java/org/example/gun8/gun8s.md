# Gün 8 – Mülakat Hazırlık Soruları ve Çözümleri

---

### 1. Java – String İmmutability (Değiştirilemezlik) ve String Constant Pool

**Soru:** Java – String neden immutable (değiştirilemez) bir sınıftır? String Pool (String Constant Pool) nedir? `new String("x")` ile `"x"` arasındaki fark nedir?

* **Cevap:**
  * **String Neden Immutable Bir Sınıftır?:**
    * **Güvenlik (Security):**
      * Veritabanı bağlantı URL'leri, kullanıcı parolaları, ağ soket adresleri ve kimlik doğrulama token'ları metin olarak String içerisinde tutulur.
      * String değiştirilebilir olsaydı, bir metoda veya katmana parametre olarak gönderilen kritik bir değer (örneğin dosya yolu veya yetkilendirme bilgisi) başka bir thread veya kötü niyetli kod tarafından referans üzerinden değiştirilebilir ve ciddi güvenlik açıklarına yol açabilirdi.
      * JVM'in sınıf yükleyicisi (`ClassLoader`), yükleyeceği sınıfların tam adlarını String olarak alır; immutability sınıf yükleme güvenliğinin temelini oluşturur.
    * **String Pool Optimizasyonu ve Bellek Tasarrufu:**
      * Java'da uygulamalar binlerce aynı metin değerini tekrar tekrar kullanır. String immutable olmasaydı, String Pool mekanizması var olamazdı.
      * Eğer bir referans String Pool'daki bir metni değiştirebilseydi, aynı metni paylaşan diğer tüm referanslar da habersizce değişmiş olur ve veri tutarsızlığı meydana gelirdi.
    * **Çoklu Thread Güvenliği (Thread-Safety):**
      * Immutable nesnelerin dahili durumu (state) oluşturulduktan sonra asla değişmez.
      * Farklı thread'ler aynı String nesnesini herhangi bir senkronizasyon (`synchronized`, `Lock`) maliyeti olmadan eşzamanlı ve güvenli bir şekilde paylaşabilir.
    * **HashCode Önbellekleme (Caching HashCode):**
      * String nesneleri `HashMap`, `HashSet` ve `Hashtable` gibi koleksiyonlarda anahtar (key) olarak yoğun biçimde kullanılır.
      * String immutable olduğu için hash değeri nesne ilk oluşturulduğunda bir kez hesaplanır ve dahili `hash` alanında saklanır (caching).
      * Koleksiyonda arama yapılırken her seferinde yeniden hash hesaplanmaz; ayrıca anahtarın değeri sonradan değişemeyeceği için koleksiyon içi adres bozulması engellenir.

  * **String Pool (String Constant Pool) Nedir?:**
    * JVM Heap belleğinde yer alan, aynı içeriğe sahip String literal'lerinin yalnızca tek bir kopyasının saklandığı özel bir bellek alanıdır.
    * Bellek tüketimini en aza indirmek ve String oluşturma maliyetini düşürmek amacıyla tasarlanmıştır.

  * **`new String("x")` ile `"x"` Arasındaki Fark:**
    * **`String s1 = "x";` (Literal Tanımlama):**
      * JVM önce String Pool'a bakar. Eğer havuzda `"x"` değeri varsa, yeni bir nesne oluşturulmaz; var olan nesnenin referansı `s1`'e atanır. Havuzda yoksa oluşturulup havuza eklenir.
    * **`String s2 = new String("x");` (`new` Anahtar Kelimesi):**
      * `new` anahtar kelimesi JVM'e doğrudan talimat verir ve String Pool'dan bağımsız olarak Heap belleğinde tamamen yeni ve benzersiz bir String nesnesi tahsis eder.
      * Parametre olarak geçilen `"x"` literal'i pool'da yoksa pool'a eklenir; ancak `s2` referansı doğrudan Heap'teki yeni nesneyi işaret eder.
    * **Bellek Eşitliği Kontrolü:**
      * `"x" == "x"` ifadesi `true` döner (aynı pool referansı).
      * `"x" == new String("x")` ifadesi `false` döner (biri pool'da, diğeri Heap'te ayrı bir nesnedir).
      * `new String("x").intern()` metodu çağrılırsa nesnenin pool'daki referansı döner.

```java
String a = "java";
String b = "java";
String c = new String("java");

System.out.println(a == b);          // true  (String Pool'daki aynı nesne referansı)
System.out.println(a == c);          // false (Biri pool referansı, diğeri heap nesnesi)
System.out.println(a.equals(c));     // true  (Karakter dizilimi/içerik aynı)
System.out.println(a == c.intern()); // true  (intern() metodu pool'daki referansı döner)
```

---

### 2. Java – Autoboxing / Unboxing ve Integer Cache Mekanizması

**Soru:** Java – Autoboxing/unboxing nedir? Integer cache mekanizması (-128 ile 127 arası) nedir? İki Integer nesnesini `==` ile karşılaştırmak neden bazen beklenmedik sonuç verir?

* **Cevap:**
  * **Autoboxing ve Unboxing:**
    * **Autoboxing:** İlkel (primitive) bir veri tipinin derleyici tarafından otomatik olarak karşılık gelen sarmalayıcı (wrapper) nesneye dönüştürülmesidir (Örnek: `int` $\to$ `Integer.valueOf(int)`).
    * **Unboxing:** Sarmalayıcı nesnenin otomatik olarak ilkel veri tipine dönüştürülmesidir (Örnek: `Integer` $\to$ `integerObj.intValue()`).

  * **Integer Cache Mekanizması (-128 ile 127):**
    * Java dil şartnamesi (JLS), bellek tasarrufu ve performans optimizasyonu sağlamak adına `-128` ile `127` arasındaki tamsayı değerlerini `IntegerCache` dahili sınıfı aracılığıyla JVM ayağa kalktığında statik olarak önbelleğe alır.
    * `Integer.valueOf()` metodu çağrıldığında (veya autoboxing yapıldığında), eğer sayı `-128` ile `127` aralığında ise yeni bir nesne üretilmez; önbellekteki sabit nesnenin referansı döner.
    * Bu aralık üst sınırı istenirse JVM argümanı ile (`-XX:AutoBoxCacheMax=<size>`) genişletilebilir.

  * **İki `Integer` Nesnesini `==` ile Karşılaştırmanın Tehlikesi:**
    * İlkel tiplerde `==` operatörü sayısal değer eşitliğini kıyaslar.
    * Nesnelerde (Wrapper sınıflar dahil) `==` operatörü **referans (bellek adresi) eşitliğini** kontrol eder.
    * `-128` ile `127` arasındaki sayılarda autoboxing yapıldığında iki değişken de önbellekteki aynı nesneyi işaret ettiği için `==` kontrolü beklenmedik şekilde `true` verir.
    * Ancak sayı bu aralığın dışına çıktığında (örneğin `128` veya `500`), her autoboxing işleminde Heap üzerinde `new Integer()` benzeri yeni ve bağımsız bir nesne oluşturulur. Değerler aynı olsa bile bellek adresleri farklı olduğundan `==` kontrolü `false` döner.
    * **Kural:** Wrapper nesneler her zaman `equals()` metodu ile karşılaştırılmalıdır.

```java
Integer x1 = 127;
Integer x2 = 127;
System.out.println(x1 == x2);      // true  (IntegerCache aralığında, referanslar aynı)

Integer y1 = 128;
Integer y2 = 128;
System.out.println(y1 == y2);      // false (Cache dışı, heap'te farklı nesneler)
System.out.println(y1.equals(y2)); // true  (Değer eşitliği kontrolü - DOĞRU KULLANIM)
```

---

### 3. Java – Generic Bounded Wildcards (`<? extends T>` ve `<? super T>`)

**Soru:** Java – Generic'lerde `<? extends T>` ve `<? super T>` (bounded wildcard) ne işe yarar? Basit bir örnekle farklarını açıkla.

* **Cevap:**
  * **PECS Prensibi (Producer Extends, Consumer Super):**
    * Bir generic yapıdan sadece veri okuyacaksanız yapıyı bir **üretici (producer)** olarak ele almalı ve `<? extends T>` kullanmalısınız.
    * Bir generic yapıya sadece veri yazacaksanız/ekleyecekseniz yapıyı bir **tüketici (consumer)** olarak ele almalı ve `<? super T>` kullanmalısınız.

  * **`<? extends T>` (Upper Bounded Wildcard / Covariance):**
    * `T` sınıfını veya `T`'den türeyen (extend eden) herhangi bir alt tipi kabul eder.
    * **Sadece Okuma (Read-Only) Güvenliği:** Koleksiyondan eleman çekerken gelen elemanın en az `T` türünde olduğu garanti edilir (`T element = list.get(0)`).
    * **Yazma Kısıtı:** Koleksiyona `null` haricinde yeni bir eleman eklenemez (`list.add(...)` derleme hatası verir). Çünkü listenin çalışma zamanındaki gerçek türünün `T`'nin hangi alt tipi olduğu (örneğin `Integer` mı `Double` mı) derleyici tarafından kesin olarak bilinemez.

  * **`<? super T>` (Lower Bounded Wildcard / Contravariance):**
    * `T` sınıfını veya `T`'nin üst sınıflarını (ata sınıflarını) kabul eder.
    * **Yazma (Write) Güvenliği:** Koleksiyona `T` türünden veya `T`'nin alt sınıflarından nesneler güvenle eklenebilir (`list.add(new T())`). Çünkü listenin tuttuğu tür `T` veya onun bir üst sınıfı olduğundan tip güvenliği bozulmaz.
    * **Okuma Kısıtı:** Koleksiyondan okuma yapıldığında elemanların türü garanti edilemez; gelen elemanlar sadece en genel ata tip olan `Object` olarak okunabilir.

  * **Standart Kütüphane Örneği (`Collections.copy`):**
    * Kaynaktan okunur (`src` $\to$ `extends`), hedefe yazılır (`dest` $\to$ `super`):
      `public static <T> void copy(List<? super T> dest, List<? extends T> src)`

```java
// Hiyerarşi: Object -> Number -> Integer / Double

// 1. Producer Extends: Sayıları oku ve topla (Listeye yeni eleman eklenemez)
public static double sumOfList(List<? extends Number> numbers) {
    double sum = 0.0;
    for (Number n : numbers) {
        sum += n.doubleValue(); // Güvenli okuma
    }
    // numbers.add(10); // DERLEME HATASI! Liste gerçekte List<Double> olabilir.
    return sum;
}

// 2. Consumer Super: Listeye tamsayı ekle (Tüketici)
public static void addIntegers(List<? super Integer> list) {
    list.add(1);  // Güvenli yazma
    list.add(2);  // Integer veya Number tipindeki listelere Integer eklenebilir
    // Integer num = list.get(0); // DERLEME HATASI! Sadece Object dönebilir.
}
```

---

### 4. Java – Java 14+ Switch Expressions ve Fall-Through Çözümü

**Soru:** Java – Java 14+ ile gelen switch expression'lar (switch $\to$ `yield` / ok `->` syntax) klasik switch-case'e göre hangi sorunu (fall-through riski) çözer?

* **Cevap:**
  * **Klasik Switch ve Fall-Through Tehlikesi:**
    * Klasik `switch-case` yapısında her `case` bloğunun sonuna açıkça `break;` yazılması zorunludur.
    * `break` yazılması unutulursa, kod bir sonraki `case` bloğunun koşulunu kontrol etmeksizin alt bloğa akmaya devam eder (**fall-through**). Bu durum derleme hatası vermez ancak sinsi mantık hatalarına (logic bugs) sebep olur.

  * **Switch Expression ile Gelen İyileştirmeler:**
    * **Ok Sözdizimi (`->` Arrow Syntax):**
      * Yalnızca eşleşen `case` kolu çalıştırılır ve bloktan otomatik olarak çıkılır.
      * Fall-through riski tamamen ortadan kalkar; manuel `break;` yazma ihtiyacı biter.
    * **İfade (Expression) Olarak Değer Döndürme:**
      * Switch artık bir komut (statement) olmakla kalmaz; bir ifade (expression) olarak geriye bir değer dönebilir ve doğrudan bir değişkene atanabilir.
    * **`yield` Anahtar Kelimesi:**
      * Birden fazla satırdan oluşan bloklarda hesaplanan sonucu switch dışına döndürmek için `yield` ifadesi kullanılır.
    * **Kapsamlılık (Exhaustiveness) Denetimi:**
      * Switch bir değişkene değer atıyorsa veya `enum` / `sealed class` ile kullanılıyorsa, derleyici tüm olası durumların kapsandığını (`default` kolu veya tüm varyantlar) doğrular.

```java
// Java 14+ Modern Switch Expression
DayOfWeek day = DayOfWeek.FRIDAY;

String status = switch (day) {
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY -> "Hafta İçi - Yoğun";
    case FRIDAY -> {
        // Çok satırlı bloklarda yield ile değer döndürülür
        log.info("Hafta sonu yaklaşıyor");
        yield "Hafta İçi - Son Gün";
    }
    case SATURDAY, SUNDAY -> "Hafta Sonu - Tatil";
}; // Expression olduğu için noktalı virgül ile biter
```

---

### 5. Java – `ConcurrentModificationException` ve Güvenli Eleman Silme

**Soru:** Java – Bir `List` üzerinde for-each ile gezinirken aynı anda eleman silmeye çalışırsan ne olur? `ConcurrentModificationException` neden oluşur, doğrusu nasıl yapılır?

* **Cevap:**
  * **Hataya Yol Açan Senaryo:**
    * Geliştirilmiş `for-each` döngüsü (`for (String item : list)`), arka planda Java derleyicisi tarafından `Iterator` yapısına dönüştürülür.
    * Döngü içerisindeyken doğrudan liste referansı üzerinden `list.remove(item)` çağrılırsa ilk veya ikinci iterasyonda `ConcurrentModificationException` fırlatılır.

  * **`ConcurrentModificationException` Neden Oluşur?:**
    * `ArrayList` gibi koleksiyonlar, yapısal değişikliklerin (ekleme/silme) sayısını takip eden bir `modCount` (modification count) değişkeni tutar.
    * Döngü başladığında oluşturulan `Iterator` nesnesi, mevcut `modCount` değerini kendi içindeki `expectedModCount` alanına kopyalar.
    * Liste üzerinden `list.remove(...)` çağrıldığında listenin `modCount` değeri 1 artar; fakat döngüyü yürüten `Iterator`'ın bu işlemden haberi olmaz ve `expectedModCount` eski değerinde kalır.
    * Bir sonraki adımda `iterator.next()` çağrıldığında `checkForComodification()` kontrolü çalışır:
      `if (modCount != expectedModCount) throw new ConcurrentModificationException();`
    * Java bu mekanizmayla **hızlı başarısızlık (fail-fast)** sağlayarak tutarsız durumdaki bir koleksiyon üzerinde çalışmayı engeller.

  * **Doğru Silme Yöntemleri:**
    * **Yöntem 1: Java 8+ `Collection.removeIf()` Kullanımı (Tavsiye Edilen):**
      * Fonksiyonel ve en performanslı yöntemdir ($O(N)$ karmaşıklık).
    * **Yöntem 2: Açık (Explicit) `Iterator.remove()` Kullanımı:**
      * İteratörün kendi `remove()` metodu silme işlemi sonrasında `expectedModCount = modCount` senkronizasyonunu bizzat yaptığı için hata fırlatmaz.
    * **Yöntem 3: Akış (Stream) ile Yeni Liste Üretme:**
      * Orijinal listeyi yerinde değiştirmek yerine koşulu sağlayan elemanları yeni bir listeye filtrelemek.

```java
List<String> names = new ArrayList<>(List.of("Ali", "Veli", "Ayşe"));

// YANLIŞ YAKLAŞIM (ConcurrentModificationException fırlatır):
// for (String name : names) {
//     if (name.equals("Veli")) names.remove(name);
// }

// DOĞRU YAKLAŞIM 1: Java 8+ removeIf
names.removeIf(name -> name.equals("Veli"));

// DOĞRU YAKLAŞIM 2: Iterator.remove()
Iterator<String> iterator = names.iterator();
while (iterator.hasNext()) {
    String name = iterator.next();
    if (name.equals("Veli")) {
        iterator.remove(); // Güvenli silme işlemi
    }
}
```

---

### 6. Hibernate – `@Version` ile Optimistic Locking ve Çakışma Yönetimi

**Soru:** Hibernate – `@Version` annotation'ı ile optimistic locking nasıl implemente edilir? Bir güncelleme çakışması olduğunda hangi exception fırlatılır?

* **Cevap:**
  * **Optimistic Locking (İyimser Kilitleme) Mekanizması:**
    * Veritabanı seviyesinde fiziksel satır kilidi (`SELECT ... FOR UPDATE`) koymadan, aynı veri üzerinde eşzamanlı değişikliklerin birbirini ezmesini engellemek için kullanılır.
    * Entity sınıfına `@Version` anotasyonu ile işaretlenmiş sayısal bir alan (`Long`, `Integer`) veya zaman damgası (`Timestamp`) eklenir.

  * **Arka Planda Çalışma Mantığı:**
    * Entity veritabanından okunduğunda mevcut sürüm numarası belleğe alınır (örneğin `version = 1`).
    * Güncelleme yapılacağı zaman Hibernate SQL sorgusunun `WHERE` koşuluna `version` kontrolünü otomatik ekler ve sürüm numarasını 1 artırır:
      ```sql
      UPDATE account 
      SET balance = 5000, version = 2 
      WHERE id = 100 AND version = 1;
      ```
    * Eğer iki kullanıcı aynı anda kaydı açmışsa; ilk güncelleyen sürümü 2 yapar. İkinci kullanıcı güncellemeye çalıştığında veritabanındaki `version` artık 2 olduğu için `WHERE version = 1` koşulu hiçbir satırla eşleşmez (etkilenen satır sayısı 0 döner).
    * Hibernate sıfır satır güncellendiğini tespit ettiği anda çakışmayı anlar ve işlemi iptal ederek hata fırlatır.

  * **Çakışma Anında Fırlatılan Exception Hiyerarşisi:**
    * **Hibernate Native Seviyesinde:** `org.hibernate.StaleObjectStateException` veya `org.hibernate.dialect.lock.OptimisticEntityLockException`.
    * **JPA Standart Katmanında:** `jakarta.persistence.OptimisticLockException` (Hibernate'in fırlattığı hatayı standart JPA istisnasına dönüştürür).
    * **Spring / Spring Data JPA Katmanında:** Spring, JPA istisnalarını kendi DataAccessException hiyerarşisine çevirerek `org.springframework.orm.ObjectOptimisticLockingFailureException` fırlatır.

```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance;

    @Version
    private Long version; // Hibernate tarafından otomatik yönetilen sürüm alanı
}
```

---

### 7. Hibernate – Second-Level Cache (L2 Cache) Mimarisi

**Soru:** Hibernate – Second-level cache (Ehcache/Redis gibi) nasıl kurulur/aktif edilir? Hangi entity'ler second-level cache'e uygundur, hangileri değildir (örnek: döviz kuru tablosu vs. transaction tablosu)?

* **Cevap:**
  * **First-Level Cache vs Second-Level Cache:**
    * **L1 Cache:** `EntityManager` / `Session` kapsamındadır, varsayılan olarak her zaman açıktır ve kapatılamaz. Session kapandığında önbellek yok olur.
    * **L2 Cache:** `SessionFactory` / Uygulama seviyesindedir. Tüm oturumlar ve thread'ler arasında paylaşılan küresel bir önbellektir. Varsayılan olarak kapalıdır; harici bir sağlayıcı (Ehcache, Redis, Hazelcast, Infinispan) ile yapılandırılır.

  * **Kurulum ve Aktif Etme Adımları:**
    * İlgili bağımlılık projeye eklenir (örneğin `hibernate-jcache` ve `ehcache`).
    * `application.yml` içerisinde önbellek sağlayıcısı ve L2 cache aktif edilir:
      ```yaml
      spring:
        jpa:
          properties:
            hibernate:
              cache:
                use_second_level_cache: true
                region.factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
              javax:
                persistence:
                  sharedCache:
                    mode: ENABLE_SELECTIVE
      ```
    * İlgili Entity sınıfı üzerine `@Cacheable` ve `@Cache` anotasyonu eklenir:
      ```java
      @Entity
      @Cacheable
      @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
      public class CurrencyRate { ... }
      ```

  * **Önbellek Eşzamanlılık Stratejileri (`CacheConcurrencyStrategy`):**
    * `READ_ONLY`: Veri asla değişmeyecekse kullanılır. Güncelleme yapılırsa hata fırlatır.
    * `NONSTRICT_READ_WRITE`: Verinin nadiren güncellendiği ve ara sıra eski veri okunmasının tolere edilebildiği durumlar için.
    * `READ_WRITE`: Veri güncellenebilir. Okuma ve yazma dengelidir. Okuma tutarlılığı için satır seviyesinde yumuşak kilit (soft lock) uygular.
    * `TRANSACTIONAL`: JTA ortamı gerektirir. Dağıtık iki aşamalı onay (2PC) desteğiyle tam ACID güvencesi sunar.

  * **Second-Level Cache İçin Uygunluk Kriterleri:**
    * **Uygun Olan Entity'ler (Read-Heavy / Rare-Update):**
      * Döviz Kurları (günde bir veya belirli aralıklarla güncellenir, milyonlarca kez okunur).
      * Ülke, Şehir, Posta Kodu ve Vergi Dairesi referans tabloları.
      * Sistem Rolleri, Yetki ve İzin tanımları.
      * Ürün Kategorileri ve Menü Ağaçları.
    * **Kesinlikle Uygun Olmayan Entity'ler (Write-Heavy / Strict-Consistency):**
      * Hesap Bakiye Tablosu ve Para Transferleri (`Transaction` tablosu).
      * Sürekli yeni kayıt giren ve güncellenen hareket tabloları.
      * Önbellek geçersiz kılma (cache invalidation) maliyeti veritabanı sorgusundan daha pahalıya gelir ve bayat (stale) veri sunulması finansal kayıplara yol açar.

---

### 8. Hibernate – Composite Primary Key (`@IdClass` vs `@EmbeddedId`)

**Soru:** Hibernate – Composite (birleşik) primary key nasıl tanımlanır? `@EmbeddedId` ile `@IdClass` arasındaki fark nedir?

* **Cevap:**
  * **Composite Primary Key Kuralları (JPA Standardı):**
    * Anahtarı oluşturan sınıf `public` olmalıdır.
    * `Serializable` arayüzünü uygulamalıdır.
    * Parametresiz varsayılan bir kurucuya (`no-arg constructor`) sahip olmalıdır.
    * `equals()` ve `hashCode()` metotları tüm anahtar alanları kapsayacak şekilde ezilmelidir (override).

  * **`@IdClass` Yaklaşımı:**
    * Entity içerisinde anahtar alanları normal birer sütun gibi ayrı ayrı tanımlanır ve her birinin başına `@Id` konur.
    * Entity sınıfının başına `@IdClass(OrderIdClass.class)` eklenir.
    * **JPQL Sorguları:** Alanlara doğrudan erişilir: `SELECT o FROM OrderItem o WHERE o.orderId = :orderId`.

  * **`@EmbeddedId` Yaklaşımı:**
    * Composite sınıf `@Embeddable` ile işaretlenir.
    * Entity içerisinde tek bir alan olarak `@EmbeddedId private OrderItemId id;` şeklinde tanımlanır.
    * **JPQL Sorguları:** Nesne hiyerarşisi üzerinden erişilir: `SELECT o FROM OrderItem o WHERE o.id.orderId = :orderId`.
    * Gerektiğinde `@AttributeOverride` ile veritabanı sütun isimleri kolayca ezilebilir.

  * **Neden Tercih Edilir ve Modern Mimarilerde Dönüşüm:**
    * Eski (legacy) sistemlerde vekil anahtarlar (surrogate keys - `id BIGINT AUTO_INCREMENT`) yerine iş mantığı alanlarının birleşimi (örneğin `sube_kod + hesap_no + doviz_tur`) primary key olarak tasarlanmıştır.
    * Çoka-çok (Many-to-Many) ilişki tablolarında ara tabloya ekstra sütun (örneğin eklenme tarihi, miktar) eklenmesi gerektiğinde JPA'da bu ara tablo composite key ile modellenir.
    * **Modernizasyon (CDC + Kafka):** Eski composite key'li sistemlerden yeni mikroservislere veri taşınırken Debezium gibi CDC (Change Data Capture) araçları transaction log'ları dinler, veriyi JSON formatında Kafka'ya aktarır ve modern servisler bu kaydı kendi bağımsız UUID veya Long id'si ile surrogate primary key yapısında depolar.

```java
// Yöntem 1: @Embeddable ve @EmbeddedId (Modern ve Nesne Yönelimli Standart)
@Embeddable
public class OrderItemId implements Serializable {
    private Long orderId;
    private Long productId;

    // Default constructor, equals() ve hashCode() zorunludur
    public OrderItemId() {}
    public OrderItemId(Long orderId, Long productId) {
        this.orderId = orderId;
        this.productId = productId;
    }
    // equals & hashCode
}

@Entity
@Table(name = "order_items")
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;

    private Integer quantity;
    private BigDecimal price;
}
```

---

### 9. Hibernate – `hibernate.hbm2ddl.auto` (ddl-auto) Seçenekleri ve Prod Tercihi

**Soru:** Hibernate – `hibernate.hbm2ddl.auto` (ddl-auto) ayarındaki `create`, `update`, `validate`, `none` seçenekleri ne anlama gelir? Prod ortamında hangisi kullanılmalı, neden?

* **Cevap:**
  * **Seçeneklerin Anlamları:**
    * **`create`:** Uygulama her başladığında veritabanındaki mevcut tabloları siler (`DROP`) ve entity sınıflarına göre tabloları sıfırdan oluşturur (`CREATE`). Mevcut tüm veriler kaybolur.
    * **`create-drop`:** Başlangıçta tabloları oluşturur, `SessionFactory` kapandığında (uygulama durduğunda) tüm tabloları siler. Genellikle entegrasyon testlerinde kullanılır.
    * **`update`:** Mevcut veritabanı şemasını Java entity sınıfları ile karşılaştırır. Yeni eklenen tabloları veya sütunları veritabanına ekler. Ancak **mevcut sütunları veya tabloları asla silmez**, isim değişikliklerini algılayamaz.
    * **`validate`:** Veritabanı şemasına hiçbir müdahalede bulunmaz. Yalnızca mevcut tabloların ve sütunların entity sınıflarıyla birebir eşleşip eşleşmediğini kontrol eder. Uyumsuzluk varsa hata fırlatır ve uygulamanın ayağa kalkmasını engeller.
    * **`none`:** DDL yönetimini tamamen kapatır; veritabanı üzerinde hiçbir kontrol ve işlem yapmaz.

  * **Prod Ortamında Hangisi Kullanılmalı?:**
    * Prodüksiyon ortamında kesinlikle **`none`** veya **`validate`** kullanılmalıdır.
    * Şema yönetimi Hibernate'e bırakılmamalı; **Flyway** veya **Liquibase** gibi versiyon kontrollü veritabanı migrasyon araçları kullanılmalıdır.

  * **`update` Prodüksiyonda Neden ASLA Kullanılmamalıdır?:**
    * **Veri Kaybı ve Yetim Sütunlar:** Kodda bir alanın adını değiştirdiğinizde (örneğin `phone` $\to$ `phoneNumber`), Hibernate bunu bir isim değişikliği olarak anlayamaz; veritabanında `phone_number` adında yeni bir sütun açar ve eski `phone` sütununu tüm verisiyle içeride bırakır.
    * **Kümeleme (Cluster / Multi-Instance) Felaketi:** Kubernetes veya birden fazla sunucu üzerinde çalışan mikroservislerde, aynı anda ayağa kalkan birden fazla pod/instance aynı anda veritabanı şemasını güncellemeye çalışır. Bu durum tablo kilitlenmelerine (DDL lock timeout), yarış durumlarına (race condition) ve uygulama çökmelerine sebep olur.
    * **Riskli Tip Değişiklikleri:** Mevcut bir sütunun tip veya boyut kısıtını (örneğin `VARCHAR(50)` $\to$ `VARCHAR(20)`) değiştiremez; `NOT NULL` kısıtları eklerken mevcut veriler nedeniyle uygulamanın patlamasına yol açar.
    * **Geri Alma (Rollback) İmkansızlığı:** Otomatik yapılan şema değişikliklerinin versiyon geçmişi tutulamaz ve hata anında güvenli bir şekilde geri alınamaz.

---

### 10. Spring Boot – `@RestControllerAdvice` ile `@ControllerAdvice` Karşılaştırması

**Soru:** Spring Boot – `@RestControllerAdvice` ile `@ControllerAdvice` arasındaki fark nedir?

* **Cevap:**
  * **`@ControllerAdvice`:**
    * Spring MVC yapısında `@ExceptionHandler`, `@ModelAttribute` ve `@InitBinder` metotlarını uygulamanın tüm Controller sınıfları için merkezi bir noktada tanımlamak (global exception handling) amacıyla kullanılır.
    * Varsayılan olarak metotların dönüş değerleri bir görünüm (View / HTML şablonu) adı olarak yorumlanır (örneğin `error.html` sayfasına yönlendirme).
    * Eğer bir REST API yanıtı (JSON/XML) dönülmek isteniyorsa, her metot üzerine açıkça `@ResponseBody` anotasyonu eklenmelidir.

  * **`@RestControllerAdvice`:**
    * `@ControllerAdvice` ve `@ResponseBody` anotasyonlarının birleşiminden oluşan bir meta-anotasyondur (`@ControllerAdvice + @ResponseBody`).
    * Spring 4.3 ile RESTful web servislerinin hata yönetimini sadeleştirmek için tanıtılmıştır.
    * Metotların başına fazladan `@ResponseBody` yazma zorunluluğunu ortadan kaldırır.
    * Fırlatılan özel exception DTO nesneleri doğrudan `HttpMessageConverter` (örneğin Jackson) tarafından serileştirilerek HTTP yanıt gövdesine (Response Body) JSON formatında yazılır.

```java
// REST API'ler için standart ve temiz yaklaşım:
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return new ErrorResponse("VALIDATION_FAILED", details, LocalDateTime.now());
    }
}
```

---

### 11. Spring Boot – `RestTemplate`, `WebClient` ve `Feign Client` Karşılaştırması

**Soru:** Spring Boot – RestTemplate, WebClient ve Feign Client arasındaki farklar nelerdir? Hangisi neden "modern" kabul edilir?

* **Cevap:**
  * **`RestTemplate`:**
    * **Çalışma Modeli:** Senkron ve iş parçacığını bloklayan (blocking I/O) geleneksel HTTP istemcisidir.
    * **Kaynak Tüketimi:** İstek başına bir işletim sistemi iş parçacığı (`thread-per-request`) harcar. Ağ gecikmesi yaşandığında yanıt dönene kadar thread kilitli bekler.
    * **Durumu:** Spring 5 ile birlikte bakım moduna (maintenance mode) alınmıştır. Yeni özellik eklenmemektedir; yerine `WebClient` veya Spring 6.1 ile gelen `RestClient` önerilmektedir.

  * **`WebClient`:**
    * **Çalışma Modeli:** Spring WebFlux kütüphanesinin parçası olan asenkron, reaktif ve bloklamayan (non-blocking I/O) istemcidir. Arka planda Netty event-loop mimarisini kullanır.
    * **Yüksek Eşzamanlılık:** Bir thread HTTP isteğini gönderdikten sonra yanıtı beklemez; havuza geri döner ve başka bir isteği işler. Yanıt geldiğinde işletim sistemi kesmesiyle işlenir. Bu sayede az sayıda thread ile binlerce eşzamanlı istek yönetilebilir.
    * Hem reaktif akışları (`Mono`, `Flux`) hem de istendiğinde senkron bloklayan çağrıları (`.block()`) destekler.

  * **`OpenFeign` (Spring Cloud Feign):**
    * **Çalışma Modeli:** Deklaratif (bildirime dayalı) HTTP istemcisidir.
    * **Kullanım Kolaylığı:** Doğrudan HTTP bağlantı kodu veya URL manipülasyonu yazılmaz. Sadece bir Java interface'i ve standart Spring MVC anotasyonları (`@GetMapping`, `@PathVariable`) tanımlanır. Spring Cloud çalışma zamanında dinamik proxy oluşturur.
    * **Mikroservis Entegrasyonu:** Eureka (Service Discovery), Spring Cloud LoadBalancer ve Resilience4j (Circuit Breaker) ile doğal entegrasyona sahiptir.

  * **Neden "Modern" Kabul Edilirler?:**
    * `WebClient`, yüksek trafikli sistemlerde thread açlığını (thread starvation) engelleyip çok düşük kaynakla yüksek verim sağladığı için reaktif mimarinin standardıdır.
    * `Feign Client`, mikroservisler arası iletişimde boilerplate kodları yok ettiği, okunabilirliği artırdığı ve ekosistem araçlarıyla hazır entegre olduğu için modern enterprise projelerde standart kabul edilir.

```java
// OpenFeign Deklaratif Tanımlama Örneği
@FeignClient(name = "account-service", path = "/api/v1/accounts")
public interface AccountClient {

    @GetMapping("/{accountId}/balance")
    AccountBalanceDTO getBalance(@PathVariable("accountId") Long accountId);
}
```

---

### 12. Spring Boot – `application.yml` vs `application.properties` ve Profil Öncelikleri

**Soru:** Spring Boot – `application.yml` ile `application.properties` arasındaki fark nedir? Profile'a özel dosyalar (`application-prod.yml`) genel dosyanın (`application.yml`) üzerine nasıl öncelik kazanır?

* **Cevap:**
  * **Sözdizimi ve Yapısal Farklar:**
    * **`application.properties`:** Düz anahtar-değer (`key=value`) formatındadır. Aynı modül yapılandırmalarında ortak prefix'ler (`spring.datasource.url`, `spring.datasource.username`) her satırda tekrar yazılmak zorundadır.
    * **`application.yml` (YAML):** Boşluk ve girintilere (indentation) dayalı hiyerarşik bir ağaç yapısı sunar. Tekrarları önler, okunabilirliği çok daha yüksektir. Harita ve liste koleksiyonlarını tanımlamak çok daha doğaldır. Tek bir dosya içinde `---` ayıracı ile birden fazla profil yapılandırması barındırabilir.

  * **Profil Yükleme ve Üzerine Yazma (Override) Mekanizması:**
    * **Genel Yapılandırmanın Okunması:** Spring Boot ayağa kalkarken ilk olarak varsayılan genel konfigürasyonu (`application.yml`) okur ve Spring `Environment` nesnesi içerisine temel `PropertySource` olarak ekler.
    * **Aktif Profil Tespiti:** Uygulama aktif bir profile (`spring.profiles.active=prod` veya ortam değişkeni `SPRING_PROFILES_ACTIVE=prod`) sahipse, Spring Boot `application-prod.yml` dosyasını arar ve yükler.
    * **Öncelik Sıralaması:** Spring Boot'un `Environment` hiyerarşisinde profile özel dosyalar, genel dosyaların **üstüne** (daha yüksek öncelikli sıraya) yerleştirilir.
    * İki dosyada aynı anahtar (`server.port`) bulunuyorsa, profile özel dosyadaki değer genel dosyadaki değeri ezer (override eder). Sadece profile özel dosyada olanlar eklenir, genel dosyada olup profile özelde olmayanlar ise genel dosyadan miras alınır.

  * **Spring Boot Yapılandırma Öncelik Hiyerarşisi (Düşükten Yükseğe):**
    1. Varsayılan `application.yml`
    2. Profile özel `application-{profile}.yml`
    3. İşletim Sistemi Ortam Değişkenleri (OS Environment Variables - `SPRING_APPLICATION_JSON`)
    4. Komut Satırı Argümanları (Command Line Arguments - `--server.port=8081`)

---

### 13. Spring Boot – `@Retryable` (Spring Retry) ve Geçici Hata Yönetimi

**Soru:** Spring Boot – `@Retryable` (Spring Retry) nedir, ne zaman kullanılır? Bir dış servise yapılan çağrıda geçici (transient) hatalarda otomatik tekrar deneme nasıl kurgulanır?

* **Cevap:**
  * **`@Retryable` Nedir ve Ne Zaman Kullanılır?:**
    * Spring AOP (Aspect-Oriented Programming) tabanlı çalışan, metot çağrısı esnasında belirli istisnalar fırlatıldığında işlemi otomatik olarak yeniden deneyen bir mekanizmadır.
    * **Kullanım Alanı (Geçici / Transient Hatalar):** Ağ bağlantı kopmaları, soket zaman aşımları (`SocketTimeoutException`), veritabanı kilitlenme (`DeadlockLoserDataAccessException`) durumları, thread havuzu anlık dolulukları veya 3. parti servisin geçici olarak 503/429 dönmesi gibi durumlar.
    * **Kullanılmaması Gereken Alanlar:** 400 Bad Request, 401 Unauthorized, 404 Not Found, `NullPointerException` veya iş mantığı doğrulama hataları gibi tekrar denendiğinde sonucu değişmeyecek kalıcı hatalarda asla retry yapılmamalıdır.

  * **Temel Yapılandırma Bileşenleri:**
    * `@EnableRetry`: Konfigürasyon sınıfında Spring Retry altyapısını aktif eder.
    * `@Retryable`:
      * `retryFor` / `value`: Hangi exception türlerinde tekrar deneme yapılacağı.
      * `noRetryFor`: Hangi exception'larda derhal durulacağı.
      * `maxAttempts`: Maksimum deneme sayısı (varsayılan 3).
      * `backoff`: Denemeler arasındaki bekleme stratejisi (`delay` ile ilk bekleme, `multiplier` ile her denemede süreyi katlama - Exponential Backoff).
    * `@Recover`: Tüm denemeler tükendiğinde ve işlem hala başarısız olduğunda devreye giren kurtarma (fallback) metodudur. Orijinal metotla aynı dönüş tipine sahip olmalı ve ilk parametre olarak fırlatılan exception türünü almalıdır.

```java
@Service
public class PaymentGatewayService {

    @Retryable(
        retryFor = { RemoteServiceUnavailableException.class, SocketTimeoutException.class },
        noRetryFor = { InvalidCardException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2) // 2sn, ardından 4sn bekler
    )
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Ödeme servisi çağrılıyor...");
        return externalBankClient.charge(request);
    }

    // 3 deneme de başarısız olursa çalışacak fallback metodu:
    @Recover
    public PaymentResponse recoverPayment(RemoteServiceUnavailableException ex, PaymentRequest request) {
        log.error("Dış banka servisi yanıt vermiyor. Fallback devreye girdi. Hata: {}", ex.getMessage());
        return PaymentResponse.failed("Banka servisine şu anda ulaşılamıyor, işlem sıraya alındı.");
    }
}
```

---

### 14. Spring Boot – Custom Validation Anotasyonu (`@ValidTcKimlik`) ve `ConstraintValidator`

**Soru:** Spring Boot – Kendi custom validation annotation'ını (`@ValidTcKimlik` gibi) nasıl yazarsın? `ConstraintValidator` interface'i nasıl kullanılır?

* **Cevap:**
  * **Gereksinim ve Mimari:**
    * Standart Jakarta Validation (`@NotNull`, `@Size`, `@Pattern`) anotasyonlarının yetersiz kaldığı durumlarda algoritmik doğrulamaları (TC Kimlik No, Vergi No, IBAN vb.) temiz bir şekilde uygulamak için custom validation anotasyonları oluşturulur.
    * Bu süreç iki temel adımdan oluşur: Anotasyon tanımı ve doğrulayıcı (`ConstraintValidator`) sınıfı.

  * **1. Adım: Anotasyon Arayüzünün Tanımlanması:**
    * `@Target`: Anotasyonun hangi elemanlara (alan, metot parametresi) konulabileceğini belirtir.
    * `@Retention(RetentionPolicy.RUNTIME)`: Çalışma zamanında Reflection ile okunabilmesini sağlar.
    * `@Constraint(validatedBy = ...)`: Doğrulama mantığını çalıştıracak sınıfı bağlar.
    * Jakarta şartnamesi gereği `message()`, `groups()` ve `payload()` metotları zorunludur.

  * **2. Adım: `ConstraintValidator` Uygulaması:**
    * `ConstraintValidator<A, T>` arayüzü uygulanır (`A`: Anotasyon türü, `T`: Doğrulanacak alanın veri tipi).
    * `isValid(T value, ConstraintValidatorContext context)` metodu ezilerek iş kuralı işletilir. Bu sınıflar Spring Bean olduğu için içerisine `@Autowired` ile başka servisler de enjekte edilebilir.

```java
// 1. Adım: Anotasyonun Tanımlanması
@Documented
@Constraint(validatedBy = TcKimlikValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTcKimlik {
    String message() default "Geçersiz T.C. Kimlik Numarası formatı";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. Adım: Doğrulama Mantığının Yazılması
public class TcKimlikValidator implements ConstraintValidator<ValidTcKimlik, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() != 11 || !value.matches("^[1-9][0-9]{10}$")) {
            return false;
        }

        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = Character.getNumericValue(value.charAt(i));
        }

        // Kural 1: ( (1,3,5,7,9. basamakların toplamı * 7) - (2,4,6,8. basamakların toplamı) ) mod 10 = 10. basamak
        int oddSum = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
        int evenSum = digits[1] + digits[3] + digits[5] + digits[7];
        int tenthDigit = ((oddSum * 7) - evenSum) % 10;
        if (tenthDigit < 0) tenthDigit += 10;
        if (tenthDigit != digits[9]) return false;

        // Kural 2: İlk 10 basamağın toplamının mod 10'u = 11. basamak
        int firstTenSum = 0;
        for (int i = 0; i < 10; i++) firstTenSum += digits[i];
        return (firstTenSum % 10) == digits[10];
    }
}

// 3. Adım: DTO Üzerinde Kullanımı
public class CustomerRegisterDTO {
    @NotBlank
    @ValidTcKimlik
    private String tcKimlikNo;
    // ...
}
```

---

### 15. Spring Boot – Spring Security Filter Chain ve `PasswordEncoder` Güvenliği

**Soru:** Spring Boot – Spring Security'de filter chain nedir? PasswordEncoder (örn. BCryptPasswordEncoder) neden düz metin (plain text) şifre saklamaktan daha güvenlidir?

* **Cevap:**
  * **Spring Security Filter Chain Nedir?:**
    * Bir istemciden gelen HTTP isteğinin Controller katmanına ulaşmadan önce geçtiği sıralı güvenlik filtreleri zinciridir.
    * Servlet seviyesindeki `DelegatingFilterProxy`, gelen istekleri Spring Context'i içindeki `FilterChainProxy` nesnesine devreder (`SecurityFilterChain`).
    * Her filtre belirli bir güvenlik sorumluluğunu yerine getirir:
      * `CorsFilter` ve `CsrfFilter` (Kökenler arası istek ve sahtecilik koruması)
      * `UsernamePasswordAuthenticationFilter` veya `BearerTokenAuthenticationFilter` (Kimlik doğrulama)
      * `AuthorizationFilter` (Rol ve yetki denetimi)
    * Zincirdeki bir filtre başarısız olursa (örneğin token geçersizse), istek akışı derhal kesilir, Controller'a iletilmez ve uygun HTTP yanıtı (401 Unauthorized / 403 Forbidden) dönülür.

  * **`PasswordEncoder` (BCrypt) Neden Düz Metinden Üstündür?:**
    * Düz metin saklama durumunda veritabanı sızıntısı doğrudan tüm kullanıcı hesaplarının ele geçirilmesi demektir.
    * **1. Tek Yönlü Özetleme (One-Way Hashing):**
      * Şifreleme (Encryption) çift yönlüdür; gizli anahtarı olan kişi şifreyi çözebilir.
      * Hash fonksiyonları tek yönlü matematiksel dönüşümlerdir; hash çıktısından orijinal parolaya geri dönülemez. Doğrulama, kullanıcının girdiği parolanın aynı algoritmayla hash'lenip veritabanındaki hash ile kıyaslanması (`encoder.matches(raw, encoded)`) yoluyla yapılır.
    * **2. Dahili Rastgele Tuzlama (Salting):**
      * BCrypt her hash üretiminde rastgele bir tuz (`salt`) üretir ve bunu üretilen hash dizesinin içerisine gömer.
      * Aynı parolayı (örneğin "123456") kullanan 10 farklı kullanıcının veritabanındaki hash değerleri tamamen farklı olur. Bu özellik, önceden hesaplanmış parola tablolarıyla yapılan **Gökkuşağı Tablosu (Rainbow Table)** saldırılarını tamamen etkisiz kılar.
    * **3. Yavaşlatma ve İşlem Maliyeti (Key Stretching / Work Factor):**
      * MD5 veya SHA-256 gibi algoritmalar veri bütünlüğü için tasarlandıklarından nanosaniyeler içinde çalışır; bu da saniyede milyarlarca parola deneyen kaba kuvvet (brute-force) saldırılarını kolaylaştırır.
      * BCrypt kasıtlı olarak CPU-yoğun ve yavaşlatılabilen (adaptive) bir algoritmadır. Belirlenen logaritmik maliyet parametresi (varsayılan $2^{10} = 1024$ iterasyon) ile hash hesaplama süresi uzatılır (örneğin 80-100 ms). Bu süre tek bir kullanıcı için fark edilemezken, kaba kuvvet saldırısı yapan saldırganın donanım maliyetini katlanarak artırır.

---

### 16. Design Pattern – Adapter Pattern

**Soru:** Design Pattern – Adapter pattern nedir? Eski bir 3. parti bankacılık API'sinin response formatını, sizin sisteminizin beklediği formata çevirmek için nasıl kullanılır?

* **Cevap:**
  * **Adapter Pattern Nedir?:**
    * Birbiriyle uyumsuz arayüzlere (interface) sahip sınıfların bir arada çalışmasını sağlayan yapısal (structural) bir tasarım desenidir.
    * Mevcut istemci kodunu değiştirmeden, dışarıdan gelen yabancı veya eski bir servisi sistemin anladığı standart arayüze dönüştürür (tıpkı yabancı bir priz adaptörü gibi).

  * **Bileşenleri:**
    * **Target Interface:** Sistemimizin kabul ettiği ve bağımlı olduğu standart Java arayüzü.
    * **Adaptee:** Sisteme entegre edilmek istenen ancak metot adları veya veri tipleri uyumsuz olan 3. parti / eski servis.
    * **Adapter:** Target Interface'i uygulayan, Adaptee nesnesini bünyesinde barındıran (composition) ve gelen çağrıları dönüştürerek ileten köprü sınıf.
    * **Client:** Yalnızca Target Interface'i çağıran ana iş mantığı servisimiz.

```java
// 1. Target Interface: Bizim sistemimizin beklediği ortak model
public interface PaymentGateway {
    PaymentResult executePayment(BigDecimal amount, String currency);
}

// 2. Adaptee: Dış bankanın uyumsuz eski servisi (XML/Garip alan isimleri)
public class LegacyBankApi {
    public LegacyBankResponse sendTransaction(double tutar, int paraBirimiKodu) {
        // Eski banka çağrısı
        return new LegacyBankResponse(200, "ONAYLANDI_TX_987");
    }
}

// 3. Adapter: Eski servisi bizim arayüzümüze uyarlayan sınıf
@Component
public class LegacyBankAdapter implements PaymentGateway {
    private final LegacyBankApi legacyBankApi = new LegacyBankApi();

    @Override
    public PaymentResult executePayment(BigDecimal amount, String currency) {
        // Tip ve parametre dönüşümleri yapılır
        double tutar = amount.doubleValue();
        int paraBirimiKodu = currency.equalsIgnoreCase("TRY") ? 949 : 840;

        LegacyBankResponse response = legacyBankApi.sendTransaction(tutar, paraBirimiKodu);

        // Yanıt bizim ortak modelimize dönüştürülür (Mapping)
        boolean isSuccess = response.getStatusCode() == 200;
        return new PaymentResult(isSuccess, response.getTransactionCode());
    }
}
```

---

### 17. Design Pattern – Facade Pattern

**Soru:** Design Pattern – Facade pattern nedir? Birden fazla alt servisi (fraud check, limit check, notification) tek bir basit arayüz arkasında toplamak için nasıl kullanılır?

* **Cevap:**
  * **Facade Pattern Nedir?:**
    * Karmaşık, çok sayıda alt bileşenden veya alt servisten oluşan bir alt sistemi tek ve sade bir arayüz (yüzey / cephe) arkasında toplayan yapısal (structural) tasarım desenidir.
    * İstemciyi (Client) alt servislerin karmaşık detaylarından, çalışma sıralarından ve bağımlılıklarından kurtarır (Loose Coupling).

  * **Bankacılık Senaryosunda Kullanımı:**
    * Bir para transferi işlemi arka planda Sahtekarlık Kontrolü (`FraudCheckService`), Limit Kontrolü (`LimitService`), Hesap Bakiyesi Çekme/Yatırma (`AccountService`) ve SMS Bildirimi (`NotificationService`) adımlarını gerektirir.
    * Client'ın bu servisleri tek tek bilmesi ve yönetmesi yerine `TransferFacade` sınıfı tüm bu orkestrasyonu tek bir metot arkasında toplar.

```java
@Service
@RequiredArgsConstructor
public class TransferFacade {

    private final FraudCheckService fraudService;
    private final LimitCheckService limitService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    @Transactional
    public TransferResult transferMoney(Long senderId, Long receiverId, BigDecimal amount) {
        // 1. Adım: Sahtekarlık kontrolü
        if (fraudService.isSuspicious(senderId, amount)) {
            throw new SuspiciousActivityException("İşlem şüpheli bulundu");
        }

        // 2. Adım: Müşteri limit kontrolü
        if (!limitService.hasAvailableDailyLimit(senderId, amount)) {
            throw new LimitExceededException("Günlük transfer limiti aşıldı");
        }

        // 3. Adım: Bakiye transferi (ACID işlem)
        accountService.debit(senderId, amount);
        accountService.credit(receiverId, amount);

        // 4. Adım: Asenkron bildirim gönderimi
        notificationService.sendSms(senderId, "Transfer gerçekleşti: " + amount);

        return TransferResult.success();
    }
}
```

---

### 18. Design Pattern – Repository (DAO) Pattern ve Spring Data JPA

**Soru:** Design Pattern – Repository (DAO) pattern nedir? Spring Data JPA'nın JpaRepository interface'i bu pattern'i nasıl otomatikleştirir?

* **Cevap:**
  * **Repository Pattern Nedir?:**
    * Domain Driven Design (DDD) yaklaşımının temel yapı taşlarından biridir.
    * İş mantığı (domain logic) katmanı ile veritabanı erişim katmanı arasına soyut bir arayüz koyar.
    * Veritabanını, bellek üzerinde yaşayan bir nesne koleksiyonu (`List`, `Set`) gibi simüle eder (`findById`, `save`, `delete`).

  * **Çözdüğü Problemler:**
    * SQL sorgularının ve veritabanı API detaylarının iş mantığı sınıflarına sızmasını engeller.
    * Veri tabanı teknolojisi değiştiğinde (örneğin SQL'den NoSQL'e geçiş) iş katmanı kodlarının değişmeden kalmasını sağlar.
    * Birim testlerde (Unit Test) veritabanına bağlanmadan Repository arayüzünün kolayca taklit edilmesini (Mocking) mümkün kılar.

  * **Spring Data JPA Bu Pattern'i Nasıl Otomatikleştirir?:**
    * Geliştirici yalnızca `public interface UserRepository extends JpaRepository<User, Long>` şeklinde bir arayüz tanımlar ve hiçbir somut sınıf (implementation) yazmaz.
    * Spring ayağa kalkarken Java Dynamic Proxy mekanizması ile bu arayüzü çalışma zamanında dinamik olarak uygular.
    * Arka planda hazır olarak `SimpleJpaRepository<T, ID>` sınıfını çalıştırır; bu sınıf da JPA `EntityManager` API'sini kullanarak standart CRUD işlemlerini yönetir.
    * `findByEmailAndStatus(...)` gibi türetilmiş metot isimlerini (Derived Query Methods) otomatik olarak parse ederek JPQL/SQL sorgularına dönüştürür.

---

### 19. Mikroservis – Database per Service Pattern ve Dağıtık JOIN Zorluğu

**Soru:** Mikroservis/Kavram – "Database per service" pattern nedir? İki farklı mikroservisin (Customer Servisi ve Account Servisi) veritabanları ayrıysa, birbirine ait veriyi JOIN ile sorgulayamamanın getirdiği zorluk nasıl çözülür?

* **Cevap:**
  * **Database per Service Nedir?:**
    * Her mikroservisin kendi veri modeline ve tamamen izole edilmiş bağımsız veritabanına sahip olması mimarisidir.
    * Bir servis başka bir servisin veritabanı tablolarına doğrudan bağlanamaz; veri paylaşımı yalnızca servislerin API'leri (REST/gRPC) veya mesaj kuyrukları (Kafka/RabbitMQ) üzerinden gerçekleşir.
    * Servislerin birbirine gevşek bağlanmasını (loose coupling) ve bağımsız olarak ölçeklenip dağıtılabilmesini sağlar.

  * **JOIN Yapılamama Zorluğunun Çözüm Yaklaşımları:**
    * **1. API Composition (İstemci / Gateway Seviyesinde Birleştirme):**
      * Bir API Gateway veya BFF (Backend for Frontend) servisi hem Customer hem de Account servisine ayrı ayrı HTTP/gRPC isteği atar.
      * Gelen sonuçlar bellekte (in-memory) birleştirilerek istemciye tek bir model olarak dönülür.
      * *Dezavantajı:* Büyük veri setlerinde ağ gecikmesi, N+1 problemine yol açması ve yüksek bellek tüketimi.
    * **2. CQRS (Command Query Responsibility Segregation) + Event-Driven Projeksiyon (Modern Yaklaşım):**
      * Yazma (Command) ve okuma (Query) veritabanları birbirinden ayrılır.
      * Customer veya Account servisinde bir güncelleme olduğunda dışarıya bir domain event fırlatılır (`CustomerUpdatedEvent`, `AccountCreatedEvent`).
      * Ayrı bir "Raporlama / Arama Servisi" bu Kafka event'lerini dinler ve iki servisten gelen verileri optimize edilmiş denormalize bir okuma veritabanında (Elasticsearch veya Read-Replica PostgreSQL) birleştirilmiş olarak saklar.
      * Kullanıcı sorgu attığında servisler arası JOIN veya API çağrısı yapılmaz; tek bir sorguyla birleşik veri kaynağından milisaniyeler içinde okunur.
      * Bu modelde nihai tutarlılık (**Eventual Consistency**) kabul edilir.
    * **Bankacılıkta Hibrit Uygulama:**
      * Para transferi ve bakiye değişimi gibi anlık doğruluk gerektiren operasyonlar senkron ve ilişkisel ACID veritabanında çalıştırılır.
      * Müşterinin "Hesap Geçmişi ve Hareketleri Listesi" gibi okuma odaklı raporlama ekranları CQRS ile Elasticsearch üzerinden sunulur.

---

### 20. Mikroservis – Feature Flag / Toggle ve Kademeli Açılış (Canary)

**Soru:** Mikroservis/Kavram – Feature flag / feature toggle nedir? Yeni bir faiz hesaplama algoritmasını tüm müşterilere aynı anda değil, kademeli olarak (örn. %5 müşteriye) açmak istersen nasıl bir yaklaşım izlersin?

* **Cevap:**
  * **Feature Flag (Feature Toggle) Nedir?:**
    * Kaynak koda dokunmadan ve yeni bir sürüm dağıtımı (deployment) yapmadan, bir özelliğin veya iş mantığının çalışma zamanında uzaktan dinamik olarak açılıp kapatılabilmesini sağlayan tekniktir.

  * **%5 Kademeli Açılış (Canary / Gradual Rollout) Yaklaşımı:**
    * **1. Deterministik ve Yapışkan Dağıtım (Consistent Hashing):**
      * Seçilen müşterilerin her istekte hep aynı algoritmayı görmesi gerekir (tutarlılık / stickiness). Rastgele (`Math.random()`) seçim yapılmaz.
      * Müşterinin tekil kimlik numarası (`customerId` veya `accountId`) bir hash fonksiyonuna sokulur ve modülo 100 işlemi uygulanır:
        `int bucket = Math.abs(customerId.hashCode()) % 100;`
      * `bucket < 5` koşulunu sağlayan müşteriler yeni algoritmaya, diğerleri mevcut eski algoritmaya yönlendirilir.
    * **2. Konfigürasyon Yönetimi (Merkezi Flag Servisi):**
      * Açılış yüzdesi hard-coded yazılmaz; Unleash, LaunchDarkly veya Spring Cloud Config gibi merkezi bir konfigürasyon sunucusundan dinamik olarak çekilir.
    * **3. İzleme ve Geri Alma (Telemetry & Instant Rollback):**
      * Yeni algoritmanın çalıştığı %5'lik grubun hata logları, hesaplama sapmaları ve sistem metrikleri (Prometheus/Grafana) yakından izlenir.
      * Beklenmeyen bir anomalide flag değeri deploy gerekmeksizin saniyeler içinde `%0` yapılarak sistem eski kararlı duruma döndürülür.
      * Sorun görülmezse kademeli olarak `%20`, `%50` ve `%100` seviyesine çıkarılır.

```java
@Service
@RequiredArgsConstructor
public class InterestCalculationService {

    private final FeatureToggleService featureToggleService;
    private final LegacyInterestCalculator legacyCalculator;
    private final ModernInterestCalculator modernCalculator;

    public BigDecimal calculateInterest(Long customerId, BigDecimal principal) {
        // Merkezi flag değeri: Örneğin 5 (%5)
        int rolloutPercentage = featureToggleService.getInterestV2RolloutPercentage();

        if (isEligibleForNewAlgorithm(customerId, rolloutPercentage)) {
            return modernCalculator.calculate(principal);
        } else {
            return legacyCalculator.calculate(principal);
        }
    }

    private boolean isEligibleForNewAlgorithm(Long customerId, int percentage) {
        if (percentage <= 0) return false;
        if (percentage >= 100) return true;
        return (Math.abs(customerId.hashCode()) % 100) < percentage;
    }
}
```

---

### 21. Algoritma 1 – In-Place Dizi Ters Çevirme (Two-Pointer)

**Soru:** Bir işlem geçmişi dizisini, ekstra bir dizi kullanmadan (in-place, $O(1)$ alan) ters çeviren bir fonksiyon yaz.
`void reverseTransactionLog(int[] transactions)`

* **Çözüm Yaklaşımı:**
  * Biri dizinin başında (`left = 0`), diğeri dizinin sonunda (`right = length - 1`) konumlanan iki işaretçi (two-pointer) kullanılır.
  * İşaretçiler birbirini geçene kadar (`left < right`), karşılıklı elemanlar geçici bir değişken (`temp`) yardımıyla takas edilir (swap) ve işaretçiler merkeze doğru kaydırılır.

* **Karmaşıklık Analizi:**
  * **Zaman Karmaşıklığı:** $O(N)$ – Dizinin yarısı kadar iterasyon yapılır ($N/2$ işlem).
  * **Alan Karmaşıklığı:** $O(1)$ – Ekstra bir bellek alanı/dizi tahsis edilmez, yerinde (in-place) çalışır.

```java
public class TransactionUtils {

    public static void reverseTransactionLog(int[] transactions) {
        if (transactions == null || transactions.length <= 1) {
            return;
        }

        int left = 0;
        int right = transactions.length - 1;

        while (left < right) {
            int temp = transactions[left];
            transactions[left] = transactions[right];
            transactions[right] = temp;

            left++;
            right--;
        }
    }
}
```

---

### 22. Algoritma 2 – ATM Minimum Banknot Problemi (Coin Change)

**Soru:** Belirli banknot değerleri (`{200, 100, 50, 20, 10}` gibi) verildiğinde, istenen tutarı en az sayıda banknotla ödemenin yolunu bulan bir fonksiyon yaz. (Bonus: Neden basit bir "greedy" yaklaşımın her banknot sistemi için doğru sonuç vermeyebileceğini bir örnekle açıkla.)

* **Çözüm Yaklaşımı ve Analiz:**
  * **Greedy (Açgözlü) Yaklaşım Neden Her Zaman Çalışmaz?:**
    * Türk Lirası veya Euro gibi sistemler **kanonik (canonical) para sistemleridir**. Büyük banknotlar küçük banknotların katları şeklinde dengeli dağıldığı için greedy yaklaşım daima küresel optimumu verir.
    * Ancak kanonik olmayan keyfi bir banknot setinde greedy hatalı sonuç verir:
      * Örneğin eldeki banknotlar: `{4, 3, 1}` ve istenen tutar: `6`.
      * **Greedy Yaklaşım:** Önce en büyük banknot olan 4'ü alır, kalan 2 için 1 + 1 alır $\to$ Toplam **3 banknot** (`4 + 1 + 1`).
      * **Gerçek Optimum Çözüm:** İki adet 3'lük banknot $\to$ Toplam **2 banknot** (`3 + 3`).
    * Bu nedenle keyfi veya garantilenmemiş banknot sistemlerinde en doğru yöntem **Dinamik Programlama (Bottom-Up DP)** yöntemidir.

#### Çözüm A: Genel ve Kesin Çözüm – Dinamik Programlama (DP)
* **Zaman Karmaşıklığı:** $O(\text{amount} \times N)$
* **Alan Karmaşıklığı:** $O(\text{amount})$

```java
public class AtmDispenser {

    public static int minNotesForAmountDP(int[] denominations, int amount) {
        if (amount < 0) return -1;
        if (amount == 0) return 0;

        int[] dp = new int[amount + 1];
        // Ulaşılamaz değer olarak amount + 1 atanır (Integer.MAX_VALUE taşma riskini önler)
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int note : denominations) {
                if (i >= note) {
                    dp[i] = Math.min(dp[i], dp[i - note] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }
}
```

#### Çözüm B: Kanonik Para Birimleri İçin Hızlı Çözüm – Greedy (Açgözlü)
* **Zaman Karmaşıklığı:** $O(N \log N)$ (Sıralama) + $O(N)$ (Dağıtım)
* **Alan Karmaşıklığı:** $O(1)$

```java
public class AtmDispenserGreedy {

    public static int minNotesForAmountGreedy(int[] denominations, int amount) {
        if (amount <= 0) return 0;

        // Banknotları büyükten küçüğe sırala
        Integer[] sortedNotes = Arrays.stream(denominations)
                .boxed()
                .sorted(Collections.reverseOrder())
                .toArray(Integer[]::new);

        int count = 0;
        int remaining = amount;

        for (int note : sortedNotes) {
            if (remaining >= note) {
                count += remaining / note;
                remaining %= note;
            }
            if (remaining == 0) break;
        }

        return remaining == 0 ? count : -1; // Tam ödenemiyorsa -1 döner
    }
}
```

---

### 23. Algoritma 3 – En Uzun Zararsız Dönem (Sliding Window)

**Soru:** Bir hesabın günlük net kâr/zarar değerlerini temsil eden dizide, hiç zarar (negatif değer) içermeyen en uzun ardışık gün aralığının uzunluğunu bulan bir fonksiyon yaz.
`int longestNoLossStreak(int[] dailyNetChange)`

* **Çözüm Yaklaşımı:**
  * Dizi üzerinde tek bir döngüyle gezinilir.
  * Eleman sıfır veya pozitif olduğu sürece (`>= 0`) mevcut ardışık seri (`currentStreak`) 1 artırılır ve en büyük değer (`maxStreak`) güncellenir.
  * Negatif bir değerle karşılaşıldığı anda seri bozulur ve `currentStreak = 0` yapılarak sayaç sıfırlanır.

* **Karmaşıklık Analizi:**
  * **Zaman Karmaşıklığı:** $O(N)$ – Dizi baştan sona yalnızca bir kez taranır.
  * **Alan Karmaşıklığı:** $O(1)$ – Sadece iki ilkel sayaç değişkeni tutulur.

```java
public class FinancialMetrics {

    public static int longestNoLossStreak(int[] dailyNetChange) {
        if (dailyNetChange == null || dailyNetChange.length == 0) {
            return 0;
        }

        int maxStreak = 0;
        int currentStreak = 0;

        for (int change : dailyNetChange) {
            if (change >= 0) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 0; // Zarar görüldü, seri sıfırlandı
            }
        }

        return maxStreak;
    }
}
```

---

### 24. Algoritma 4 – Palindrome Referans Kodu Kontrolü (Two-Pointer)

**Soru:** Bir işlem referans kodunun (String) baştan ve sondan aynı okunup okunmadığını (palindrome) kontrol eden bir fonksiyon yaz — Set/ekstra dizi kullanmadan, iki pointer ile.
`boolean isPalindromeReference(String referenceCode)`

* **Çözüm Yaklaşımı:**
  * Biri String'in başında (`left = 0`), diğeri son karakterinde (`right = length - 1`) bulunan iki işaretçi kullanılır.
  * Karakterler eşit olduğu sürece `left` sağa, `right` sola doğru kaydırılır.
  * Herhangi bir adımda karakterler uyuşmazsa doğrudan `false` dönülür; işaretçiler karşılaştığında döngü biterse `true` dönülür.

* **Karmaşıklık Analizi:**
  * **Zaman Karmaşıklığı:** $O(N)$ – Metnin en fazla yarısı kadar karakter kıyaslaması yapılır.
  * **Alan Karmaşıklığı:** $O(1)$ – İlave hiçbir String veya koleksiyon nesnesi oluşturulmaz.

```java
public class ReferenceValidator {

    public static boolean isPalindromeReference(String referenceCode) {
        if (referenceCode == null) {
            return false;
        }
        if (referenceCode.length() <= 1) {
            return true;
        }

        int left = 0;
        int right = referenceCode.length() - 1;

        while (left < right) {
            if (referenceCode.charAt(left) != referenceCode.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }
}
```

---

### 25. Senaryo 1 – Optimistic Locking Çakışması ve UX Yönetimi

**Soru:** Bir hesap güncelleme ekranında, iki farklı banka çalışanı aynı müşteri kaydını aynı anda açıp düzenliyor. `@Version` alanı sayesinde ikinci kaydeden `OptimisticLockException` alıyor. Bu hatayı kullanıcıya nasıl (hangi mesaj/UX ile) yansıtırsın? Otomatik retry mantıklı mı, yoksa kullanıcıya mı bırakılmalı?

* **Çözüm ve Mimari Değerlendirme:**
  * **Otomatik Retry Neden Mantıksızdır?:**
    * Form üzerinden kullanıcı etkileşimiyle yapılan güncellemelerde otomatik retry uygulanması **veri kaybına ve sessiz ezilmeye (silent overwrite / lost update)** yol açar.
    * İlk çalışanın yaptığı değişiklikler (örneğin telefon numarası ve adres güncellemesi), ikinci çalışanın eski ekran üzerinden yaptığı güncelleme otomatik tekrarlandığında sessizce ezilir.
    * *(İstisna: Otomatik retry yalnızca arayüzsüz sayaç artırımı, bakiye düşümü gibi kullanıcı niyetinin ezilmeyeceği matematiksel arka plan operasyonlarında mantıklıdır).*

  * **Uçtan Uca Hata ve UX Yönetimi:**
    * **1. HTTP Katmanı:** Backend servis `OptimisticLockException` yakalandığında istemciye HTTP `409 Conflict` durum kodunu ve detaylı hata gövdesini döner.
    * **2. Git Çakışma Mantığına Benzer Karşılaştırma Ekranı (Diff Modal):**
      * İkinci banka çalışanının önüne doğrudan "Hata oluştu" deyip formu kapatmak yerine bir karşılaştırma ekranı açılır.
      * Ekranda üç sütun gösterilir:
        1. *Kullanıcının Göndermeye Çalıştığı Değerler*
        2. *Diğer Çalışan Tarafından Az Önce Kaydedilen Güncel Değerler*
        3. *Çakışan alanların renkli vurgusu*
    * **3. Kullanıcıya Seçenek Sunulması:**
      * **"Güncel Veriyi Yükle ve Yeniden İncele":** Sayfayı son kaydedilen veritabanı haliyle yeniler, çalışanın kendi değişikliklerini tekrar yapmasını ister.
      * **"Değişikliklerimi Zorla (Force Overwrite)":** Yalnızca yetkili süpervizör onayına tabi olarak güncel sürüm üzerine zorla yazma imkanı sunar.

---

### 26. Senaryo 2 – Transaction Tablosunun Second-Level Cache'e Alınması Hatası

**Soru:** Bir geliştirici, performans için TRANSACTION (işlem) tablosunu second-level cache'e almış. Bir süre sonra müşteriler güncel bakiyelerini göremiyor, eski işlem verileri dönüyor. Bu neden yanlış bir tercih? Hangi tür veriler second-level cache için uygundur, hangileri kesinlikle uygun değildir?

* **Çözüm ve Mimari Değerlendirme:**
  * **Neden Yanlış Bir Tercihtir?:**
    * Finansal işlem (`Transaction`) tabloları doğası gereği yoğun yazma işlemi alan (**write-heavy**) ve yüksek oranda anlık ekleme (`INSERT`) / güncelleme barındıran tablolardır.
    * Bir kaydı güncellediğinizde veya yeni işlem girdiğinizde L2 önbelleği geçersiz kılmak (cache invalidation) gerekir. Sürekli geçersiz kılınan bir önbellek hiçbir performans kazancı sağlamaz; aksine kilitleme ve senkronizasyon maliyetleriyle sistemi daha da yavaşlatır.
    * Eğer geçersiz kılma gecikirse veya kümeleme ortamında replike olamazsa, müşteriler bayat (stale) veriler görür; bu durum mükerrer para çekme, bakiye tutarsızlığı ve doğrudan mali kayıplarla sonuçlanır.

  * **Second-Level Cache İçin Kesinlikle Uygun Olmayan Veriler:**
    * Müşteri hesap bakiyeleri ve bakiye hareketleri.
    * Finansal işlem ve para transferi kayıtları.
    * Sipariş durumları ve anlık stok hareketleri.
    * Zaman damgasına duyarlı log ve denetim (audit) kayıtları.

  * **Second-Level Cache İçin Mükemmel Uygunluktaki Veriler:**
    * Okuma/Yazma oranı çok yüksek olan (**Read-Heavy**) veriler (Okuma sıklığı güncelleme sıklığının yüzlerce katı olan tablolar).
    * Döviz kurları tablosu (Günde bir veya belirli saatlerde güncellenir, sürekli okunur).
    * Ülke, şehir, ilçe ve posta kodu referans tabloları.
    * Sistem rol, yetki ve güvenlik izinleri tanımları.
    * Sabit ürün kategorileri ve sistem parametreleri.

---

### 27. Senaryo 3 – `ConcurrentModificationException` Prod Hatası Çözümleri

**Soru:** Prod ortamında, günlük batch işleminde bir `List<Transaction>` üzerinde gezerken belirli koşulu sağlamayan kayıtları listeden silen bir kod `ConcurrentModificationException` fırlatıp çöküyor. Bu hatayı nasıl düzeltirsin (`Iterator.remove()`, `removeIf()`, ayrı bir liste kullanma gibi seçenekleri değerlendir)?

* **Çözüm Seçeneklerinin Değerlendirilmesi ve Karşılaştırması:**

  * **1. Seçenek: Java 8+ `Collection.removeIf()` (En İdeal ve Performanslı Çözüm):**
    * **Nasıl Çalışır?:** Listenin kendi implementasyonunu kullanır (`ArrayList` içinde özel olarak optimize edilmiştir).
    * **Performans:** $O(N)$ zaman karmaşıklığı ile çalışır. Döngü sırasında elemanları tek tek kaydırmak yerine, silinmeyecek elemanları tespit edip dizi içinde tek geçişte sola kaydırır.
    * **Tercih:** Batch işlemler için okunabilirliği en yüksek ve en hızlı çözümdür.

  * **2. Seçenek: `Iterator.remove()` (Klasik ve Güvenli Çözüm):**
    * **Nasıl Çalışır?:** Liste üzerinden değil, açıkça oluşturulan `Iterator` nesnesi üzerinden gezinilir ve `iterator.remove()` çağrılır. İteratör kendi `expectedModCount` değerini güncellediği için istisna fırlatılmaz.
    * **Performans:** Standart `ArrayList` üzerinde her silme işleminde `System.arraycopy` ile sağdaki elemanlar tek tek sola kaydırıldığı için $O(N^2)$ zaman alabilir. Milyonlarca kayıt içeren büyük batch listelerde yavaşlık yaratabilir.

  * **3. Seçenek: Akış (Stream) ile Yeni Bir Liste Üretmek (Fonksiyonel Yaklaşım):**
    * **Nasıl Çalışır?:** Orijinal listeyi yerinde değiştirmek yerine filtreleme yapılarak yeni bir liste üretilir:
      `List<Transaction> validTransactions = transactions.stream().filter(t -> !shouldRemove(t)).toList();`
    * **Performans:** $O(N)$ zaman alır; ancak bellekte yeni bir liste oluşturulduğu için ekstra bellek tahsisi ($O(N)$ space) gerektirir. Bellek kısıtı olmayan batch işlemlerde yan etkisiz (immutable) yapısıyla güvenlidir.

  * **4. Seçenek: `CopyOnWriteArrayList` Değerlendirmesi:**
    * Eşzamanlı (multi-thread) ortamlarda gezinirken güvenlidir ancak her yazma/silme işleminde dizinin kopyasını çıkardığı için $O(N)$ kopyalama maliyeti yaratır.
    * Çok sayıda silme işlemi içeren **büyük batch işlemlerde KESİNLİKLE KULLANILMAMALIDIR**; bellek patlamasına (OutOfMemoryError) ve devasa CPU yüküne yol açar.

```java
// BATCH İŞLEM İÇİN EN DOĞRU VE HIZLI ÇÖZÜM:
transactions.removeIf(transaction -> transaction.getStatus() == TransactionStatus.FAILED);
```
