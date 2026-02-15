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

### Database

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
    requiresDatabase = true
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

