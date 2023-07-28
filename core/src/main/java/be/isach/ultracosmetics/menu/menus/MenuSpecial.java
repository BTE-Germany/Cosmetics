package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticEntType;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.menu.buttons.*;
import be.isach.ultracosmetics.permissions.PermissionManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Difficulty;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Pet {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuSpecial extends Menu {

    public static final int[] COSMETICS_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    protected final PermissionManager pm = ultraCosmetics.getPermissionManager();
    private final Map<UUID, Integer> lastUsedPages = new HashMap<>();
    private final boolean hideNoPermissionItems = SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item");

    public MenuSpecial(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics);
    }

    @Override
    public void open(UltraPlayer player) {
        open(player, 1);
    }

    public void open(UltraPlayer player, int page) {
        final int maxPages = getMaxPages(player);
        if (page > maxPages) {
            page = maxPages;
        }
        if (page < 1) {
            page = 1;
        }
        lastUsedPages.put(player.getUUID(), page);

        Inventory inventory = createInventory(maxPages == 1 ? getName() : getName(page, player));
        boolean hasUnlockable = hasUnlockable(player);

        // Cosmetic types.
            Map<Integer, CosmeticType<?>> slots = getSlots(page, player);
            for (Map.Entry<Integer, CosmeticType<?>> entry : slots.entrySet()) {
                int slot = entry.getKey();
                CosmeticType<?> cosmeticType = entry.getValue();

                if (!shouldHideItem(player, cosmeticType)) continue;
                CosmeticButton button = CosmeticButton.fromType(cosmeticType, player, ultraCosmetics);
                putItem(inventory, slot, button, player);
            }


        if (page > 1) {
            putItem(inventory, getSize() - 18, new PreviousPageButton(), player);
        }
        if (page < maxPages) {
            putItem(inventory, getSize() - 10, new NextPageButton(), player);
        }

        putItem(inventory, inventory.getSize() - 6, new MainMenuButton(ultraCosmetics), player);

        if (hasUnlockable && !hideNoPermissionItems) {
            putItem(inventory, inventory.getSize() - 3, new FilterCosmeticsButton(), player);
        }

        putItems(inventory, player, page);
        fillInventory(inventory);
        player.getBukkitPlayer().openInventory(inventory);
    }

    @Override
    public void refresh(UltraPlayer player) {
        open(player, getCurrentPage(player));
    }

    /**
     * @param ultraPlayer The menu owner
     * @return The current page of the menu opened by ultraPlayer
     */
    public int getCurrentPage(UltraPlayer ultraPlayer) {
        return lastUsedPages.getOrDefault(ultraPlayer.getUUID(), 1);
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    protected int getMaxPages(UltraPlayer player) {
        int i = 0;
        for(Category cat: Category.values()) {
            for (CosmeticType<?> type : CosmeticType.enabledOf(cat)) {
                if (shouldHideItem(player,type)) {
                    i++;
                }
            }
        }

        return Math.max(1, ((i - 1) / 28) + 1);
    }

    protected int getItemsPerPage() {
        return 28;
    }

    /**
     * @param page The page to open.
     * @return The name of the menu with page detailed.
     */
    protected Component getName(int page, UltraPlayer ultraPlayer) {
        return Component.empty().append(getName()).appendSpace()
                .append(Component.text("(" + page + "/" + getMaxPages(ultraPlayer) + ")", NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    @Override
    protected int getSize() {
        // I think for consistency it's better to have all menus max size
        return 54;
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer) {
        // --
    }

    /**
     * @return The name of the menu.
     */
    @Override
    protected Component getName(UltraPlayer player) {
        if(player.isInShop()) return MessageManager.getMiniMessage().deserialize("<bold>Shop - ").append(getName());

        return getName();
    }

    @Override
    protected Component getName() {
        return MessageManager.getMessage("Menu.Special.Title");
    }

    /**
     * Puts items in the inventory.
     *
     * @param inventory   Inventory.
     * @param ultraPlayer Inventory Owner.
     * @param page        Page to open.
     */
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
    }

    @SuppressWarnings("unchecked")
    protected Map<Integer,CosmeticType<?>> getSlots(int page, UltraPlayer player) {
        int start = 28 * (page - 1);
        int limit = 28;
        int current = 0;
        Map<Integer,CosmeticType<?>> slots = new HashMap<>();
        ArrayList<CosmeticType<?>> enabled = new ArrayList<>();

        for(Category cat: Category.values()) {
            enabled.addAll(CosmeticType.valuesOf(cat));
        }
        enabled.removeIf(k -> !shouldHideItem(player, k));

        for (int i = start; current < limit && i < enabled.size(); i++) {
            slots.put(COSMETICS_SLOTS[current++ % 28], enabled.get(i));
        }
        return slots;
    }


    protected boolean shouldHideItem(UltraPlayer player, CosmeticType<?> cosmeticType) {
      return cosmeticType.isSpecial() && player.canEquip(cosmeticType);
    }

    protected boolean hasUnlockable(UltraPlayer player) {
        if (ultraCosmetics.getWorldGuardManager().isInShowroom(player.getBukkitPlayer())) return false;
        for(Category cat: Category.values()) {
        for (CosmeticType<?> type : CosmeticType.enabledOf(cat)) {
            if (!player.canEquip(type)) {
                return true;
            }
        }}
        return false;
    }
}
