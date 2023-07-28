package be.isach.ultracosmetics.treasurechests;

public enum TreasureChestType {

    DEFAULT("Normal Chest",1),
    PREMIUM("Premium Chest",2),
    SPECIAL("Special Chest",3),
    ;

    private final String name;
    private final int multiplier;

    private TreasureChestType(String name, int multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public String getName() {
        return name;
    }

    public static TreasureChestType fromString(String s) {
        for(TreasureChestType type: values()) {
            if(type.toString().equalsIgnoreCase(s)) return type;
        }
        return null;
    }
}
