## Java'da equals() ve hashCode() Override Etmenin Önemi

Java'da `Object` sınıfından miras alınan varsayılan `equals()` metodu, nesnelerin içerdiği değerleri değil bellek adreslerini (referanslarını) karşılaştırır. Varsayılan `hashCode()` ise bellek adresine dayalı benzersiz bir tam sayı üretir. Bu metotları override etmemek, içerik ve mantık olarak tamamen aynı olan iki nesnenin (örneğin ID'si 1 olan iki farklı `User` nesnesi) JVM tarafından birbirinden bağımsız, farklı nesneler olarak algılanmasına neden olur.

### HashMap ve HashSet İçinde Yaşanacak Sorunlar

* **Veriye Ulaşamama (Kayıp):** `HashMap` veriyi kaydederken ve okurken anahtarın (key) hash koduna göre ilgili kovayı (bucket) bulur. Bir nesneyi anahtar olarak kaydedip, daha sonra aynı değerlere sahip yeni bir nesne ile o değeri `map.get(yeniNesne)` okumaya kalktığınızda `null` döner. Çünkü override edilmemiş `hashCode()`, yeni nesne için farklı bir sayı üretir ve `HashMap` yanlış kovada arama yapar.
* **Veri Tekrarı (Duplication):** `HashSet` yalnızca benzersiz (unique) elemanlar tutmak üzere tasarlanmıştır. Ancak `hashCode()` ve `equals()` override edilmezse, özellikleri tamamen aynı olan 10 farklı nesne de koleksiyona eklenir çünkü bellek referansları farklıdır. Bu durum `Set` yapısının temel işlevini bozar.
* **Hafıza Sızıntısı (Memory Leak):** Hash tabanlı koleksiyonlara eklenen anahtarlar tekrar bulunamadığı için üzerlerine yazılamaz veya silinemezler. Sürekli aynı veriyi taşıyan yeni nesneler eklenirse koleksiyon şişer ve `OutOfMemoryError` hatasına yol açar.

**Kural:** `equals()` metodunu override eden bir sınıf, `hashCode()` metodunu da kesinlikle override etmek zorundadır. `equals()`'a göre eşit olan iki nesnenin hash kodları da mutlaka aynı olmalıdır.

---

## Java'da final, finally ve finalize

* **final:** Kısıtlama ve değişmezlik sağlar.
* *Sınıfta:* Kalıtımı (miras alınmasını) engeller.
* *Metotta:* Alt sınıflarda override edilmesini engeller.
* *Değişkende:* İlk atamadan sonra değerinin değiştirilmesini (reassignment) engeller.


* **finally:** `try-catch` mekanizmasına bağlıdır. Exception (hata) fırlatılsın ya da fırlatılmasın her koşulda çalışması garanti olan kod bloğudur. Yalnızca `System.exit()` çağrılırsa veya JVM çökerse çalışmaz.
* **finalize:** `Object` sınıfında tanımlıdır. Bir nesne Garbage Collector tarafından bellekten temizlenmeden hemen önce son temizlik işlemleri için tetiklenir. Java 9 ile *Deprecated* (kullanımdan kaldırılmış) ilan edilmiştir ve kullanımı terk edilmiştir.

---

## SQL Join Türleri ve Müşteri Senaryosu

* **INNER JOIN:** Sadece iki tabloda da eşleşen (kesişen) satırları getirir. Eşleşmeyenleri ve null olanları kabul etmez.
* **LEFT JOIN:** Sol tablonun tamamını ve sağ tablonun eşleşen satırlarını getirir. Sağ tabloda eşleşmeyen durumlar için `NULL` değer atar.
* **RIGHT JOIN:** Sağ tablonun tamamını ve sol tablonun eşleşen satırlarını getirir. Eşleşmeyenler için `NULL` değer atar.
* **FULL OUTER JOIN:** Her iki tablodaki tüm satırları getirir. Eşleşmeyen kısımlara iki taraf için de `NULL` yazar.

**Senaryo Çözümü:** Bir müşterinin hiç hesabı olmasa bile listede görünmesini istiyorsak **LEFT JOIN** kullanılır.
Örneğin: `FROM Musteri m LEFT JOIN Hesap h ON m.id = h.musteri_id`. Bu sayede hesabı olmayan müşteriler `NULL` olarak listelenir ve `WHERE h.id IS NULL` şartı ile hesabı olmayan müşteriler kolayca filtrelenebilir.

---

## SQL'de Deadlock ve Bankacılık Sisteminde Önlenmesi

Veritabanında `REPEATABLE READ` veya `SERIALIZABLE` gibi transaction izolasyon seviyelerinde, aynı anda çalışan iki farklı thread (örneğin A kullanıcısı B'ye, B kullanıcısı da aynı anda A'ya para gönderirken) birbirlerinin kilitlediği (lock) kaynaklara ihtiyaç duyduğunda kilitlenme (deadlock) oluşur. İşlemler sonsuza kadar birbirini bekler.

**Alınabilecek Önlemler:**

1. **Asenkron İşleme:** Deadlock oluşmasını önlemek için süreç Kafka veya Redis gibi araçlarla asenkron (event-driven) bir yapıya taşınabilir.
2. **Sıralı Kilitleme Algoritması:** Kaynaklara (hesaplara) her zaman belirli bir hiyerarşik sıraya göre erişilmesi sağlanabilir (Örn: Her zaman önce ID'si küçük olan hesabı kilitle).
3. **İzolasyon Seviyesinin Düşürülmesi:** Duruma göre izolasyon seviyesi `READ COMMITTED` seviyesine çekilerek kaynakların daha çabuk serbest kalması sağlanabilir.

---

## Hibernate – İlişkiler ve Metotlar

### mappedBy ve CascadeType

* **mappedBy:** Çift yönlü (`@OneToMany`, `@ManyToMany` vb.) ilişkilerde, ilişkinin sahibinin (owner) karşı taraf olduğunu belirtmek için kullanılır. "Bu ilişkinin yöneticisi ben değilim, yabancı anahtar (foreign key) diğer tabloda tutuluyor" demektir.
* **CascadeType Seçenekleri:**
* `PERSIST`: Üst nesne kaydedildiğinde (save), ona bağlı alt nesneler de veritabanına kaydedilir.
* `MERGE`: Üst nesne güncellendiğinde alt nesneler de otomatik olarak güncellenir.
* `REMOVE`: Bir nesne silindiğinde, ona bağlı bütün alt nesneler de silinir.
* `REFRESH`: Ana nesne veritabanından tekrar belleğe çekildiğinde, alt nesneler de tekrar okunarak senkronize edilir.
* `DETACH`: Bir nesne Hibernate session'ından çıkarıldığında, alt nesnelerin tamamı da session'dan çıkarılır.
* `ALL`: Yukarıdaki işlemlerin tümünü kapsar.



### save(), persist(), merge() ve update() Arasındaki Farklar

* **persist():** JPA standartlarına aittir. Nesneyi persistence context'e (kalıcılık bağlamı) ekler ancak veritabanına hemen `INSERT` atmaz, işlem sonucunda nesnenin ID'sini anında döndürmesi garanti edilmez (void döner).
* **save():** Hibernate'e özgüdür. Nesneyi kaydeder ve doğrudan oluşturulan kimliği (ID) geri döndürür.
* **merge():** Session'dan kopmuş (detached) bir nesnenin mevcut durumunu, veritabanındaki kayıt ile birleştirerek (update) günceller ve nesnenin yönetilen (managed) bir kopyasını geri döndürür.
* **update():** Hibernate'e özgüdür. Detached durumdaki nesneyi doğrudan veritabanında günceller. Eğer aynı ID'ye sahip başka bir nesne zaten session içindeyse `NonUniqueObjectException` hatası fırlatır.

---

## Spring Boot – Dependency Injection (Bağımlılık Enjeksiyonu)

* **Field Injection (`@Autowired` doğrudan değişkende):** Önerilmeyen yöntemdir. Sınıf değişkeni `final` olarak tanımlanamaz ve nesnenin referansının değişme riski vardır. Döngüsel bağımlılıkları (circular dependency) derleme anında yakalamak zordur. Birim (unit) testlerde mock nesneleri inject etmek için reflection gerektirir ve Spring IoC konteynerine yüksek bağımlılık oluşturur.
* **Constructor Injection (Yapıcı Metot ile):** En çok tercih edilen ve önerilen yöntemdir. Sınıf oluşturulurken IoC container constructor'ı okur ve gerekli tüm bağımlılıkları sağlar. Bu sayede sınıf ayağa kalktığında bağımlılıkların eksiksiz olduğunu garanti ederiz. Değişkenler `final` olarak işaretlenebilir ve döngüsel bağımlılık durumları uygulama derlenirken veya başlarken hemen hata fırlatarak yakalanır.
* **Setter Injection:** Sınıftaki bağımlılık, sınıf oluşturulduktan sonra setter metotları çağrılarak sağlanır. Kesin olarak var olmak zorunda olmayan, opsiyonel bağımlılıklar için veya çalışma zamanında bağımlılığın değiştirilmesi gereken senaryolarda kullanılır.

---

## Spring Boot – Security ve JWT (JSON Web Token) Akışı

* **Authentication (Kimlik Doğrulama):** "Bu kullanıcı kim?" sorusunun cevabıdır. Kullanıcının sistemde kayıtlı olup olmadığını (kullanıcı adı/şifre kontrolü) kontrol eder.
* **Authorization (Yetkilendirme):** "Bu kullanıcının yetkisi ne?" sorusunun cevabıdır. Sisteme giriş yapmış kullanıcının ilgili işlemi yapmaya izni olup olmadığını kontrol eder.

**JWT Akışı ve Yapısı:**
JWT 3 ana parçadan oluşur:

1. **Header:** Token'ın türü (JWT) ve kullanılan şifreleme/imzalama algoritması (örn. HS256) belirtilir.
2. **Payload:** Kullanıcının rolü, yetkileri ve ID'si gibi bilgileri (claims) barındırır. Burada şifre gibi hassas bilgiler asla taşınmaz.
3. **Signature:** Header ve Payload kısımlarının, sunucudaki gizli bir anahtar (secret key) ile hash'lenerek oluşturulmuş imzasıdır.

**JWT Çalışma Adımları:**

1. İstemci sunucuya kullanıcı adı ve şifresiyle giriş isteği atar.
2. Sunucu kimlik bilgilerini kontrol eder. Başarılıysa gizli anahtarla imzalanmış bir JWT üretir ve istemciye döner.
3. İstemci bu token'ı saklar (Local Storage veya HttpOnly Cookie).
4. İstemci yetki gerektiren bir istek yaparken token'ı `Authorization` başlığı (Bearer) altında sunucuya iletir.
5. Sunucu token'ın imzasını kendi gizli anahtarıyla doğrular. İmza geçerliyse, veritabanına gitmeye gerek kalmadan token içindeki payload verisinden kullanıcının kimliğini ve rollerini tespit edip işleme izin verir.

---

## Design Pattern – Strateji (Strategy) Deseni

Bir ödeme sisteminde birden fazla ödeme yöntemi varsa ve gelecekte yenileri eklenecekse **Strategy Pattern** kullanılmalıdır. Çünkü bir işlemi gerçekleştirmek için (ödeme yapma) birden fazla farklı algoritma/yol varsa ve bu yollardan hangisinin seçileceğine çalışma zamanında (runtime) karar verilmesi gerekiyorsa en uygun desen budur. Bu desen sayesinde `if-else` blokları kullanılmadan kod genişletilebilir (Open-Closed Prensibine uygundur).

---

## Mikroservis Mimarisi

### API Gateway

Bütün istemci isteklerinin (web, mobil vb.) mikroservislere doğrudan değil, tek bir noktadan geçmesini sağlayan giriş kapısıdır. Bu noktada yetkilendirme, güvenlik, rate limiting (istek sınırlandırma), yönlendirme, loglama ve protokol dönüşümü (örneğin dışarıya REST açıp içeride gRPC kullanma) işlemleri merkezi olarak yönetilir. `Resilience4j` gibi araçlarla devre kesici (Circuit Breaker) yapılarak dayanıklılık sağlanır.

### Service Discovery (Eureka vb.)

Mikroservis mimarisinde servislerin IP adresleri ve portları sabit değildir, bulut ortamlarında sürekli değişebilir veya servisler çöküp yeniden başlatılabilir. Servislerin adreslerini elle statik yazmak imkânsızdır. Service Discovery, ağdaki mevcut ve ayakta olan servislerin isimlerini ve güncel adreslerini tutan dinamik bir rehberdir. İstek yapacak servis, doğrudan IP bilmek yerine Service Discovery'den servisin güncel adres listesini alarak isteği doğru yere iletir.

---

## Mikroservis İletişim Senaryosu: Kredi Kartı Başvurusu

### 1. Senkron ve Asenkron İletişim Tercihleri

* **Kredi Skoru Servisi (Asenkron):** Bekleme süresi 5-10 saniye olabildiği veya timeout ihtimali olduğu için asenkron kurgulanmalıdır. İstemciye "Başvurunuz alındı, değerlendirme sonucu iletilecektir" mesajı dönülmeli ve işlem kuyruğa (RabbitMQ / Kafka) bırakılmalıdır.
* **Onay/Ret Kararı ve Kart Üretim (Asenkron):** Karar verme süreci bir algoritma veya Camunda gibi bir iş akış motoruyla insana bağlı (manuel onay) yürütülüyorsa kesinlikle asenkron olmalıdır. Onay sonrası kart üretim servisine mesaj tabanlı bir event fırlatılmalıdır.
* **Bildirim Gönderme (Asenkron):** E-posta veya SMS gönderimindeki bir hata uygulamanın ana akışını durdurmamalıdır. Bu işlem de asenkron yürütülmelidir.
* **Müşteri Doğrulama (Senkron):** Müşteri TC Kimlik / Telefon doğrulaması gibi anlık yanıt gerektiren süreçler işlemin en başında senkron yapılabilir.

### 2. Kredi Skoru Servisi Çökerse (Circuit Breaker)

Kredi skoru servisi yanıt vermezse tüm sistemin kilitlenmesini engellemek için **Circuit Breaker** (Devre Kesici) deseni uygulanır.

* **Kapalı (Closed):** İstekler normal şekilde akar.
* **Açık (Open):** Başarısızlık oranı belirli bir eşiği geçerse (örn: %50 hata) devre açılır. Artık gelen istekler sorunlu servise gönderilmez, anında hata veya "varsayılan (fallback)" bir değer dönülür.
* **Yarı Açık (Half-Open):** Belirli bir süre sonra devre, servisin düzelip düzelmediğini anlamak için birkaç test isteğine izin verir. Başarılı olursa devre tekrar kapanır, başarısız olursa açık kalmaya devam eder. Hata anındaki istekler veritabanına yazılır ve servis kalktığında asenkron olarak tekrar işletilir.

### 3. Saga Pattern vs Orkestrasyon

Bu akış birbirini etkileyen süreçler zinciridir. Adımların birinde başarılı olup bir diğerinde (örn: kart üretimi) başarısız olunursa, önceki adımlarda atılan veritabanı kayıtlarının geri alınması (Compensating Transaction) gerekir. Bu durumda **Saga Pattern** kullanılmalıdır. Eğer sürecin yönetimini merkezi bir servis üstleniyorsa (BPMN / Camunda) buna **Orchestration (Orkestrasyon)** tabanlı Saga denir. Bildirim aşamasındaki bir hata için tüm süreci iptal etmeye gerek yoktur, sadece mesaj yeniden kuyruğa eklenir (Retry).

---

## Algoritma: Geçerli Parantez Kontrolü (Valid Parentheses)

Bu problemi çözmek için **Stack (Yığın)** veri yapısı kullanılır. Son Giren İlk Çıkar (LIFO) prensibi, parantezlerin açılma ve kapanma mantığına tam uyar.

```java
public boolean isValidTransactionChain(String chain) {
    Stack<Character> stack = new Stack<>();
    
    for (char c : chain.toCharArray()) {
        if (c == '(') {
            stack.push(c);
        } else if (c == ')') {
            if (stack.isEmpty() || stack.pop() != '(') {
                return false;
            }
        }
    }
    
    return stack.isEmpty();
}

```

**Karmaşıklık Analizi:**

* **Zaman Karmaşıklığı:** `O(N)` - Verilen string'deki karakterler üzerinde tek bir döngü ile geçilir.
* **Alan (Space) Karmaşıklığı:** `O(N)` - En kötü durumda (örneğin `((((((`) tüm karakterler stack içerisine atılır.

---

## SQL: Hesap Bakiyeleri Raporlama

İstenen veri kümesini alabilmek için, alt sorguların sebep olduğu N+1 performans problemlerinden kaçınarak **Window Function** (Pencere Fonksiyonları) kullanmak en doğru yaklaşımdır.

### En İyi Yaklaşım (Window Function ile)

```sql
WITH RankedAccounts AS (
    SELECT 
        account_id,
        account_type,
        balance,
        ROW_NUMBER() OVER (PARTITION BY account_type ORDER BY balance DESC) as rn
    FROM ACCOUNT
)
SELECT 
    a.account_type,
    COUNT(a.account_id) AS total_accounts,
    AVG(a.balance) AS average_balance,
    MAX(CASE WHEN r.rn = 1 THEN r.account_id END) AS top_account_id
FROM ACCOUNT a
INNER JOIN RankedAccounts r ON a.account_id = r.account_id
GROUP BY a.account_type
HAVING AVG(a.balance) > 5000;

```

### Alternatif Yaklaşım (Subquery / Alt Sorgu ile)

*(Not: Bu yöntem büyük veritabanlarında `GROUP BY` içindeki her bir satır için alt sorguyu baştan çalıştıracağı için performans sorunları yaratabilir.)*

```sql
SELECT 
    a.account_type,
    COUNT(a.account_id) AS total_accounts,
    AVG(a.balance) AS average_balance,
    (
        SELECT sub.account_id 
        FROM ACCOUNT sub 
        WHERE sub.account_type = a.account_type 
        ORDER BY sub.balance DESC 
        LIMIT 1
    ) AS top_account_id
FROM ACCOUNT a
GROUP BY a.account_type
HAVING AVG(a.balance) > 5000;

```