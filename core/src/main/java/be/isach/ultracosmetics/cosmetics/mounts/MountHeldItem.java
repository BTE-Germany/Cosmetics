package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UnmovableItemProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class MountHeldItem extends Mount implements UnmovableItemProvider {
    private final ItemStack heldItem;
    private final int slot = SettingsManager.getConfig().getInt("Gadget-Slot");
    private final boolean removeWithDrop = SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop");

    public MountHeldItem(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, type, ultraCosmetics);

        heldItem = new ItemStack(getHeldItemMaterial());
        ItemMeta meta = heldItem.getItemMeta();
        String loreString = MessageManager.getMessage(getOptionPath("Held-Item-Lore"));
        meta.setLore(Arrays.asList(loreString.split("\n")));
        heldItem.setItemMeta(meta);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        getUltraCosmetics().getUnmovableItemListener().addProvider(this);
    }

    @Override
    public void clear() {
        super.clear();
        getPlayer().getInventory().setItem(slot, null);
        getUltraCosmetics().getUnmovableItemListener().removeProvider(this);
    }

    @Override
    public boolean tryEquip() {
        getOwner().removeCosmetic(Category.GADGETS);
        getOwner().removeCosmetic(Category.MOUNTS);
        if (getPlayer().getInventory().getItem(slot) != null) {
            getPlayer().sendMessage(MessageManager.getMessage("Must-Remove.Mounts").replace("%slot%", String.valueOf(slot + 1)));
            return false;
        }
        getPlayer().getInventory().setItem(slot, heldItem);
        getPlayer().getInventory().setHeldItemSlot(slot);
        return true;
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void handleDrop(PlayerDropItemEvent event) {
        if (removeWithDrop) {
            clear();
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean itemMatches(ItemStack stack) {
        return stack != null && stack.isSimilar(heldItem);
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void moveItem(int slot, Player player) {
        clear();
    }

    @Override
    public void handleInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    public ItemStack getHeldItem() {
        return heldItem.clone();
    }

    public abstract Material getHeldItemMaterial();
}
