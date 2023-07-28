package be.isach.ultracosmetics.menu.buttons;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.menu.ClickData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.treasurechests.TreasureChestType;
import be.isach.ultracosmetics.treasurechests.TreasureRandomizer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OpenChestButton extends TreasureButton {
    private final Component itemName = MessageManager.getMessage("Treasure-Chests");
    private final int chestCount = SettingsManager.getConfig().getInt("TreasureChests.Count", 4);
    private final String[] noKeysLore = new String[] {"", MessageManager.getLegacyMessage("Dont-Have-Key"), buyKeyMessage};
    private final String[] hasKeysLore;

    public OpenChestButton(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics);
        hasKeysLore = new String[] {"", MessageManager.getLegacyMessage("Click-Open-Chest"), ""};
    }

    @Override
    public ItemStack getDisplayItem(UltraPlayer ultraPlayer) {
        String[] lore = ultraPlayer.getKeys() < 1 ? noKeysLore : hasKeysLore;
        return ItemFactory.create(XMaterial.CHEST, itemName, lore);
    }

    @Override
    public void onClick(ClickData clickData) {
        UltraPlayer player = clickData.getClicker();
        Player p = player.getBukkitPlayer();
        if (!canBuyKeys && player.getKeys() < 1) {
            XSound.BLOCK_ANVIL_LAND.play(p.getLocation(), 0.2f, 1.2f);
            return;
        }
        if (player.getKeys() > 0) {
            if (player.getCurrentTreasureChest() != null) {
                return;
            }
            if (player.getBukkitPlayer().getVehicle() != null) {
                MessageManager.send(player.getBukkitPlayer(), "Chest-Location.Dismount-First");
                return;
            }
            if(player.getBukkitPlayer().getLocation().clone().add(0,0,3).getBlock().getType()!= Material.AIR) {
                MessageManager.send(player.getBukkitPlayer(), "Chest-Location.Not-Enough-Space");
                return;
            }
            player.getBukkitPlayer().closeInventory();
            player.removeKey();
            new TreasureChest(player.getUUID(),player.getBukkitPlayer().hasPermission("tab.donator")? TreasureChestType.PREMIUM:TreasureChestType.DEFAULT);
        }
    }
}
