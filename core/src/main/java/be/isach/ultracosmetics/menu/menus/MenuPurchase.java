package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.menu.PurchaseData;
import be.isach.ultracosmetics.menu.buttons.PurchaseCancelButton;
import be.isach.ultracosmetics.menu.buttons.PurchaseConfirmButton;
import be.isach.ultracosmetics.menu.buttons.PurchaseShowcaseButton;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;

/**
 * Created by sacha on 04/04/2017.
 */
public class MenuPurchase extends Menu {

    private Component name;
    private PurchaseData purchaseData;

    public MenuPurchase(UltraCosmetics ultraCosmetics, Component name, PurchaseData purchaseData) {
        super(ultraCosmetics);
        this.name = name;
        this.purchaseData = purchaseData;
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player) {
        // Showcase Item
        putItem(inventory, 13, new PurchaseShowcaseButton(purchaseData), player);

        // Purchase Item
        PurchaseConfirmButton confirmButton = new PurchaseConfirmButton(purchaseData);
        for (int i = 27; i < 30; i++) {
            for (int j = i; j <= i + 18; j += 9) {
                putItem(inventory, j, confirmButton, player);
            }
        }

        // Cancel Item
        PurchaseCancelButton cancelButton = new PurchaseCancelButton(purchaseData);
        for (int i = 33; i < 36; i++) {
            for (int j = i; j <= i + 18; j += 9) {
                putItem(inventory, j, cancelButton, player);
            }
        }
    }

    @Override
    protected int getSize() {
        return 54;
    }

    @Override
    protected Component getName() {
        return name;
    }
}
