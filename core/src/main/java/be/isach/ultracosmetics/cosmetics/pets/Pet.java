package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.EntityCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.PetPathfinder;
import be.isach.ultracosmetics.util.ServerVersion;
import com.cryptomorin.xseries.XMaterial;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.goal.PathfinderLookAtEntity;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an instance of a pet summoned by a player.
 *
 * @author iSach
 * @since 03-08-2015
 */
public abstract class Pet extends EntityCosmetic<PetType, Mob> implements Updatable {

    /**
     * List of items popping out from Pet.
     */
    protected List<Item> items = new ArrayList<>();

    /**
     * ArmorStand for nametags. Only custom entity pets use this.
     */
    protected ArmorStand armorStand;

    /**
     * The {@link org.bukkit.inventory.ItemStack ItemStack} this pet drops, null if none.
     * Sometimes modified before dropping to change what is dropped
     */
    protected ItemStack dropItem;

    public Pet(UltraPlayer owner, PetType petType, UltraCosmetics ultraCosmetics, ItemStack dropItem) {
        super(owner, petType, ultraCosmetics,false);
        this.dropItem = dropItem;
    }

    public Pet(UltraPlayer owner, PetType petType, UltraCosmetics ultraCosmetics, XMaterial dropItem) {
        this(owner, petType, ultraCosmetics, dropItem.parseItem());
    }

    public Pet(UltraPlayer owner, PetType petType, UltraCosmetics ultraCosmetics) {
        this(owner, petType, ultraCosmetics, petType.getItemStack());
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onEquip() {
        initializeEntity();
    }

    @SuppressWarnings("deprecation")
    private void initializeEntity() {
        entity = spawnEntity();

        if (entity instanceof Ageable) {
            Ageable ageable = (Ageable) entity;
            if (SettingsManager.getConfig().getBoolean("Pets-Are-Babies")) {
                ageable.setBaby();
            } else {
                ageable.setAdult();
            }
            ageable.setAgeLock(true);
        }

        if (entity instanceof Tameable) {
            ((Tameable) entity).setTamed(true);
        }

        if (useArmorStandNameTag()) {
            armorStand = (ArmorStand) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setMarker(useMarkerArmorStand());
            armorStand.setCustomNameVisible(true);
            FixedMetadataValue metadataValue = new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand");
            armorStand.setMetadata("C_AD_ArmorStand", metadataValue);
            entity.setPassenger(armorStand);
        } else {
            getEntity().setCustomNameVisible(true);
        }

        // Must run AFTER setting the entity to a baby
        clearPathfinders();

        updateName();

        entity.getEquipment().clear();
        entity.setRemoveWhenFarAway(false);
        if (SettingsManager.getConfig().getBoolean("Pets-Are-Silent") && UltraCosmeticsData.get().getServerVersion().isAtLeast(ServerVersion.v1_9)) {
            entity.setSilent(true);
        }

        entity.setMetadata("Pet", new FixedMetadataValue(getUltraCosmetics(), "UltraCosmetics"));
        setupEntity();
    }

    private void clearPathfinders() {
        EntityBrain brain = BukkitBrain.getBrain(entity);
        brain.getGoalAI().clear();
        brain.getTargetAI().clear();
        brain.getScheduleManager().clear();

        brain.getGoalAI().put(new PetPathfinder(entity, getPlayer()), 0);
        brain.getGoalAI().put(new PathfinderLookAtEntity<>(entity, Player.class), 1);
    }

    @Override
    protected void scheduleTask() {
        runTaskTimer(getUltraCosmetics(), 0, 3);
    }

    @Override
    public boolean tryEquip() {
        if (getType().isMonster() && getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            getOwner().sendMessage(MessageManager.getMessage("Mounts.Cant-Spawn"));
            return false;
        }
        return true;
    }

    public boolean useArmorStandNameTag() {
        // setCustomNameVisible(true) doesn't seem to work on 1.8, so we'll just use armor stands in that case
        return isCustomEntity() || UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_8;
    }

    public boolean useMarkerArmorStand() {
        return true;
    }

    @Override
    public void run() {
        if (entity != null && !entity.isValid()) {
            clear();
            return;
        }

        if (!getOwner().isOnline() || getOwner().getCurrentPet() != this) {
            clear();
            return;
        }

        // Teleporting an entity across worlds seems to internally remove and re-add
        // the entity anyway, so we do it manually to keep pathfinders working correctly.
        if (entity.getWorld() != getPlayer().getWorld()) {
            entity.remove();
            initializeEntity();
        }

        onUpdate();
    }

    @Override
    protected void onClear() {
        // Remove Armor Stand.
        if (armorStand != null) {
            armorStand.remove();
        }

        // Remove Pet Entity.
        removeEntity();

        // Remove items.
        items.stream().filter(Entity::isValid).forEach(Entity::remove);

        // Clear items.
        items.clear();
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public boolean hasArmorStand() {
        return armorStand != null;
    }

    public List<Item> getItems() {
        return items;
    }

    public void updateName() {
        Entity rename;
        if (armorStand == null) {
            rename = entity;
        } else {
            rename = armorStand;
        }
        Component newName;
        if (getOwner().getPetName(getType()) != null) {
            newName = getOwner().getPetName(getType());
        } else {
            newName = getType().getEntityName(getPlayer());
        }
        rename.setCustomName(MessageManager.toLegacy(newName));
    }

    @Override
    public void onUpdate() {
        if (SettingsManager.getConfig().getBoolean("Pets-Drop-Items")) {
            dropItem();
        }
    }

    public void dropItem() {
        // Not using the ItemFactory variance method for this one
        // because we want to bump the Y velocity a bit between calcs.
        Vector velocity = new Vector(RANDOM.nextDouble() - 0.5, RANDOM.nextDouble() / 2.0 + 0.3, RANDOM.nextDouble() - 0.5).multiply(0.4);
        final Item drop = ItemFactory.spawnUnpickableItem(dropItem, ((LivingEntity) entity).getEyeLocation(), velocity);
        items.add(drop);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            drop.remove();
            items.remove(drop);
        }, 5);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == getEntity()) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() == getEntity()) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer() == getPlayer()) getEntity().teleport(getPlayer());
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (event.getEntity() == entity) event.setCancelled(true);
    }

    @Override
    protected Component appendActivateMessage(Component base) {
        Component name = getOwner().getPetName(getType());
        if (name == null) return base;
        return Component.empty().append(base).append(Component.text(" (", NamedTextColor.GRAY))
                .append(name).append(Component.text(")", NamedTextColor.GRAY));
    }

    public boolean isCustomEntity() {
        return false;
    }

    public boolean customize(String customization) {
        return false;
    }

    /**
     * Generics are confusing...
     * This function accepts an enum and a string representing a key to the enum.
     * If the arg is able to be parsed as a value of the enum, func will be called
     * with the resulting value.
     *
     * @param <T>   an enum (i.e. Variant)
     * @param types the enum class (i.e. Variant.class)
     * @param arg   the key to search for in the enum
     * @param func  the function to call upon success
     * @return true if arg was able to be parsed
     */
    protected <T extends Enum<T>> boolean enumCustomize(Class<T> types, String arg, Consumer<T> func) {
        return valueCustomize(s -> Enum.valueOf(types, s), arg, func);
    }

    protected <T> boolean valueCustomize(Function<String, T> valueFunc, String arg, Consumer<T> func) {
        T value;
        try {
            value = valueFunc.apply(arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
        func.accept(value);
        return true;
    }

    protected boolean customizeHeldItem(String customization) {
        String[] parts = customization.split(":", 2);
        Optional<XMaterial> mat = XMaterial.matchXMaterial(parts[0]);
        if (!mat.isPresent() || !mat.get().parseMaterial().isItem()) return false;
        ItemStack stack = mat.get().parseItem();
        if (parts.length > 1) {
            int model;
            try {
                model = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return false;
            }
            ItemMeta meta = stack.getItemMeta();
            meta.setCustomModelData(model);
            stack.setItemMeta(meta);
        }
        entity.getEquipment().setItemInMainHand(stack);
        return true;
    }
}
