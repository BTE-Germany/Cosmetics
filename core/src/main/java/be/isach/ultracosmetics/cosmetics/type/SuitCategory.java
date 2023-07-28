package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.cosmetics.suits.SuitAstronaut;
import be.isach.ultracosmetics.cosmetics.suits.SuitCursed;
import be.isach.ultracosmetics.cosmetics.suits.SuitDiamond;
import be.isach.ultracosmetics.cosmetics.suits.SuitFrozen;
import be.isach.ultracosmetics.cosmetics.suits.SuitRave;
import be.isach.ultracosmetics.cosmetics.suits.SuitSanta;
import be.isach.ultracosmetics.cosmetics.suits.SuitSlime;
import be.isach.ultracosmetics.util.MathUtils;

import org.bukkit.Color;

import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum SuitCategory {
    RAVE(
            "Rave",
            "rave",
            XMaterial.LEATHER_HELMET,
            XMaterial.LEATHER_CHESTPLATE,
            XMaterial.LEATHER_LEGGINGS,
            XMaterial.LEATHER_BOOTS,
            // MathUtils.random(int) is inclusive
            s -> Color.fromRGB(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255)),
            SuitRave.class) {
        @Override
        public void setupConfig(CustomConfiguration config, String path) {
            config.addDefault(path + ".Update-Delay-In-Creative", 10,
                    "How many ticks UC should wait between updating the rave suit for creative players.",
                    "Setting this to a higher value allows more time between updates,",
                    "meaning players shouldn't have their inventories close immediately after opening.",
                    "Set to 1 or less to update every tick.");
        }
    },
    ASTRONAUT("Astronaut", "astronaut", XMaterial.GLASS, XMaterial.GOLDEN_CHESTPLATE, XMaterial.GOLDEN_LEGGINGS, XMaterial.GOLDEN_BOOTS, s -> null, SuitAstronaut.class),
    DIAMOND("Diamond", "diamond", XMaterial.DIAMOND_HELMET, XMaterial.DIAMOND_CHESTPLATE, XMaterial.DIAMOND_LEGGINGS, XMaterial.DIAMOND_BOOTS, s -> null, SuitDiamond.class),
    SANTA("Santa", "santa", XMaterial.LEATHER_HELMET, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, s -> Color.RED, SuitSanta.class),
    FROZEN("Frozen", "frozen", XMaterial.PACKED_ICE, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, s -> (s == ArmorSlot.HELMET ? null : Color.AQUA), SuitFrozen.class),
    CURSED("Cursed", "cursed", XMaterial.JACK_O_LANTERN, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, s -> (s == ArmorSlot.HELMET ? null : Color.fromRGB(35, 30, 42)), SuitCursed.class),
    SLIME("Slime", "slime", XMaterial.SLIME_BLOCK, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS, s -> (s == ArmorSlot.HELMET ? null : Color.fromRGB(128, 241, 95)), SuitSlime.class),
    ;

    private final String configName;
    private final String permissionSuffix;
    private final XMaterial helmetType;
    private final XMaterial chestplateType;
    private final XMaterial leggingsType;
    private final XMaterial bootsType;
    private SuitType helmet;
    private SuitType chestplate;
    private SuitType leggings;
    private SuitType boots;
    private final Class<? extends Suit> clazz;
    private final Function<ArmorSlot,Color> colorFunc;

    private SuitCategory(String configName, String permissionSuffix, XMaterial helmet, XMaterial chestplate,
            XMaterial leggings, XMaterial boots, Function<ArmorSlot,Color> colorFunc, Class<? extends Suit> clazz) {
        this.configName = configName;
        this.permissionSuffix = permissionSuffix;
        this.clazz = clazz;
        this.helmetType = helmet;
        this.chestplateType = chestplate;
        this.leggingsType = leggings;
        this.bootsType = boots;
        this.colorFunc = colorFunc;
    }

    public void initializeSuitParts() {
        this.helmet = new SuitType(helmetType, ArmorSlot.HELMET, this,false);
        this.chestplate = new SuitType(chestplateType, ArmorSlot.CHESTPLATE, this,false);
        this.leggings = new SuitType(leggingsType, ArmorSlot.LEGGINGS, this,false);
        this.boots = new SuitType(bootsType, ArmorSlot.BOOTS, this,false);
        if (CosmeticType.GENERATE_MISSING_MESSAGES) {
            MessageManager.addMessage(getConfigPath() + ".whole-equip", "&f&lwhole " + configName + " &f&lsuit");
            MessageManager.addMessage(getConfigPath() + ".helmet-name", configName + " &f&Helmet");
            MessageManager.addMessage(getConfigPath() + ".chestplate-name", configName + " &f&lChestplate");
            MessageManager.addMessage(getConfigPath() + ".leggings-name", configName + " &f&lLeggings");
            MessageManager.addMessage(getConfigPath() + ".boots-name", configName + " &f&lBoots");
        }
    }

    public String getConfigName() {
        return configName;
    }

    public String getConfigPath() {
        return helmet.getConfigPath();
    }

    public String getPermissionSuffix() {
        return permissionSuffix;
    }

    public Class<? extends Suit> getSuitClass() {
        return clazz;
    }

    public SuitType getHelmet() {
        return helmet;
    }

    public SuitType getChestplate() {
        return chestplate;
    }

    public SuitType getLeggings() {
        return leggings;
    }

    public SuitType getBoots() {
        return boots;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Suits." + configName + ".Enabled");
    }

    public SuitType getPiece(ArmorSlot slot) {
        switch (slot) {
        case HELMET:
            return getHelmet();
        case CHESTPLATE:
        default:
            return getChestplate();
        case LEGGINGS:
            return getLeggings();
        case BOOTS:
            return getBoots();
        }
    }

    public List<SuitType> getPieces() {
        return Arrays.asList(getHelmet(), getChestplate(), getLeggings(), getBoots());
    }

    public Color getColor(ArmorSlot slot) {
        return colorFunc.apply(slot);
    }

    public void setupConfig(CustomConfiguration config, String path) {
    }

    public static List<SuitCategory> enabled() {
        List<SuitCategory> enabled = new ArrayList<>(Arrays.asList(values()));
        return enabled;
    }
}
