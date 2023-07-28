package be.isach.ultracosmetics.mysql.tables;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.mysql.column.Column;
import be.isach.ultracosmetics.mysql.column.StringColumn;
import be.isach.ultracosmetics.mysql.column.UniqueConstraint;
import be.isach.ultracosmetics.mysql.query.InsertQuery;
import be.isach.ultracosmetics.mysql.query.InsertValue;
import be.isach.ultracosmetics.mysql.query.SelectSubquery;

import javax.sql.DataSource;

public class CosmeticTable extends Table {

    public CosmeticTable(DataSource dataSource, String name) {
        super(dataSource, name);
    }

    @Override
    public void setupTableInfo() {
        tableInfo.add(new Column<>("id", "INTEGER AUTO_INCREMENT PRIMARY KEY", Integer.class));
        tableInfo.add(new StringColumn("category", 32, true));
        tableInfo.add(new StringColumn("type", 32, true));
        tableInfo.add(new UniqueConstraint("category", "type"));
        tableInfo.add(new UniqueConstraint("id", "category"));
        tableInfo.add(new Column<>("price", "INTEGER NOT NULL DEFAULT 0", Integer.class));
        }

    @Override
    public void loadBaseData() {
        InsertQuery insert = insertIgnore("category", "type");
        for (Category cat : Category.values()) {
            InsertValue catItem = new InsertValue(cat.toString().toLowerCase());
            for (CosmeticType<?> type : cat.getValues()) {
                insert.insert(catItem, new InsertValue(type.getConfigName().toLowerCase()));
            }
        }
        insert.execute();
    }

    public SelectSubquery subqueryFor(CosmeticType<?> type, boolean insert) {
        return new SelectSubquery(insert ? null : "id", getWrappedName(), "id")
                .where("category", cleanCategoryName(type)).where("type", cleanCosmeticName(type));
    }

    /*
     * Debugging only. For actual database interactions, use `subqueryFor`
     */
    public int getCosmeticID(CosmeticType<?> type) {
        return select("id").where("category", cleanCategoryName(type)).where("type", cleanCosmeticName(type)).asInt();
    }
}
