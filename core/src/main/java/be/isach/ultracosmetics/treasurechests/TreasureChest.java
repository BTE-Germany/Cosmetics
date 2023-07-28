package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.treasurechests.loot.LootReward;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class TreasureChest implements Listener {

    private final Player player;
    private final Location chestLocation;
    private final TreasureRandomizer randomGenerator;
    private Item item;
    private final ArrayList<ArmorStand> holograms;
    private final TreasureChest instance;
    private boolean opened = false;

    private TreasureChestType type;

    public TreasureChest(UUID owner) {
        this(owner, TreasureChestType.DEFAULT);
    }
    public TreasureChest(UUID owner, TreasureChestType type) {
        holograms = new ArrayList<>();
        this.type = type;
        this.player = getPlayer(owner);
        boolean allowDamage = SettingsManager.getConfig().getBoolean("TreasureChests.Allow-Damage");
        Location playerLocation = player.getLocation();
        chestLocation = playerLocation.clone().add(new Vector(0,0,3));

        chestLocation.getBlock().setType(Material.CHEST);

        UltraCosmetics uc = UltraCosmeticsData.get().getPlugin();
        UltraPlayerManager pm = uc.getPlayerManager();

        Bukkit.getPluginManager().registerEvents(this, uc);


        Block centerPossibleBlock = player.getLocation().getBlock();


        randomGenerator = new TreasureRandomizer(player, playerLocation);


        Bukkit.getScheduler().runTaskLater(uc, () -> {
            if (pm.getUltraPlayer(player) != null && pm.getUltraPlayer(player).getCurrentTreasureChest() == TreasureChest.this) {
                forceOpen(0);
            }
        }, 1200L);

        pm.getUltraPlayer(player).setCurrentTreasureChest(this);

        instance = this;
    }

    public Player getPlayer(UUID owner) {
        if (owner != null) {
            return Bukkit.getPlayer(owner);
        }
        return null;
    }

    public Location getChestLocation() {
        return chestLocation;
    }


    public void forceOpen(int i) {
        Block block = chestLocation.getBlock();
        opened = true;

        Lidded state = (Lidded) block.getState();
        state.open();
        ((BlockState) state).update();

        randomGenerator.setLocation(block.getLocation().clone().add(0.0D, 1.0D, 0.0D));
        LootReward reward = randomGenerator.giveRandomThing(this);

        item = spawnItem(reward.getStack(), block.getLocation());
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> makeHolograms(block.getLocation(), reward), 10L);
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, 100L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(event.getPlayer() != player) return;
        if(opened) return;
        if(block==null) return;
        if(block.getType()!=Material.CHEST) return;
        event.setCancelled(true);
        opened = true;

        Lidded state = (Lidded) block.getState();
        state.open();
        ((BlockState) state).update();

        randomGenerator.setLocation(block.getLocation().clone().add(0.0D, 1.0D, 0.0D));
        LootReward reward = randomGenerator.giveRandomThing(this);

        item = spawnItem(reward.getStack(), block.getLocation());
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> makeHolograms(block.getLocation(), reward), 10L);
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), this::clear, 100L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer() == player && !event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            event.setCancelled(true);
        }
    }


    private Item spawnItem(ItemStack stack, Location loc) {
        return ItemFactory.spawnUnpickableItem(stack, loc.clone().add(0.5, 1.2, 0.5), new Vector(0, 0.25, 0));
    }

    private void makeHolograms(Location location, LootReward reward) {
        String[] names = reward.getName();
        Location loc = location.clone().add(0.5, 0.3, 0.5);
        for (int i = names.length - 1; i >= 0; i--) {
            spawnHologram(loc, names[i]);
            loc.add(0, 0.25, 0);
        }
    }

    private ArmorStand spawnHologram(Location location, String s) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomName(s);
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "C_AD_ArmorStand"));
        holograms.add(armorStand);
        return armorStand;
    }

    public void clear() {
        for(ArmorStand h: holograms) {
            h.remove();
        }
        item.remove();
        chestLocation.getBlock().setType(Material.AIR);
        if (player != null) {
            UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).setCurrentTreasureChest(null);
        }
        HandlerList.unregisterAll(instance);
    }

    public TreasureChestType getType() {
        return type;
    }

}
