İstediğiniz notlar, kavramsal ve sözdizimsel hatalar düzeltilerek ve eksik kısımlar detaylandırılarak aşağıda düzenlenmiştir.

## Java – Interface ile Abstract Class Arasındaki Fark ve Java 8 Etkisi

**Interface:**

* Bir sınıf birden fazla interface'i implement edebilir (Çoklu kalıtım yeteneği sağlar).
* Temel amacı alt sınıflara bir sözleşme (contract) sunmaktır. Sınıfın "ne yapması gerektiğini" belirtir.
* Durum (state) tutamazlar; instance variable barındıramazlar (sadece `public static final` sabitler).

**Abstract Class (Soyut Sınıf):**

* Bir sınıf yalnızca tek bir abstract sınıfı kalıtım (extends) yoluyla alabilir.
* Amacı, birbiriyle yakından ilişkili nesneler arasında ortak davranışları (metotları) ve durumları (alanları) paylaşmaktır.
* İçerisinde hem soyut (gövdesiz) hem de somut (gövdeli) metotlar, kurucular (constructors) ve değişkenler barındırabilir.

**Java 8 Sonrası Etkiler:**

* **Default Metotlar:** Interface'lere geçmişe dönük uyumluluk (backward compatibility) sağladı. Mevcut interface'e yeni bir metot eklendiğinde, bu interface'i implemente eden eski sınıfların patlamasını engeller. İstenirse alt sınıfta override edilir, istenirse default haliyle kullanılır.
* **Static Metotlar:** Java 8 öncesinde yardımcı işlemler için ayrı Utility sınıfları (`Collections`, `Arrays` vb.) açmak zorunluydu. Artık utility metotları doğrudan interface içinde tanımlanarak gereksiz sınıf kalabalığı önlenmektedir.

---

## Java – Optional Kullanım Amacı ve Avantajları

**Optional:** Bir metodun dönüş değerinin mevcut olabileceğini (bir nesne) veya hiç olmayabileceğini (boş) açıkça ifade etmek için tasarlanmış bir kapsayıcı (wrapper) sınıftır. En büyük avantajı, geliştiriciyi dönen değeri kontrol etmeye zorlayarak `NullPointerException` (NPE) hatalarını compile-time seviyesinde engellemeye yardımcı olmasıdır.

**Kötü Kullanım Örnekleri (Anti-Patterns):**

1. **Metot Parametresi Olarak Kullanmak:** `Optional` serileştirilemez (Serializable arayüzünü uygulamaz) ve sınıf alanlarında (field) veya metot parametrelerinde kullanmak gereksiz bellek/performans maliyeti yaratır. Yalnızca dönüş tipi (return type) olarak tasarlanmıştır.
2. **Optional Döndüren Metotta `null` Döndürmek:** Bir metot `Optional` dönüyorsa ve değer yoksa asla `null` döndürülmemelidir; bunun yerine `Optional.empty()` döndürülmelidir.
3. **Gereksiz Yere `Optional.of()` Kullanmak:** Değerin `null` olabilme ihtimali varsa `Optional.ofNullable(deger)` kullanılmalıdır. `Optional.of(null)` doğrudan NPE fırlatır.

---

## SQL – Subquery ile JOIN Arasındaki Fark ve Performans

**Subquery (Alt Sorgu):**

* Özellikle *ilişkili (correlated)* alt sorgularda, üst sorgunun döndürdüğü her bir satır için alt sorgu tekrar tekrar çalışır (N+1 problemi).
* İç içe for döngüsü gibi hareket ettiği için büyük veri setlerinde ciddi performans kayıplarına yol açar.

**JOIN:**

* Veritabanı motorunun (Query Optimizer) en iyi optimize ettiği işlemlerden biridir.
* İki veya daha fazla tabloyu tek bir aşamada birleştirerek tüm eşleşen satırları tek bir sorgu planı ile (Hash Join, Merge Join vb. algoritmalar kullanarak) getirir.
* Bu nedenle, aynı sonucu üreten bir sorguda JOIN kullanımı, belleği ve işlemciyi çok daha verimli kullandığı için neredeyse her zaman Subquery'ye tercih edilir.

---

## SQL – Database Normalizasyonu ve Denormalizasyon

Normalizasyon, veritabanındaki veri tekrarını (redundancy) önlemek ve veri bütünlüğünü sağlamak için uygulanan kurallar bütünüdür.

* **1NF (Birinci Normal Form):** Tablodaki tüm alanlar atomik olmalıdır. Bir hücrede birden fazla değer (virgülle ayrılmış liste vb.) bulunamaz.
* **2NF (İkinci Normal Form):** Tablo 1NF olmalıdır. Kompozit (birden fazla kolondan oluşan) bir Primary Key varsa, tablodaki diğer tüm kolonlar bu anahtarın *tamamına* bağımlı olmalıdır (kısmi bağımlılık olamaz).
* **3NF (Üçüncü Normal Form):** Tablo 2NF olmalıdır. Primary Key olmayan hiçbir kolon, yine Primary Key olmayan başka bir kolona bağımlı olamaz (geçişli bağımlılık - transitive dependency kabul edilmez).

**Denormalizasyon ve Bankacılık Kullanımı:**
Normalizasyon veri ekleme, güncelleme ve silme (DML) işlemlerinde tutarlılık sağlarken, okuma anında çok sayıda JOIN gerektirdiği için okuma performansını düşürür.
Bankacılık gibi sistemlerde (özellikle raporlama, hesap özetleri, data warehouse süreçleri) okuma performansını artırmak için bilinçli olarak veri tekrarına izin verilir (Denormalizasyon). Sadece NoSQL (Redis, ElasticSearch) değil, ilişkisel veritabanlarında da ağır okuma yükünü hafifletmek için hesaplanmış kolonlar veya önceden birleştirilmiş tablolar kullanılır.

---

## Hibernate – Dirty Checking ve @DynamicUpdate

**Dirty Checking:** Hibernate'in, *Persistent Context* (yönetilen durum) içindeki entity nesnelerinde yapılan değişiklikleri otomatik olarak takip etme mekanizmasıdır. Geliştiricinin her değişiklikten sonra manuel olarak `repository.save(entity)` çağırmasına gerek kalmaz; transaction kapanırken Hibernate arka planda veritabanına `UPDATE` sorgusunu otomatik gönderir.

* **Nasıl Çalışır:** Entity veritabanından çekildiğinde Hibernate bunu L1 Cache'e (1. Seviye Ön Bellek) alır ve bir kopyasını (snapshot) saklar. Transaction biterken (flush anında) mevcut nesne ile snapshot karşılaştırılır. Fark (dirty state) varsa SQL güncellemesi oluşturulur.
* **@DynamicUpdate:** Varsayılan olarak Hibernate, entity içinde sadece tek bir alan değişse bile UPDATE sorgusuna tablodaki tüm kolonları yazar. `@DynamicUpdate` kullanıldığında, Hibernate sorguyu çalışma anında oluşturur ve *sadece değişen alanları* UPDATE sorgusuna dahil eder. Çok fazla kolonu olan tablolarda ağ ve veritabanı maliyetini düşürür, ancak sorgunun dinamik oluşturulma süresi nedeniyle çok küçük tablolarda gereksiz maliyet yaratabilir.

---

## Hibernate – @Embeddable ve @Embedded

Bu anotasyonlar, tek başına veritabanında ayrı bir tablo (ve dolayısıyla kendi Primary Key'i) olmayı hak etmeyen, ancak nesne yönelimli programlamada (OOP) bir arada tutulması mantıklı olan veri gruplarını (örn. Adres, İletişim Bilgileri) modellemek için kullanılır.

* **@Embeddable:** Gömülecek olan sınıfı (`Address`) işaretler.
* **@Embedded:** Entity sınıfı (`Customer`) içerisinde bu sınıfın kullanılacağını belirtir. Bu sayede ayrı bir tablo ve JOIN maliyeti oluşmaz, `Address` sınıfının alanları `Customer` tablosuna kolon olarak eklenir.

**Birden Fazla Adres Durumu:**
Eğer bir müşterinin birden fazla adresi olacaksa, `@Embedded` doğrudan kullanılamaz. Bu durumda iki seçenek vardır:

1. **@ElementCollection:** Ayrı bir varlık (entity) oluşturmadan, basit bir alt tablo oluşturarak adresleri tutmak.
2. **One-to-Many İlişkisi:** `Address` bilgisini kendi ID'si olan ayrı bir Entity haline getirip JOIN ile bağlamak (Gerçek dünyada en çok tercih edilen yöntem).

---

## Spring Boot – AOP (Aspect Oriented Programming)

**AOP:** Uygulamanın ana iş mantığından (business logic) bağımsız olan, ancak sistemin birçok yerinde tekrarlanan işlemleri (loglama, güvenlik, yetkilendirme, transaction yönetimi - *cross-cutting concerns*) merkezi bir yerden yönetmeyi sağlayan programlama paradigmasıdır.

**Temel Kavramlar:**

* **Aspect:** Çapraz kesen kodların (örn. loglama sınıfı) modül haline getirilmiş halidir.
* **Join Point:** AOP kurallarının uygulanabileceği, uygulamanın çalışma anındaki belirli noktalardır (Spring AOP'de bu genellikle metotların çalışmasıdır).
* **Pointcut:** Aspect'in hangi Join Point'lerde (hangi metotlarda veya paketlerde) devreye gireceğini belirleyen filtreleme (regex/expression) kuralıdır.
* **Advice:** Belirlenen Pointcut'ta kodun tam olarak ne zaman çalışacağını belirten anotasyonlardır:
* `@Before`: Hedef metot çalışmadan *önce*.
* `@After`: Hedef metot çalıştıktan *sonra* (başarılı veya hata fırlatarak bitsin fark etmez).
* `@AfterReturning`: Hedef metot *sadece başarılı* bir sonuç dönerse.
* `@AfterThrowing`: Hedef metot bir *exception (hata)* fırlatırsa.
* `@Around`: Hedef metodu tamamen sarar. Metodun hem öncesine hem sonrasına müdahale edebilir ve metodun çalışmasını `ProceedingJoinPoint.proceed()` ile manuel tetikler.



---

## Spring Boot – @Cacheable ve Invalidation

* **@Cacheable:** Okuma işlemleri için kullanılır. Metot çağrıldığında önce önbelleğe (cache) bakar. Veri varsa metodu hiç çalıştırmadan değeri döner. Veri yoksa metodu çalıştırır ve sonucu önbelleğe yazar. (Örn: Döviz kurları).
* **@CacheEvict:** Cache Invalidation (önbellek temizleme) işlemi için kullanılır. Belirli bir olay gerçekleştiğinde (veri silindiğinde veya eski geçerliliğini yitirdiğinde) önbellekteki veriyi siler. Genellikle `@Scheduled` ile belirli periyotlarda veya bir veri güncellendiğinde tetiklenir.
* **@CachePut:** Metodu *her zaman* çalıştırır ve dönen yeni sonuçla önbellekteki veriyi günceller. Veri tutarlılığını sağlamak için UPDATE işlemlerinde kullanılır.
* **@CacheConfig:** Sınıf seviyesinde kullanılarak, o sınıftaki tüm metotların aynı önbellek ismini (cache name) kullanmasını sağlar.
* **@Caching:** Bir metot üzerinde birden fazla cache kuralını (Evict, Put vb.) aynı anda tanımlamak için kullanılır.

**Senaryo (Döviz Kuru):**
Kur getiren metot `@Cacheable(value = "doviz", key = "#kurTipi")` ile işaretlenir. `@Scheduled` kullanılarak arka planda çalışan ayrı bir metot ile her gün saat 00:00'da `@CacheEvict(value = "doviz", allEntries = true)` tetiklenerek eski kur bilgileri temizlenir (Cache Invalidation).

---

## Design Pattern – Builder Pattern

**Builder Pattern:** Kurulum süreci karmaşık olan ve çok fazla parametre alan nesnelerin adım adım, esnek ve okunabilir bir şekilde oluşturulmasını sağlayan yaratımsal (creational) bir tasarım desenidir.

**Neden Constructor Overloading Yerine Tercih Edilir?**

1. **Telescoping Constructor Sorunu:** Çok sayıda opsiyonel alan olduğunda, farklı kombinasyonlar için onlarca farklı yapıcı metot (constructor) yazmak zorunda kalınır. Bu kod karmaşasına yol açar.
2. **İsimlendirme ve Okunabilirlik:** Yapıcı metoda gönderilen yan yana 5 tane `String` veya `boolean` değerinin ne anlama geldiği (hangisi ad, hangisi soyad) çağrıldığı yerde anlaşılamaz. Builder zincirleme çağrılarla (`.setName().setAge()`) okunabilirliği artırır.
3. **Immutability (Değişmezlik) ve Thread Safety:** Eğer constructor yerine boş nesne oluşturup Setter'lar kullanılırsa, nesne oluşturulma anında eksik kalabilir ve başka bir thread (iş parçacığı) bu yarım nesneye erişebilir. Builder pattern, `.build()` metodu çağrılana kadar nesneyi dışarı açmaz. Oluşan nihai nesne dışarıya tam ve (istenirse) `immutable` olarak teslim edilir.

---

## Mikroservis – Event Sourcing ve CQRS

* **CQRS (Command Query Responsibility Segregation):** Bir sistemdeki "yazma" (Command - Insert/Update/Delete) ve "okuma" (Query - Select) işlemlerinin modellerini ve genellikle veritabanlarını birbirinden ayırma prensibidir. Yazma işlemleri ilişkisel bir veritabanına yapılırken, veriler asenkron olarak okuma için optimize edilmiş NoSQL (Elasticsearch, Mongo) tabanlı veritabanlarına aktarılarak okuma performansı maksimize edilir.
* **Event Sourcing:** Bir nesnenin mevcut durumunu tek bir satır olarak veritabanında tutmak yerine, o nesne üzerinde gerçekleşen *tüm durum değişikliklerini (olayları/eventleri)* sırasıyla ve değiştirilemez (immutable) bir şekilde kaydetme yaklaşımıdır.

**Hesap Bakiyesi Senaryosunda Avantajı:**
Mevcut bakiyeyi sadece "1000 TL" olarak tutmak, bu paranın nasıl oluştuğu bilgisini yok eder. Event Sourcing kullanıldığında; "Hesap Açıldı (0)", "Para Yatırıldı (+500)", "Para Yatırıldı (+700)", "Havale Yapıldı (-200)" şeklindeki tüm loglar (eventler) sırasıyla saklanır. Bu sayede:

1. Mükemmel bir denetim izi (audit log) oluşur.
2. Sistem herhangi bir geçmiş zamandaki duruma (Point-in-time recovery) geri döndürülebilir.
3. Uygulama çökerse, geçmiş eventler baştan çalıştırılarak (replay) güncel bakiye tekrar hesaplanabilir.

---

## Mikroservis – Retry Stratejisi ve Dead Letter Queue (DLQ)

Hatalı veya geçici kesinti yaşayan mesajları sabit aralıklarla veya sonsuza kadar tekrar denemek (fixed retry) ağı tıkar ve hedeflenen servisi (örneğin SMS API'si) daha da çıkmaza sokar.

* **Retry Stratejisi:** Gecikmelerin kademeli olarak artırıldığı **Exponential Backoff** (Örn: 2sn, 4sn, 8sn, 16sn bekleme) kullanılmalıdır. Buna ek olarak **Jitter** (rastgelelik) eklenerek, aynı anda hata almış birden fazla mesajın servise milisaniyesi milisaniyesine aynı anda tekrar yüklenmesi (thundering herd problemi) engellenir.
* **Hata Tipine Göre Davranış:** Timeout, 500 Internal Server Error gibi durumlarda (geçici network hataları) retry mantıklıdır. Ancak 400 Bad Request, geçersiz telefon numarası gibi *kalıcı hatalarda* mesajı tekrar denemek anlamsızdır, doğrudan iptal edilmeli veya DLQ'ya atılmalıdır.
* **Dead Letter Queue (DLQ):** Maksimum deneme sayısına (örn. 5 kez) ulaşmış veya formattan dolayı hiç işlenemeyecek mesajların ana kuyruğu tıkamaması için ayrıldığı "ölü mesajlar kuyruğu"dur. Mesaj DLQ'ya alınırken orijinal hata kodu, kaç kez denendiği ve stacktrace bilgisi header'lara eklenir.

**DLQ İşleme Yöntemleri:**

1. **İzleme ve Alarmlar:** Prometheus/Grafana gibi araçlarla DLQ kuyruğundaki mesaj sayısı izlenir. Belli bir eşiği geçerse nöbetçi mühendise (PagerDuty/Slack) alarm gider.
2. **Manuel İnceleme:** Geçersiz format vb. durumlar admin panel üzerinden ekipler tarafından manuel incelenir.
3. **Otomatik Replay:** Geçici bir kod bug'ı veya 3. parti API çökmesi varsa, sistem düzeltildikten sonra DLQ'daki mesajlar otomatik veya tetikleme ile tekrar ana kuyruğa alınarak (replay) işlenmesi sağlanır.

---

## Algoritma – Sıralı İki Diziyi Birleştirme (Two Pointers)

Koleksiyonların kendi sıralama metotlarını kullanmadan `O(n + m)` zaman karmaşıklığı ile iki sıralı diziyi birleştiren algoritma (Two-pointer tekniği). Java sözdizimsel hataları giderilmiş ve mantıksal operatörler düzeltilmiştir.

```java
public int[] mergeSortedTransactions(int[] branch1, int[] branch2) {
    int sol = 0;
    int sag = 0;
    int index = 0;
    
    // Toplam uzunlukta yeni bir dizi oluşturuyoruz
    int[] merge = new int[branch1.length + branch2.length];
    
    // İki diziden de eleman olduğu sürece karşılaştırma yap
    while (sol < branch1.length && sag < branch2.length) {
        if (branch1[sol] <= branch2[sag]) {
            merge[index] = branch1[sol];
            sol++;
        } else {
            merge[index] = branch2[sag];
            sag++;
        }
        index++;
    }
    
    // İlk dizide arta kalan eleman varsa ekle
    while (sol < branch1.length) {
        merge[index] = branch1[sol];
        sol++;
        index++;
    }
    
    // İkinci dizide arta kalan eleman varsa ekle
    while (sag < branch2.length) {
        merge[index] = branch2[sag];
        sag++;
        index++;
    }
    
    return merge;
}

```

**Performans Analizi:**

* **Zaman Karmaşıklığı:** `O(N + M)`. Sadece tek bir döngü ile tüm elemanların üzerinden bir kez geçilir.
* **Alan Karmaşıklığı:** `O(N + M)`. Yeni bir dizi oluşturduğumuz için.
* Neden `Collections.sort()` daha yavaştır? Dizileri tek bir yerde birleştirip ardından standart bir sıralama algoritması (örn. MergeSort veya TimSort) çağırmak `O(K log K)` zaman alır (K = N + M). Eğer toplam 1000 eleman varsa, Two-Pointer yaklaşımı ortalama 1000 işlem yaparken, sort fonksiyonu yaklaşık 1000 * 10 (log2(1000) ≈ 10) = 10,000 işlem yapar.

---

## SQL – İşlem Yapmamış Müşterileri Bulma

Üç farklı yaklaşım ile hiç işlem (transaction) yapmamış hesapların bulunması:

**1. LEFT JOIN ... WHERE IS NULL**

```sql
SELECT
    a.account_id,
    a.balance,
    c.name
FROM ACCOUNT a
INNER JOIN CUSTOMER c ON a.customer_id = c.customer_id
LEFT JOIN TRANSACTION t ON a.account_id = t.account_id
WHERE t.transaction_id IS NULL;

```

**2. NOT EXISTS (Subquery)**

```sql
SELECT
    a.account_id,
    a.balance,
    c.name
FROM ACCOUNT a
INNER JOIN CUSTOMER c ON a.customer_id = c.customer_id
WHERE NOT EXISTS (
    SELECT 1 
    FROM TRANSACTION t 
    WHERE t.account_id = a.account_id
);

```

**3. NOT IN (Subquery)**

```sql
SELECT
    a.account_id,
    a.balance,
    c.name
FROM ACCOUNT a
INNER JOIN CUSTOMER c ON a.customer_id = c.customer_id
WHERE a.account_id NOT IN (
    SELECT account_id 
    FROM TRANSACTION 
    WHERE account_id IS NOT NULL
);

```

**Performans ve NULL Değer Riski Farkı:**
`NOT EXISTS` eşleşen ilk kaydı bulduğunda aramayı durdurduğu (short-circuit) için veri tabanı motorları tarafından yüksek performansla işlenir. `LEFT JOIN` önce iki tablonun eşleşen-eşleşmeyen tüm varyasyonlarını bellekte birleştirip ardından filtrelediği için maliyetlidir. `NOT IN` kullanımı ise risklidir; alt sorgudan (Subquery) dönen sonuçlar arasında tek bir `NULL` değer bile varsa, SQL'in üç değerli (True, False, Unknown) mantığı gereği tüm karşılaştırma `UNKNOWN` döner ve sorgu hiçbir veri getirmez.