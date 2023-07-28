package be.isach.ultracosmetics.menu.buttons;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.menu.Button;
import be.isach.ultracosmetics.menu.ClickData;
import be.isach.ultracosmetics.menu.PurchaseData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;

public class PurchaseConfirmButton implements Button {
    private final PurchaseData purchaseData;

    public PurchaseConfirmButton(PurchaseData purchaseData) {
        this.purchaseData = purchaseData;
    }

    @Override
    public ItemStack getDisplayItem(UltraPlayer ultraPlayer) {
        return ItemFactory.create(XMaterial.EMERALD_BLOCK, MessageManager.getMessage("Purchase"));
    }

    @Override
    public void onClick(ClickData clickData) {
        UltraPlayer player = clickData.getClicker();
        player.getBukkitPlayer().closeInventory();
        if (!purchaseData.canPurchase()) return;
        int balance = UltraCosmeticsData.get().getPlugin().getMySqlConnectionManager().getPlayerData().getCoins(player.getUUID());
        if(balance < purchaseData.getPrice()) {
            player.sendMessage(MessageManager.getMessage("Not-Enough-Money"));
        } else {
            UltraCosmeticsData.get().getPlugin().getMySqlConnectionManager().getPlayerData().setCoins(player.getUUID(),balance-purchaseData.getPrice());
            player.sendMessage(MessageManager.getMessage("Successful-Purchase"));
            purchaseData.runOnPurchase();
        }

    }
}
