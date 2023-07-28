package be.isach.ultracosmetics.mysql.column;

public class IntColumn extends Column<Integer> {

    public IntColumn(String name) {
        super(name, "INT", Integer.class);
    }

}
