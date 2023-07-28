package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.mounts.*;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.VersionManager;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A cosmetic type.
 *
 * @author iSach
 * @since 12-18-2015
 */
public class MountType extends CosmeticEntType<Mount> {

    private final int repeatDelay;
    private final List<XMaterial> defaultBlocks;
    private final double defaultSpeed;
    private final double movementSpeed;

    private MountType(String configName, XMaterial material, EntityType entityType, int repeatDelay, double defaultSpeed, Class<? extends Mount> mountClass, List<XMaterial> defaultBlocks,boolean special) {
        super(Category.MOUNTS, configName, material, entityType, mountClass,special);
        this.repeatDelay = repeatDelay;
        this.defaultBlocks = defaultBlocks;
        this.defaultSpeed = defaultSpeed;
        this.movementSpeed = SettingsManager.getConfig().getDouble("Mounts." + configName + ".Speed", defaultSpeed);
        setupConfigLate(SettingsManager.getConfig(), getConfigPath());
        if (GENERATE_MISSING_MESSAGES) {
            MessageManager.addMessage(getConfigPath() + ".menu-name", configName);
            MessageManager.addMessage(getConfigPath() + ".entity-displayname", "&l%playername%'s " + configName);
        }
    }

    private MountType(String configName, XMaterial material, EntityType entityType, int repeatDelay, double defaultSpeed, Class<? extends Mount> mountClass,boolean special) {
        this(configName, material, entityType, repeatDelay, defaultSpeed, mountClass, null,special);
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public double getDefaultMovementSpeed() {
        return defaultSpeed;
    }

    @Override
    public Component getName() {
        return MessageManager.getMessage("Mounts." + getConfigName() + ".menu-name");
    }

    public Component getName(Player player) {
        return MessageManager.getMessage("Mounts." + getConfigName() + ".entity-displayname",
                Placeholder.unparsed("playername", player.getName())
        );
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public List<XMaterial> getDefaultBlocks() {
        return defaultBlocks;
    }

    public boolean doesPlaceBlocks() {
        return defaultBlocks != null;
    }

    public void setupConfigLate(CustomConfiguration config, String path) {
        if (LivingEntity.class.isAssignableFrom(getEntityType().getEntityClass())) {
            config.addDefault(path + ".Speed", getDefaultMovementSpeed(), "The movement speed of the mount, see:", "https://minecraft.fandom.com/wiki/Attribute#Attributes_available_on_all_living_entities");
        }
        if (doesPlaceBlocks()) {
            // Don't use Stream#toList(), it doesn't exist in Java 8
            config.addDefault(path + ".Blocks-To-Place", getDefaultBlocks().stream().map(Enum::name).collect(Collectors.toList()), "Blocks to choose from as this mount walks.");
        }
    }

    public static void register(ServerVersion version) {
        VersionManager vm = UltraCosmeticsData.get().getVersionManager();
        new MountType("DruggedHorse", XMaterial.SUGAR, EntityType.HORSE, 2, 1.1, MountDruggedHorse.class,false);
        new MountType("GlacialSteed", XMaterial.PACKED_ICE, EntityType.HORSE, 2, 0.4, MountGlacialSteed.class, Collections.singletonList(XMaterial.SNOW_BLOCK),false);
        new MountType("MountOfFire", XMaterial.BLAZE_POWDER, EntityType.HORSE, 2, 0.4, MountOfFire.class, Arrays.asList(XMaterial.ORANGE_TERRACOTTA, XMaterial.YELLOW_TERRACOTTA, XMaterial.RED_TERRACOTTA),false);
        new MountType("Snake", XMaterial.WHEAT_SEEDS, EntityType.SHEEP, 2, 0.3, MountSnake.class,false);
        new MountType("HypeCart", XMaterial.MINECART, EntityType.MINECART, 1, 0, MountHypeCart.class,false);
        new MountType("MoltenSnake", XMaterial.MAGMA_CREAM, EntityType.MAGMA_CUBE, 1, 0.4, MountMoltenSnake.class,false);
        new MountType("SlimeSnake", XMaterial.SLIME_BLOCK, EntityType.SLIME, 1, 0.4, MountSlimeSnake.class,false);
        new MountType("MountOfWater", XMaterial.LIGHT_BLUE_DYE, EntityType.HORSE, 2, 0.4, MountOfWater.class, Arrays.asList(XMaterial.LIGHT_BLUE_TERRACOTTA, XMaterial.CYAN_TERRACOTTA, XMaterial.BLUE_TERRACOTTA),false);
        new MountType("EcologistHorse", XMaterial.GREEN_DYE, EntityType.HORSE, 2, 0.4, MountEcologistHorse.class, Arrays.asList(XMaterial.LIME_TERRACOTTA, XMaterial.GREEN_TERRACOTTA),false);
        new MountType("Rudolph", XMaterial.DEAD_BUSH, horseOrType("MULE", version), 1, 0.4, MountRudolph.class,false);
        new MountType("WalkingDead", XMaterial.ROTTEN_FLESH, horseOrType("ZOMBIE_HORSE", version), 2, 0.4, MountWalkingDead.class,false);
        new MountType("InfernalHorror", XMaterial.BONE, horseOrType("SKELETON_HORSE", version), 2, 0.4, MountInfernalHorror.class,false);
        new MountType("Horse", XMaterial.SADDLE, horseOrType("HORSE", version), 0, 0.3, MountHorse.class,false);
        new MountType("Donkey", XMaterial.CHEST, horseOrType("DONKEY", version), 0, 0.25, MountDonkey.class,false);
        new MountType("Mule", XMaterial.ENDER_CHEST, horseOrType("MULE", version), 0, 0.25, MountMule.class,false);
        new MountType("Pig", XMaterial.PORKCHOP, EntityType.PIG, 0, 0.35, MountPig.class,false);

        if (version.isMobChipAvailable()) {
            new MountType("NyanSheep", XMaterial.CYAN_DYE, EntityType.SHEEP, 1, 0.4, MountNyanSheep.class,false);
            new MountType("Dragon", XMaterial.DRAGON_EGG, EntityType.ENDER_DRAGON, 1, 0.7, MountDragon.class,false) {
                @Override
                public void setupConfig(CustomConfiguration config, String path) {
                    super.setupConfig(config, path);
                    config.addDefault("Mounts.Dragon.Stationary", false, "If true, the dragon will not move.");
                }
            };
        }

        if (version.isAtLeast(ServerVersion.v1_16)) {
            new MountType("Strider", XMaterial.WARPED_FUNGUS_ON_A_STICK, EntityType.STRIDER, 0, 0.35, MountStrider.class,false);
        }

        if (version.isAtLeast(ServerVersion.v1_20)) {
            new MountType("Camel", XMaterial.CACTUS, EntityType.CAMEL, 0, 0.35, MountCamel.class,false);
        }

        if (vm.isUsingNMS()) {
            new MountType("Slime", XMaterial.SLIME_BALL, EntityType.SLIME, 2, 0.8, vm.getModule().getSlimeClass(),false);
            new MountType("Spider", XMaterial.COBWEB, EntityType.SPIDER, 2, 0.4, vm.getModule().getSpiderClass(),false);
        }
    }

    private static EntityType horseOrType(String name, ServerVersion version) {
        if (!version.isAtLeast(ServerVersion.v1_11)) return EntityType.HORSE;
        return EntityType.valueOf(name);
    }
}
