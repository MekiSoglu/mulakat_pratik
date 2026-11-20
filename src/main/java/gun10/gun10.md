# Gün 10 – Java: HashMap İç Çalışma Mekanizması

---

### 1. Java – HashMap İçeride Nasıl Çalışır?

**Soru:** Java'da `HashMap` içeride nasıl çalışır? Hash hesabı, bucket, collision, load factor kavramlarını açıklayınız. Java 8'de bir bucket'ta çok fazla eleman birikirse ne değişti?

---

#### 1. Genel Mimari ve Temel Veri Yapısı

Java'da `HashMap`, anahtar-değer (`Key-Value`) çiftlerini saklayan, **hashing** temelli ve sıralama garantisi vermeyen (`unordered`) bir veri yapısıdır.

Arka planda temelde bir **Dizi (Array)** ve bu dizinin elemanlarında oluşan **Bağlı Liste (Singly LinkedList)** veya **Kırmızı-Siyah Ağaç (Red-Black Tree)** hibrit yapısını kullanır.

```
table (Node<K,V>[]):
[0]  -> null
[1]  -> Node(K1, V1) -> Node(K2, V2) -> null  (Collision -> LinkedList)
[2]  -> null
...
[9]  -> TreeNode (Red-Black Tree Kökü)        (Java 8+ Treeification)
...
[15] -> Node(K3, V3) -> null
```

Her bir girdi (`Entry`), dahili statik bir sınıf olan `Node<K,V>` nesnesidir ve şu 4 alanı tutar:
1. `final int hash`: Anahtarın hesaplanmış hash değeri.
2. `final K key`: Eklenen anahtar.
3. `V value`: Değer.
4. `Node<K,V> next`: Çarpışma durumunda aynı kovadaki bir sonraki düğümün referansı.

---

#### 2. Hash Hesabı ve Dizi İndeksinin Bulunması

Bir eleman `put(key, value)` veya `get(key)` edildiğinde 3 aşamalı bir işlem gerçekleşir:

##### Adım 1: `key.hashCode()` Çağrısı
Java, anahtar nesnesinin `hashCode()` metodunu çağırarak 32-bit işaretli bir tamsayı (`int`) elde eder.

##### Adım 2: Perturbation Function (Bit Karıştırma / Yayma)
HashMap kaynak kodunda yer alan dahili `hash(Object key)` fonksiyonu uygulanır:

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

* **Neden `(h ^ (h >>> 16))` yapılır?**
  * HashMap'in başlangıç kapasitesi genelde küçüktür (örn. 16). İndeks hesaplanırken dizinin boyutuna göre yalnızca en düşük 4-5 bit kullanılır.
  * Eğer bir `hashCode()` implementasyonu sadece yüksek bitlerde (üst 16 bit) değişiyor, düşük bitlerde hep aynı kalıyorsa, bu durum korkunç derecede **collision (çarpışma)** yaratır.
  * `h >>> 16` işlemi üst 16 biti alt tarafa kaydırır ve `^` (XOR) ile alt 16 bit ile harmanlar. Böylece yüksek bitlerdeki değişim alt bitlere de yansıtılır (**Avalanche Effect**).
* **`null` Key Yönetimi:** `key == null` ise hash değeri daima `0` döner. Dolayısıyla `HashMap`'te `null` key daima `table[0]` bucket'ında tutulur.

##### Adım 3: Dizi İndeksinin Belirlenmesi (Bitwise AND)
Hesaplanan hash değerinin dizinin hangi indeksine denk geleceği şu formülle bulunur:

$$\text{index} = (n - 1) \ \& \ \text{hash}$$

*(burada $n$, dizinin o anki kapasitesidir, örn. 16)*

* **Neden Modulo (`%`) Değil de `&` (Bitwise AND)?:**
  * Matematiksel olarak $n$ sayısı 2'nin bir kuvveti olduğunda ($2^k$), $\text{hash} \pmod n$ işlemi ile $\text{hash} \ \& \ (n - 1)$ işlemi **tamamen aynı sonucu** üretir.
  * *Örnek ($n = 16$):* $n - 1 = 15$ ikilik tabanda `0000 1111`'dir. Herhangi bir sayıyı `0000 1111` ile `&` işlemine sokmak, sayının son 4 bitini maskeler (0 ile 15 arasında bir indeks verir).
  * İşlemci seviyesinde bit düzeyinde AND (`&`) işlemi, bölme ve mod (`%`) işlemine kıyasla kat kat daha hızlıdır. Bu yüzden HashMap kapasitesi daima **2'nin kuvveti** (`power of two`) olmak zorundadır.

---

#### 3. Bucket (Kova) Kavramı

* `table` dizisinin (`Node<K,V>[]`) her bir indeksine **bucket (kova)** denir.
* Bir bucket;
  * Hiç eleman içermeyebilir (`null`),
  * Tek bir `Node<K,V>` içerebilir,
  * Çarpışma olmuşsa birden çok düğümü birbirine bağlı bir **bağlı liste (LinkedList)** olarak tutabilir,
  * Java 8+ ile birlikte eleman sayısı 8'i aşmışsa dengeli bir **Kırmızı-Siyah Ağaç (Red-Black Tree)** olarak tutabilir.

---

#### 4. Collision (Çarpışma) ve Çözümü: Separate Chaining

##### Collision Nedir?
İki farklı anahtarın (`k1.equals(k2) == false`):
1. `hashCode()` değerlerinin şans eseri aynı üretilmesi, ya da
2. Hash kodları farklı olsa bile `(n - 1) & hash` formülü sonucunda **aynı bucket indeksine** denk gelmesi durumudur.

##### Çözüm Yöntemi: Separate Chaining (Ayrı Zincirleme)
Java `HashMap`, çarpışmaları çözmek için **Separate Chaining** tekniğini kullanır. Aynı indekse düşen elemanlar aynı bucket içerisinde bir bağlı liste olarak birbirine bağlanır.

##### Java 7 vs Java 8 Ekleme Farkı (Head vs Tail Insertion):
* **Java 7 (Head Insertion):** Yeni gelen eleman listenin **başına** eklenirdi ($O(1)$ ekleme). Ancak bu durum multithread ortamlarda aynı anda resize yapılırken bağlantıların ters çevrilmesine ve **sonsuz döngü (circular reference / infinite loop)** oluşmasına neden olabiliyordu.
* **Java 8 (Tail Insertion):** Yeni gelen eleman listenin **sonuna** eklenir. Liste taranırken zaten aynı anahtarın var olup olmadığına (`equals`) bakılır ve eleman sayısı sayılır; yoksa en sona eklenir. Bu, sonsuz döngü hatasını engellemiştir (yine de `HashMap` thread-safe değildir!).

##### `equals()` ve `hashCode()` Sözleşmesi (Contract)
`HashMap`'in doğru çalışabilmesi için `equals` ve `hashCode` sözleşmesine kesinlikle uyulmalıdır:
1. `a.equals(b) == true` ise, `a.hashCode() == b.hashCode()` **kesinlikle eşit olmalıdır**.
2. `a.hashCode() == b.hashCode()` ise, `a.equals(b)` **true olmak zorunda değildir** (Collision durumu).
3. `get(key)` yapılırken:
   * Önce `hash` karşılaştırılır (`e.hash == hash` -> tamsayı karşılaştırması çok hızlıdır).
   * Hash eşitse referans eşitliği (`e.key == key`) veya mantıksal eşitlik (`key.equals(e.key)`) kontrol edilir.

---

#### 5. Load Factor (Yük Katsayısı) ve Resizing (Yeniden Boyutlandırma)

##### Temel Parametreler:
* **`DEFAULT_INITIAL_CAPACITY`:** $16$ ($1 \ll 4$)
* **`DEFAULT_LOAD_FACTOR`:** $0.75f$
* **Threshold (Eşik Değeri):** $\text{Threshold} = \text{Capacity} \times \text{Load Factor}$
  * Başlangıçta: $16 \times 0.75 = 12$. Yani HashMap'teki toplam eleman sayısı 12'yi aştığında resize tetiklenir.

##### Neden Varsayılan Load Factor 0.75?
* **Zaman (Time) ve Alan (Space) Takası (Trade-off):**
  * Load Factor **$1.0$** olsaydı: Tablo dolana kadar resize yapılmazdı, bellekten tasarruf edilirdi; ancak çarpışmalar (collisions) ciddi oranda artar, `get` ve `put` süreleri uzardı.
  * Load Factor **$0.5$** olsaydı: Çarpışma neredeyse hiç olmazdı, erişim çok hızlı olurdu; ancak dizinin yarısı daima boş kalır, aşırı bellek israfı oluşur ve çok sık resize maliyeti yaşanırdı.
  * İstatistiki Poisson dağılımına göre $0.75$, minimum çarpışma ile optimum bellek verimliliği arasındaki en ideal denge noktasıdır.

##### Resizing ve Rehashing Mekanizması:
Kapasite dolduğunda (`size > threshold`):
1. Dizi boyutu **2 katına** çıkarılır (`newCapacity = oldCapacity << 1`, örn. $16 \to 32 \to 64$).
2. **Java 8 Bit Testi Optimizasyonu:** Java 7'de tüm elemanların indeksi sıfırdan hesaplanırdı (`hash % newCapacity`). Java 8'de ise kapasite 2 katına çıktığında maskeye yalnızca 1 yeni bit eklenir.
   * `(e.hash & oldCap) == 0` ise eleman **eski indeksinde** kalır.
   * `(e.hash & oldCap) != 0` ise eleman **$\text{eski indeks} + \text{oldCap}$** indeksine taşınır.
   * Bu sayede bağlı liste iki parçaya bölünerek zahmetsizce ve yüksek performansla yeni tabloya aktarılır.

---

#### 6. Java 8'de Ne Değişti? (Treeification / Red-Black Tree)

Java 8 öncesinde (Java 7 ve öncesi) bir bucket'ta çarpışma olduğunda tüm elemanlar bir **Singly LinkedList** olarak tutuluyordu.

##### Problem (Java 7 ve Öncesi):
* Kötü tasarlanmış bir `hashCode()` metodu veya bilinçli yapılan **HashDoS** (Hash Denial of Service) saldırılarında, saldırgan bilerek aynı hash değerini üreten binlerce anahtar göndererek tüm verinin tek bir bucket'ta toplanmasına neden olabiliyordu.
* Bu durumda bağlı listede arama süresi $O(1)$ yerine **$O(n)$**'e düşüyor, CPU tüketimi %100'e fırlıyor ve sistem kilitleniyordu.

##### Java 8 Çözümü: Red-Black Tree (Treeification)
Java 8 ile birlikte bir bucket'taki bağlı liste eleman sayısı belirli bir eşiği aştığında yapı otomatik olarak dengeli bir ikili arama ağacı olan **Red-Black Tree**'ye (`TreeNode`) dönüştürülür.

```
[Bucket X]
Eski Hali (Java 7):   Node1 -> Node2 -> Node3 -> ... -> Node8 (LinkedList: O(n))

Yeni Hali (Java 8+):            TreeNode (Kök)
                               /              \
                        TreeNode              TreeNode   (Red-Black Tree: O(log n))
                        /      \              /      \
                   TreeNode  TreeNode    TreeNode  TreeNode
```

##### Kritik Sabitler ve Kurallar:
1. **`TREEIFY_THRESHOLD = 8`:**
   * Bir bucket'taki eleman sayısı 8'e ulaştığında ve yeni bir eleman eklendiğinde ağaçlaştırma tetiklenir.
2. **`MIN_TREEIFY_CAPACITY = 64` (Çok Önemli Mülakat Detayı):**
   * Bir bucket'ta 8 eleman birikse bile, **tablonun toplam kapasitesi 64'ten küçükse ağaca dönüştürülmez!**
   * Bunun yerine önce tablo kapasitesi `resize()` edilerek 2 katına çıkarılır. Amaç, elemanları farklı kovalara dağıtmaktır. Tablo boyutu en az 64 olduğunda ağaca dönüşüm gerçekleşir.
3. **`UNTREEIFY_THRESHOLD = 6`:**
   * Silme (`remove`) veya resize sonrasında bir ağaçtaki eleman sayısı 6 veya altına düşerse, ağaç yapısı tekrar standart **LinkedList (`Node`)** yapısına geri döndürülür.
   * *Neden 8 değil de 6? (Hysteresis)*: Eğer eşik 8 olsaydı, ardışık 8. eleman ekleme ve silme işlemlerinde sistem sürekli liste $\leftrightarrow$ ağaç dönüşümü yaparak ciddi performans kaybederdi. 8 ile 6 arasındaki bu fark gereksiz dönüşüm dalgalanmasını engeller.

---

#### 7. Karmaşıklık Özeti (Time Complexity)

| İşlem | Ortalama Durum | En Kötü Durum (Java 7) | En Kötü Durum (Java 8+) |
| :--- | :--- | :--- | :--- |
| **`get(key)`** | $\mathcal{O}(1)$ | $\mathcal{O}(n)$ *(LinkedList)* | $\mathcal{O}(\log n)$ *(Red-Black Tree)* |
| **`put(key, value)`** | $\mathcal{O}(1)$ | $\mathcal{O}(n)$ | $\mathcal{O}(\log n)$ |
| **`remove(key)`** | $\mathcal{O}(1)$ | $\mathcal{O}(n)$ | $\mathcal{O}(\log n)$ |

---

#### 8. Mülakat Özeti (Cheat Sheet)

* **Nasıl Çalışır?:** Dizi (`table`) tabanlıdır. `key.hashCode()` alınır, üst bitler sağa kaydırılıp XOR'lanır, `(n - 1) & hash` ile bucket indeksi bulunur.
* **Bucket:** Dizi indeksidir. Boş, tek düğüm, bağlı liste veya kırmızı-siyah ağaç tutabilir.
* **Collision:** Farklı key'lerin aynı indekse düşmesidir. Java'da **Separate Chaining** ile çözülür.
* **Load Factor:** Varsayılan 0.75'tir. Kapasite $\times 0.75$ aşıldığında tablo 2 katına büyütülür (rehashing).
* **Java 8 Yeniliği:** Bir kovada eleman sayısı $\ge 8$ olursa ve toplam kapasite $\ge 64$ ise bağlı liste **Red-Black Tree**'ye dönüştürülür; en kötü durum arama maliyeti $\mathcal{O}(n)$'den $\mathcal{O}(\log n)$'e indirilir. Eleman sayısı $\le 6$'ya düşerse tekrar bağlı listeye döner.
