package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.pets.*;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.SmartLogger;
import be.isach.ultracosmetics.util.SmartLogger.LogLevel;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Pet types.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class PetType extends CosmeticEntType<Pet> {

    private static final Map<EntityType, Class<? extends Pet>> PET_MAP = new HashMap<>();

    private final String customization;

    private PetType(String configName, XMaterial material, EntityType entityType, Class<? extends Pet> clazz, String customization,boolean special) {
        super(Category.PETS, configName, material, entityType, clazz,special);
        this.customization = customization;

        PET_MAP.putIfAbsent(entityType, clazz);

        if (GENERATE_MISSING_MESSAGES) {
            MessageManager.addMessage(getConfigPath() + ".menu-name", configName);
            MessageManager.addMessage(getConfigPath() + ".entity-displayname", "&l%playername%'s " + configName);
        }
    }

    private PetType(String configName, XMaterial material, EntityType entityType, Class<? extends Pet> clazz,boolean special) {
        this(configName, material, entityType, clazz, null,special);
    }

    public Component getEntityName(Player player) {
        return MessageManager.getMessage("Pets." + getConfigName() + ".entity-displayname",
                Placeholder.unparsed("playername", player.getName())
        );
    }

    @Override
    public Component getName() {
        return MessageManager.getMessage("Pets." + getConfigName() + ".menu-name");
    }

    @Override
    public Pet equip(UltraPlayer player, UltraCosmetics ultraCosmetics) {
        Pet pet = super.equip(player, ultraCosmetics);
        if (pet != null && customization != null) {
            if (!pet.customize(customization)) {
                UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.WARNING, "Invalid customization string for pet " + getConfigName());
                player.sendMessage(ChatColor.RED + "Invalid customization string, please contact an admin.");
            }
        }
        return pet;
    }

    public static void register(ServerVersion serverVersion) {
        new PetType("Piggy", XMaterial.PORKCHOP, EntityType.PIG, PetPiggy.class,false);
        new PetType("EasterBunny", XMaterial.CARROT, EntityType.RABBIT, PetEasterBunny.class,false);
        new PetType("Cow", XMaterial.MILK_BUCKET, EntityType.COW, PetCow.class,false);
        new PetType("Mooshroom", XMaterial.RED_MUSHROOM, EntityType.MUSHROOM_COW, PetMooshroom.class,false);
        new PetType("Dog", XMaterial.BONE, EntityType.WOLF, PetDog.class,false);
        new PetType("Chick", XMaterial.EGG, EntityType.CHICKEN, PetChick.class,false);
        new PetType("ChristmasElf", XMaterial.BEACON, EntityType.VILLAGER, PetChristmasElf.class,false);
        new PetType("IronGolem", XMaterial.IRON_INGOT, EntityType.IRON_GOLEM, PetIronGolem.class,false);
        new PetType("Snowman", XMaterial.SNOWBALL, EntityType.SNOWMAN, PetSnowman.class,false);
        new PetType("Villager", XMaterial.EMERALD, EntityType.VILLAGER, PetVillager.class,false);
        new PetType("Bat", XMaterial.COAL, EntityType.BAT, PetBat.class,false);
        new PetType("Sheep", XMaterial.WHITE_WOOL, EntityType.SHEEP, PetSheep.class,false);
        new PetType("Wither", XMaterial.WITHER_SKELETON_SKULL, EntityType.WITHER, PetWither.class,false) {
            @Override
            public void setupConfig(CustomConfiguration config, String path) {
                super.setupConfig(config, path);
                config.addDefault(path + ".Bossbar", "in range",
                        "Sets who the bossbar is visible for. (Has no effect on 1.8)",
                        "'in range': vanilla behavior, visible to all players in range.",
                        "'owner': only visible to pet owner",
                        "'none': not visible to any players");
            }
        };
        new PetType("Slime", XMaterial.SLIME_BALL, EntityType.SLIME, PetSlime.class,false);
        new PetType("Silverfish", XMaterial.GRAY_DYE, EntityType.SILVERFISH, PetSilverfish.class,false);
        new PetType("Blaze", XMaterial.BLAZE_ROD, EntityType.BLAZE, PetBlaze.class,false);
        new PetType("Creeper", XMaterial.GUNPOWDER, EntityType.CREEPER, PetCreeper.class,false);
        new PetType("Enderman", XMaterial.ENDER_PEARL, EntityType.ENDERMAN, PetEnderman.class,false);
        new PetType("Skeleton", XMaterial.BOW, EntityType.SKELETON, PetSkeleton.class,false);
        new PetType("Zombie", XMaterial.ROTTEN_FLESH, EntityType.ZOMBIE, PetZombie.class,false);
        /* Squid disabled because its not moving at all, its just turning around all the time */
        /* new PetType("Squid", XMaterial.INK_SAC, EntityType.SQUID, PetSquid.class); */
        new PetType("CaveSpider", XMaterial.SPIDER_EYE, EntityType.CAVE_SPIDER, PetCaveSpider.class,false);
        new PetType("Spider", XMaterial.STRING, EntityType.SPIDER, PetSpider.class,false);
        new PetType("Endermite", XMaterial.ENDER_EYE, EntityType.ENDERMITE, PetEndermite.class,false);
        /* Ghast disabled because its not moving at all (besides teleport) */
        /* new PetType("Ghast", XMaterial.FIRE_CHARGE, EntityType.GHAST, PetGhast.class); */
        new PetType("Guardian", XMaterial.PRISMARINE_SHARD, EntityType.GUARDIAN, PetGuardian.class,false);
        new PetType("MagmaCube", XMaterial.MAGMA_CREAM, EntityType.MAGMA_CUBE, PetMagmaCube.class,false);
        new PetType("Witch", XMaterial.POTION, EntityType.WITCH, PetWitch.class,false);
        new PetType("Horse", XMaterial.LEATHER_HORSE_ARMOR.or(XMaterial.LEATHER), EntityType.HORSE, PetHorse.class,false);

        // Cases fall through, so for example v1_19 gets all pets of 1.19 and below.
        switch (serverVersion) {
            case NEW:
            case v1_20:
                new PetType("Sniffer", XMaterial.TORCHFLOWER_SEEDS, EntityType.SNIFFER, PetSniffer.class,false);
            case v1_19:
                new PetType("Frog", XMaterial.LILY_PAD, EntityType.FROG, PetFrog.class,false);
                new PetType("Warden", XMaterial.SCULK_SHRIEKER, EntityType.WARDEN, PetWarden.class,false) {
                    @Override
                    public void setupConfig(CustomConfiguration config, String path) {
                        super.setupConfig(config, path);
                        config.addDefault(path + ".Block-Effect", true,
                                "Whether the darkness effect is blocked while this pet is equipped.",
                                "Please note that this will also block darkness from real wardens,",
                                "due to Spigot API limitations.");
                    }
                };
                new PetType("Allay", XMaterial.ALLAY_SPAWN_EGG, EntityType.ALLAY, PetAllay.class,false);
                new PetType("Tadpole", XMaterial.TADPOLE_BUCKET, EntityType.TADPOLE, PetTadpole.class,false);
            case v1_18:
                new PetType("Goat", XMaterial.GOAT_HORN.or(XMaterial.WHEAT), EntityType.GOAT, PetGoat.class,false);
            case v1_17:
                new PetType("Axolotl", XMaterial.AXOLOTL_BUCKET, EntityType.AXOLOTL, PetAxolotl.class,false) {
                    @Override
                    public void setupConfig(CustomConfiguration config, String path) {
                        super.setupConfig(config, path);
                        config.addDefault(path + ".Fast", false, "https://imgur.com/a/EKWwQ6w");
                    }
                };
                /* Glow Squid disabled because its not moving at all, its just turning around all the time */
                /* new PetType("GlowSquid", XMaterial.GLOW_INK_SAC, EntityType.GLOW_SQUID, PetGlowSquid.class); */
            case v1_16:
                new PetType("Piglin", XMaterial.GOLD_INGOT, EntityType.PIGLIN, PetPiglin.class,false);
                new PetType("Strider", XMaterial.WARPED_FUNGUS, EntityType.STRIDER, PetStrider.class,false);
                new PetType("Hoglin", XMaterial.COOKED_PORKCHOP, EntityType.HOGLIN, PetHoglin.class,false);
                new PetType("PiglinBrute", XMaterial.GOLDEN_AXE, EntityType.PIGLIN_BRUTE, PetPiglinBrute.class,false);
                new PetType("Zoglin", XMaterial.ZOGLIN_SPAWN_EGG, EntityType.ZOGLIN, PetZoglin.class,false);
                new PetType("ZombifiedPiglin", XMaterial.GOLDEN_SWORD, EntityType.ZOMBIFIED_PIGLIN, PetZombifiedPiglin.class,false);
            case v1_15:
                new PetType("Bee", XMaterial.HONEYCOMB, EntityType.BEE, PetBee.class,false);
            case v1_14:
                new PetType("Panda", XMaterial.BAMBOO, EntityType.PANDA, PetPanda.class,false);
                new PetType("Fox", XMaterial.SWEET_BERRIES, EntityType.FOX, PetFox.class,false);
                new PetType("Kitty", XMaterial.TROPICAL_FISH, EntityType.CAT, PetKitty.class,false);
                new PetType("Ocelot", XMaterial.COD, EntityType.OCELOT, PetOcelot.class,false);
                new PetType("WanderingTrader", XMaterial.WANDERING_TRADER_SPAWN_EGG, EntityType.WANDERING_TRADER, PetWanderingTrader.class,false);
                new PetType("Pillager", XMaterial.CROSSBOW, EntityType.PILLAGER, PetPillager.class,false);
                new PetType("Ravager", XMaterial.RAVAGER_SPAWN_EGG, EntityType.RAVAGER, PetRavager.class,false);
            case v1_13:
                new PetType("Cod", XMaterial.COD_BUCKET, EntityType.COD, PetCod.class,false);
                new PetType("Pufferfish", XMaterial.PUFFERFISH, EntityType.PUFFERFISH, PetPufferfish.class,false);
                new PetType("Salmon", XMaterial.SALMON_BUCKET, EntityType.SALMON, PetSalmon.class,false);
                new PetType("TropicalFish", XMaterial.TROPICAL_FISH_BUCKET, EntityType.TROPICAL_FISH, PetTropicalFish.class,false);
                new PetType("Turtle", XMaterial.TURTLE_HELMET, EntityType.TURTLE, PetTurtle.class,false);
                new PetType("Dolphin", XMaterial.DOLPHIN_SPAWN_EGG, EntityType.DOLPHIN, PetDolphin.class,false);
                new PetType("Drowned", XMaterial.TRIDENT, EntityType.DROWNED, PetDrowned.class,false);
                /* Phantom disabled because its just stuck flying down into the ground */
                /* new PetType("Phantom", XMaterial.PHANTOM_MEMBRANE, EntityType.PHANTOM, PetPhantom.class); */
            case v1_12:
                new PetType("Parrot", XMaterial.COOKIE, EntityType.PARROT, PetParrot.class,false);
                new PetType("Illusioner", XMaterial.COMMAND_BLOCK, EntityType.ILLUSIONER, PetIllusioner.class,false);
            case v1_11:
                new PetType("Llama", XMaterial.RED_WOOL, EntityType.LLAMA, PetLlama.class,false);
                new PetType("Vex", XMaterial.IRON_SWORD, EntityType.VEX, PetVex.class,false);
                new PetType("Evoker", XMaterial.TOTEM_OF_UNDYING, EntityType.EVOKER, PetEvoker.class,false);
                new PetType("Vindicator", XMaterial.IRON_AXE, EntityType.VINDICATOR, PetVindicator.class,false);
                // Some entities are here because they were split into different EntityTypes in 1.11
                new PetType("Donkey", XMaterial.CHEST, EntityType.DONKEY, PetDonkey.class,false);
                new PetType("Mule", XMaterial.SADDLE, EntityType.MULE, PetMule.class,false);
                new PetType("SkeletonHorse", XMaterial.BONE_BLOCK, EntityType.SKELETON_HORSE, PetSkeletonHorse.class,false);
                new PetType("ZombieHorse", XMaterial.ZOMBIE_HORSE_SPAWN_EGG, EntityType.ZOMBIE_HORSE, PetZombieHorse.class,false);
                new PetType("ElderGuardian", XMaterial.PRISMARINE_CRYSTALS, EntityType.ELDER_GUARDIAN, PetElderGuardian.class,false) {
                    @Override
                    public void setupConfig(CustomConfiguration config, String path) {
                        super.setupConfig(config, path);
                        config.addDefault(path + ".Block-Effect", true,
                                "Whether the mining fatigue effect is blocked while this pet is equipped.",
                                "Please note that this will also block mining fatigue from real elder guardians,",
                                "due to Spigot API limitations.");
                    }
                };
                new PetType("WitherSkeleton", XMaterial.STONE_SWORD, EntityType.WITHER_SKELETON, PetWitherSkeleton.class,false);
                new PetType("ZombieVillager", XMaterial.GOLDEN_APPLE, EntityType.ZOMBIE_VILLAGER, PetZombieVillager.class,false);
                new PetType("Husk", XMaterial.SAND, EntityType.HUSK, PetHusk.class,false);
                new PetType("Stray", XMaterial.ARROW, EntityType.STRAY, PetStray.class,false);
            case v1_10:
                new PetType("PolarBear", XMaterial.SNOW_BLOCK, EntityType.POLAR_BEAR, PetPolarBear.class,false);
            case v1_9:
                new PetType("Shulker", XMaterial.SHULKER_BOX, EntityType.SHULKER, PetShulker.class,false);
            case v1_8:
                break;
        }

        if (UltraCosmeticsData.get().getVersionManager().isUsingNMS()) {
            new PetType("Pumpling", XMaterial.PUMPKIN, EntityType.ZOMBIE, UltraCosmeticsData.get().getVersionManager().getModule().getPumplingClass(),false);
        }

        if (!serverVersion.isAtLeast(ServerVersion.v1_14)) {
            new PetType("Kitty", XMaterial.TROPICAL_FISH, EntityType.OCELOT, PetKitty.class,false);
        }

        ConfigurationSection pets = getCustomConfig(Category.PETS);
        if (pets == null) return;

        EntityType type;
        Optional<XMaterial> mat;
        SmartLogger log = UltraCosmeticsData.get().getPlugin().getSmartLogger();
        for (String key : pets.getKeys(false)) {
            ConfigurationSection pet = pets.getConfigurationSection(key);
            if (!pet.isString("type") || !pet.isString("item") || !pet.isString("customization")) {
                log.write(LogLevel.WARNING, "Incomplete custom pet '" + key + "'");
                continue;
            }
            try {
                type = EntityType.valueOf(pet.getString("type").toUpperCase());
            } catch (IllegalArgumentException e) {
                log.write(LogLevel.WARNING, "Invalid entity type for custom pet '" + key + "'");
                continue;
            }
            if (!PET_MAP.containsKey(type)) {
                log.write(LogLevel.WARNING, "Entity type '" + type + "' for pet '" + key + "' does not exist as a pet.");
                continue;
            }
            mat = XMaterial.matchXMaterial(pet.getString("item"));
            if (!mat.isPresent() || !mat.get().parseMaterial().isItem()) {
                log.write(LogLevel.WARNING, "Invalid item for custom pet '" + key + "'");
                continue;
            }
            MessageManager.addMessage(Category.PETS.getConfigPath() + "." + key + ".menu-name", key);
            MessageManager.addMessage(Category.PETS.getConfigPath() + "." + key + ".entity-displayname", "&l%playername%'s " + key);
            MessageManager.addMessage(Category.PETS.getConfigPath() + "." + key + ".Description", "A custom pet!");
            new PetType(key, mat.get(), type, PET_MAP.get(type), pet.getString("customization"),false);
        }
    }
}
