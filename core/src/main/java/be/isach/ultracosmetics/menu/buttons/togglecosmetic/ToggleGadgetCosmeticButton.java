package be.isach.ultracosmetics.menu.buttons.togglecosmetic;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.menu.ClickData;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;

public class ToggleGadgetCosmeticButton extends ToggleCosmeticButton {
    public ToggleGadgetCosmeticButton(UltraCosmetics ultraCosmetics, GadgetType cosmeticType) {
        super(ultraCosmetics, cosmeticType);
    }

    @Override
    protected void handleRightClick(ClickData clickData) {
        UltraPlayer ultraPlayer = clickData.getClicker();
        if (UltraCosmeticsData.get().isAmmoPurchaseEnabled() && ((GadgetType) cosmeticType).requiresAmmo()) {
            ultraCosmetics.getMenus().openAmmoPurchaseMenu((GadgetType) cosmeticType, ultraPlayer, () -> clickData.getMenu().refresh(ultraPlayer));
        }
    }

    @Override
    protected boolean handleActivate(ClickData clickData) {
        UltraPlayer ultraPlayer = clickData.getClicker();
        GadgetType gadgetType = ultraPlayer.getCurrentGadget().getType();
       /* if (ultraCosmetics.getEconomyHandler().isUsingEconomy() && UltraCosmeticsData.get().isAmmoPurchaseEnabled()
                && gadgetType.requiresAmmo() && ultraPlayer.getAmmo(gadgetType) < 1) {
            ultraCosmetics.getMenus().openAmmoPurchaseMenu(gadgetType, ultraPlayer, () -> clickData.getMenu().refresh(ultraPlayer));
            return false;
        }*/
        return super.handleActivate(clickData);
    }

    @Override
    protected void modifyLore(List<String> lore, UltraPlayer ultraPlayer) {
        GadgetType gadgetType = (GadgetType) cosmeticType;
        if (!UltraCosmeticsData.get().isAmmoEnabled() || !gadgetType.requiresAmmo() || !ultraPlayer.canEquip(gadgetType)) {
            return;
        }
        lore.add("");
        int ammo = ultraPlayer.getAmmo(gadgetType);
        lore.add(MessageManager.getLegacyMessage("Ammo", Placeholder.unparsed("ammo", String.valueOf(ammo))));

    }

    @Override
    protected int getAmount(UltraPlayer ultraPlayer) {
        boolean currentGadgetIsThis = ultraPlayer.getCurrentGadget() != null && ultraPlayer.getCurrentGadget().getType() == cosmeticType;
        if (SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Show-Ammo-In-Menu-As-Item-Amount") && !currentGadgetIsThis) {
            return Math.max(1, Math.min(64, ultraPlayer.getAmmo((GadgetType) cosmeticType)));
        }
        return super.getAmount(ultraPlayer);
    }
}
