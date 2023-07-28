package be.isach.ultracosmetics.mysql.tables;

import be.isach.ultracosmetics.mysql.column.Column;
import be.isach.ultracosmetics.mysql.column.UUIDColumn;
import be.isach.ultracosmetics.mysql.column.VirtualUUIDColumn;
import be.isach.ultracosmetics.player.profile.ProfileKey;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import javax.sql.DataSource;

public class PlayerDataTable extends Table {

    public PlayerDataTable(DataSource dataSource, String name) {
        super(dataSource, name);
    }

    @Override
    public void setupTableInfo() {
        tableInfo.add(new UUIDColumn("uuid", "BINARY(16) PRIMARY KEY"));
        tableInfo.add(new VirtualUUIDColumn());
        tableInfo.add(new Column<>("gadgetsEnabled", "BOOLEAN NOT NULL DEFAULT 1", Boolean.class));
        tableInfo.add(new Column<>("selfMorphView", "BOOLEAN NOT NULL DEFAULT 1", Boolean.class));
        tableInfo.add(new Column<>("treasureNotifications", "BOOLEAN NOT NULL DEFAULT 0", Boolean.class));
        tableInfo.add(new Column<>("filterByOwned", "BOOLEAN NOT NULL DEFAULT 0", Boolean.class));
        tableInfo.add(new Column<>("treasureKeys", "INTEGER NOT NULL DEFAULT 0", Integer.class));
        tableInfo.add(new Column<>("coins", "INTEGER NOT NULL DEFAULT 0", Integer.class));
    }

    public void addPlayer(UUID uuid) {
        insertIgnore("uuid").insert(insertUUID(uuid)).execute();
    }

    public Map<String,Object> getSettings(UUID uuid) {
        StringJoiner columns = new StringJoiner(", ");
        for (ProfileKey key : ProfileKey.values()) {
            if (key.getSqlKey() == null) continue;
            columns.add(key.getSqlKey());
        }
        return select(columns.toString()).uuid(uuid).getResults(r -> {
            Map<String,Object> settings = new HashMap<>();
            for (ProfileKey key : ProfileKey.values()) {
                if (key.getSqlKey() == null) continue;
                if (key == ProfileKey.KEYS) {
                    settings.put(key.getSqlKey(), r.getInt(key.getSqlKey()));
                    continue;
                }
                settings.put(key.getSqlKey(), r.getBoolean(key.getSqlKey()));
            }

            return settings;
        }, false);
    }

    public boolean getSetting(UUID uuid, ProfileKey key) {
        return select(key.getSqlKey()).uuid(uuid).asBool();
    }

    public void setSetting(UUID uuid, ProfileKey key, Object value) {
        update().uuid(uuid).set(key.getSqlKey(), value).execute();
    }

    public int getKeys(UUID uuid) {
        return select("treasureKeys").uuid(uuid).asInt();
    }

    public void setKeys(UUID uuid, int keys) {
        update().uuid(uuid).set("treasureKeys", keys).execute();
    }


    public int getCoins(UUID uuid) {
        return select("coins").uuid(uuid).asInt();
    }

    public void setCoins(UUID uuid, int keys) {
        update().uuid(uuid).set("coins", keys).execute();
    }

    public void addCoins(UUID uuid, int coins) {
        int current = getCoins(uuid);
        setCoins(uuid, current + coins);
    }
}
