# NCore - Modüler Minecraft Plugin Sistemi

NCore, Minecraft sunucuları için modüler bir plugin sistemidir. Modüller, JAR dosyaları olarak dinamik olarak yüklenebilir, etkinleştirilebilir ve devre dışı bırakılabilir.

## Özellikler

- ✅ **Modüler Sistem**: Modüller JAR dosyaları olarak dinamik yükleme
- ✅ **Dependency Yönetimi**: Modüller arası bağımlılık desteği
- ✅ **Hot Reload**: Sunucuyu kapatmadan modül yükleme/kaldırma
- ✅ **Otomatik Database**: Database gereken modüller için otomatik bağlantı
- ✅ **Debug Modu**: Detaylı loglama için debug mode
- ✅ **Module Komutları**: Modül yönetimi için tam komut desteği

## Kurulum

1. NCore.jar dosyasını sunucunuzun `plugins` klasörüne kopyalayın
2. Sunucuyu başlatın
3. `plugins/NCore/config.yml` dosyasını yapılandırın (özellikle database ayarları)
4. Modüllerinizi `plugins/NCore/modules/` klasörüne atın
5. Sunucuyu yeniden başlatın veya `/module reload` komutunu kullanın

## Yapılandırma

### config.yml

```yaml
# Debug mode - detaylı loglar için true yapın
debug-mode: false

# Database ayarları
database:
  host: localhost
  port: 3306
  database: ncore
  username: root
  password: ''
  pool-size: 10
  use-ssl: false
```

## Komutlar

| Komut | Açıklama |
|-------|----------|
| `/module list` | Tüm modülleri listeler |
| `/module info <modül>` | Modül hakkında detaylı bilgi gösterir |
| `/module load <jarfile>` | Yeni bir modülü JAR dosyasından yükler |
| `/module enable <modül>` | Bir modülü etkinleştirir |
| `/module disable <modül>` | Bir modülü devre dışı bırakır |
| `/module reload [modül]` | Modül(leri) yeniden yükler |

## Modül Oluşturma

### Basit Modül

```java
package com.example;

import com.notpatch.nCore.module.Module;
import com.notpatch.nCore.module.ModuleInfo;
import com.notpatch.nCore.util.NLogger;

@ModuleInfo(
    name = "ExampleModule",
    description = "Örnek bir modül",
    authors = {"YourName"}
)
public class ExampleModule extends Module {

    @Override
    public void onLoad() {
        NLogger.info("Module loading...");
    }

    @Override
    public void onEnable() {
        NLogger.info("Module enabled!");
    }

    @Override
    public void onDisable() {
        NLogger.info("Module disabled!");
    }
}
```

### Database Kullanan Modül

```java
package com.example;

import com.notpatch.nCore.module.Module;
import com.notpatch.nCore.module.ModuleInfo;
import com.notpatch.nCore.util.NLogger;

import java.sql.SQLException;

@ModuleInfo(
    name = "DatabaseModule",
    description = "Database kullanan örnek modül",
    authors = {"YourName"},
    requiresDatabase = true  // Database gerekli!
)
public class DatabaseModule extends Module {

    @Override
    public void onLoad() {
        NLogger.info("Module loading...");
    }

    @Override
    public void onEnable() {
        if (hasDatabase()) {
            try {
                createTables();
                NLogger.info("Database tables created!");
            } catch (SQLException e) {
                NLogger.error("Failed to create tables!");
                NLogger.exception(e);
            }
        }
    }

    @Override
    public void onDisable() {
        NLogger.info("Module disabled!");
    }

    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS my_table (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player_uuid VARCHAR(36) NOT NULL,
                data_value VARCHAR(255),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        getDatabaseManager().executeUpdate(sql);
    }
}
```

### Bağımlılıklar ile Modül

```java
@ModuleInfo(
    name = "DependentModule",
    description = "Başka modüle bağımlı modül",
    authors = {"YourName"},
    dependencies = {"ExampleModule"}  // Bu modül yüklenmeden önce ExampleModule yüklenecek
)
public class DependentModule extends Module {
    // ...
}
```

## ModuleInfo Parametreleri

| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| `name` | String | ✅ Evet | - | Modül adı (benzersiz olmalı) |
| `version` | String | ❌ Hayır | "1.0.0" | Modül versiyonu |
| `description` | String | ❌ Hayır | "" | Modül açıklaması |
| `authors` | String[] | ❌ Hayır | [] | Modül yazarları |
| `dependencies` | String[] | ❌ Hayır | [] | Gerekli modüller (önce yüklenecek) |
| `softDependencies` | String[] | ❌ Hayır | [] | Opsiyonel modüller |
| `requiresDatabase` | boolean | ❌ Hayır | false | Database gereksinimi |

## Module Lifecycle

```
1. onLoad()      -> Modül yüklenirken çağrılır
2. onEnable()    -> Modül etkinleştirilirken çağrılır
3. onDisable()   -> Modül devre dışı bırakılırken çağrılır
4. onReload()    -> Modül yeniden yüklenirken çağrılır (varsayılan: disable + enable)
```

## Database Kullanımı

Modülünüzde `requiresDatabase = true` belirtirseniz:
- Modül etkinleştirilmeden önce otomatik olarak database bağlantısı kurulur
- `getDatabaseManager()` ile database manager'a erişebilirsiniz
- `hasDatabase()` ile bağlantı kontrolü yapabilirsiniz

### Database İşlemleri

```java
// Update/Insert/Delete
getDatabaseManager().executeUpdate(
    "INSERT INTO my_table (player_uuid, data_value) VALUES (?, ?)",
    playerUUID.toString(),
    "some_value"
);

// Query
ResultSet rs = getDatabaseManager().executeQuery(
    "SELECT * FROM my_table WHERE player_uuid = ?",
    playerUUID.toString()
);
while (rs.next()) {
    String value = rs.getString("data_value");
    // ...
}
rs.close();
```

## Modül Derleme

### Maven ile

```xml
<dependencies>
    <dependency>
        <groupId>com.notpatch</groupId>
        <artifactId>NCore</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Derlemek için:
```bash
mvn clean package
```

## Runtime'da Modül Yükleme

1. Modül JAR dosyanızı `plugins/NCore/modules/` klasörüne atın
2. Oyun içinde `/module load <jarfile.jar>` komutunu çalıştırın
3. Modül otomatik olarak yüklenecek ve etkinleştirilecektir

## Sorun Giderme

### "Module not found" hatası
- Modül JAR dosyasının `modules/` klasöründe olduğundan emin olun
- JAR dosyası adını doğru yazdığınızdan emin olun (büyük/küçük harf duyarlı)

### "Failed to enable module: Database connection failed"
- `config.yml` içindeki database ayarlarını kontrol edin
- MySQL/MariaDB sunucusunun çalıştığından emin olun
- Database kullanıcı adı ve şifresinin doğru olduğunu kontrol edin

### "Missing dependency" hatası
- Bağımlı modüllerin yüklendiğinden emin olun
- `/module list` ile yüklü modülleri kontrol edin

## Lisans

Bu proje açık kaynaklıdır.

## İletişim

- GitHub: [NotPatch](https://github.com/notpatch)

