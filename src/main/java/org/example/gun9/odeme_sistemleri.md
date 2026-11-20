Bir kredi kartı ile yapılan ödemede Authorization (yetkilendirme), Capture (tahsilat), Settlement (takas/mutabakat) ve Clearing adımları nedir, sırasıyla ne anlama gelir?



Authorization : kartın geçerli olup olmadığı ve yeterli limitin olup olmadığı kontorol edilir , burada bir transfer olmaz sadece belirtilen miktar kadar bloke konur 

Capture:
authoraztionda bloke konulan tutarın kesin olarak tahsil edilme isteğidir , bu istekten sonra gün sonu havuzuna geçer

Clearing :
burada tüm başarılı işlemler kart şebekelerine gönderilir , fiziksel bir transfer yaşanmaz ,burada loglama yapılır borçlar belirlenir .

settlement:
clearing sonrasında takas merkezinde hesaplanan net tutarlar alacaklı ve borçlu bankalar arasında transfer edilir . 





2. 
Issuer bank (kartı çıkaran banka) ile Acquirer bank (işyerinin bankası) arasındaki fark nedir? Bir POS işleminde bu iki taraf nasıl devreye girer?

Issuer Bank : müşterinin kart sahibinin bankasıdır .

Acquirer : Post makşnesin sahibinin kullandığı bankadır .

1. post makines kartı okur bilgiler acquirer bank a gider eğer issuer kendisine ait ise işlemleri kendisi yapar 

2. değilse bilgiler kart şebekesine gönderilir 

3. şebeke bilgileri ıssuara ilertir burada yetikelendirme kontoroller yapılır ve bir cevap döner 

4. cevap aynı yolu izleyerek acquirer bankaya gelir slip olarak basılır .







3D Secure nedir, nasıl çalışır? Neden bazı işlemlerde 3D Secure adımı atlanabilir (frictionless flow)?


3D security : 3 yapının iletişiminde dayanır 
Acquirer domain , Issuer Domain , Interoperability Domain arasındaki iletişimi kurar 

1. kart bilgisi ve cihazdan toplanan veriler Issuer bank a gider 
2. burdaki yapay zeka bilgilere göre direkt onay yada sms doğrulması ister 
3. bu adımlarda sorun olmassa autorizatin işlemi ile para blokesi başlar ve para treansferi adımları takip eder 

eğer kulanıcı cihazında gelen veriler düşük risk içeriyorsa aynı cihaz aynı bölge aynı site vb o zaman sms doğrulaması atlanır .




PCI-DSS nedir, bir bankanın/işyerinin kart numarasını (PAN) doğrudan veritabanında saklaması neden risklidir? Tokenization bu riski nasıl azaltır?



PCI-DSS: Kart verisini işleyen/tutan herkesin uyması gereken küresel güvenlik standardı.
PAN Saklama Riski: Veri sızıntısında kartların çalınması, ağır para cezaları, sanal POS'un iptali ve yüksek denetim maliyeti.
Tokenization Çözümü: Gerçek kart numarasını kasada tutup, sisteme rastgele anlamsız bir anahtar (Token) vermek. Çalınsa dahi dışarıda bir değeri yoktur; şirketi PCI denetim kapsamından çıkarır.


Loglara Asla Hassas Veri Yazmama Kuralı:

"Bir hata aldığında (catch bloğunda) gelen kart nesnesini logger.info(request) diye basabilir misin?"
Cevap: Kesinlikle Hayır! Kart numarası, CVV veya son kullanma tarihi log dosyalarına yazılırsa bu en ağır PCI-DSS ihlallerinden biridir. Loglarda kart sadece maskeli (örneğin: 4543********9010) yazılabilir.
CVV Asla Saklanamaz Kuralı:

Veritabanında kart saklama (Card on File) özelliği yapsan bile CVV veritabanına hiçbir koşulda kaydedilemez.
Transit ve Rest Şifreleme:

Veri veritabanında dururken (Data at Rest) şifreli olmalı.
Veri ağda gezerken (Data in Transit) TLS 1.2+ ile korunmalıdır.










ISO 8583 nedir? Kart işlemlerinde (POS, ATM) hangi amaçla kullanılır?