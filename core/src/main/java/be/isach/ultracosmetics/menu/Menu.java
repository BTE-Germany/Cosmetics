package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a Menu. A menu can have multiple pages in case of cosmetics.
 * Each item in the menu will, when clicked by a player, execute a ClickRunnable.
 *
 * @author iSach
 * @since 07-05-2016
 */
public abstract class Menu implements Listener {

    /**
     * UltraCosmetcs Instance.
     */
    protected final UltraCosmetics ultraCosmetics;

    /**
     * Click Runnables maps.
     * <p>
     * Key: Item
     * Value: ClickRunnable to call when item is clicked.
     */
    private final Map<Inventory, Map<ItemStack, Button>> clickRunnableMap = new HashMap<>();
    private final boolean fillEmpty = SettingsManager.getConfig().getBoolean("Fill-Blank-Slots-With-Item.Enabled");

    private final ItemStack fillerItem = getFillerItem();

    public Menu(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;

        ultraCosmetics.getServer().getPluginManager().registerEvents(this, ultraCosmetics);
    }

    public void open(UltraPlayer player) {
        player.getBukkitPlayer().openInventory(getInventory(player));
    }

    public void refresh(UltraPlayer player) {
        open(player);
    }

    protected Inventory createInventory(Component name) {
        Inventory inventory = Bukkit.createInventory(new CosmeticsInventoryHolder(), getSize(), MessageManager.toLegacy(name));
        ((CosmeticsInventoryHolder) inventory.getHolder()).setInventory(inventory);
        return inventory;
    }

    public Inventory getInventory(UltraPlayer player) {
        Inventory inventory = createInventory(getName());
        putItems(inventory, player);
        fillInventory(inventory);
        return inventory;
    }

    protected void putItem(Inventory inventory, int slot, Button button, UltraPlayer ultraPlayer) {
        ItemStack itemStack = button.getDisplayItem(ultraPlayer);
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }

        inventory.setItem(slot, itemStack);
        Map<ItemStack, Button> map = clickRunnableMap.computeIfAbsent(inventory, f -> new HashMap<>());
        map.put(itemStack, button);
    }

    protected void fillInventory(Inventory inventory) {
        if (!fillEmpty) return;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, fillerItem);
            }
        }
    }

    private ItemStack getFillerItem() {
        ItemStack itemStack = ItemFactory.getItemStackFromConfig("Fill-Blank-Slots-With-Item.Item");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        // Check that Inventory is valid.
        if (!clickRunnableMap.containsKey(event.getInventory())) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
            if(Objects.requireNonNull(event.getPlayer().getOpenInventory().getItem(0)).getType() == Material.GRAY_STAINED_GLASS_PANE) {
                return;
            }

            UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer((Player) event.getPlayer()).setInShop(false);
        },20L);


    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Check that Inventory is valid.
        if (!clickRunnableMap.containsKey(event.getInventory())) {
            return;
        }

        // Cancel the event no matter what once we've verified that
        // the player is clicking with our inventory open.
        event.setCancelled(true);

        // Check that the filler item isn't being clicked
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName() || event.getCurrentItem().equals(fillerItem)) {
            return;
        }

        Button button = null;
        String clickItemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Set<Entry<ItemStack, Button>> entries = clickRunnableMap.get(event.getInventory()).entrySet();
        for (Entry<ItemStack, Button> entry : entries) {
            if (entry.getKey().getItemMeta().getDisplayName().equals(clickItemName)) {
                button = entry.getValue();
                break;
            }
        }
        if (button == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        button.onClick(new ClickData(this, ultraPlayer, event.getClick(), event.getCurrentItem(), event.getSlot()));
        player.updateInventory();
    }

    public UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    protected abstract void putItems(Inventory inventory, UltraPlayer player);

    protected abstract int getSize();

    protected abstract Component getName();
}
