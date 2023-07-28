package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.menu.buttons.ClearCosmeticButton;
import be.isach.ultracosmetics.menu.buttons.KeysButton;
import be.isach.ultracosmetics.menu.buttons.OpenChestButton;
import be.isach.ultracosmetics.menu.buttons.OpenCosmeticMenuButton;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;

/**
 * Main {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuMain extends Menu {
    private final Component title = MessageManager.getMessage("Menu.Main.Title");
    private int[] layout;

    public MenuMain(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics);

        switch (Category.enabledSize()) {
            case 10:
                layout = new int[] {9, 11, 13, 15, 17, 27, 29, 31, 33, 35};
                break;
            case 9:
                layout = new int[] {9, 11, 13, 15, 17, 28, 30, 32, 34};
                break;
            case 8:
                layout = new int[] {10, 12, 14, 16, 28, 30, 32, 34};
                break;
            case 7:
                layout = new int[] {10, 13, 16, 28, 30, 32, 34};
                break;
            case 6:
                layout = new int[] {10, 13, 16, 28, 31, 34};
                break;
            case 5:
                layout = new int[] {10, 16, 22, 29, 33};
                break;
            case 4:
                layout = new int[] {19, 21, 23, 25};
                break;
            case 3:
                layout = new int[] {20, 22, 24};
                break;
            case 2:
                layout = new int[] {21, 23};
                break;
            case 1:
                layout = new int[] {22};
                break;
        }

        if (UltraCosmeticsData.get().areTreasureChestsEnabled() && layout != null) {
            for (int i = 0; i < layout.length; i++) {
                layout[i] += 9;
            }
        }
    }

    @Override
    public void open(UltraPlayer player) {
        if (!UltraCosmeticsData.get().areTreasureChestsEnabled() && Category.enabledSize() == 1) {
            getUltraCosmetics().getMenus().getCategoryMenu(Category.enabled().get(0)).open(player);
            return;
        }
        super.open(player);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player) {
        if (Category.enabledSize() > 0) {
            int i = 0;
            boolean foundSuits = false;
            for (Category category : Category.enabled()) {
                // Avoid counting suits categories as different menu items
                if (category.isSuits()) {
                    if (foundSuits) continue;
                    foundSuits = true;
                }
                putItem(inventory, layout[i++], new OpenCosmeticMenuButton(getUltraCosmetics(), category), player);
            }
        }
        putItem(inventory, inventory.getSize() - 5, new ClearCosmeticButton(), player);
        if (UltraCosmeticsData.get().areTreasureChestsEnabled()) {
            putItem(inventory, 5, new KeysButton(ultraCosmetics), player);
            putItem(inventory, 3, new OpenChestButton(ultraCosmetics), player);
        }
    }

    @Override
    protected Component getName() {
        return title;
    }
    @Override
    protected Component getName(UltraPlayer player) {
        if(player.isInShop()) return MessageManager.getMiniMessage().deserialize("<bold>Shop - ").append(getName());

        return getName();
    }
    @Override
    protected int getSize() {
        return UltraCosmeticsData.get().areTreasureChestsEnabled() ? 54 : 45;
    }

}
