package be.isach.ultracosmetics.menu.buttons;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.SuitCategory;
import be.isach.ultracosmetics.menu.Button;
import be.isach.ultracosmetics.menu.ClickData;
import be.isach.ultracosmetics.menu.Menus;
import be.isach.ultracosmetics.permissions.PermissionManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.LazyTag;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenSpecialsMenuButton implements Button {
    private final PermissionManager pm;
    private final Menus menus;
    private final ItemStack baseStack;

    public OpenSpecialsMenuButton(UltraCosmetics ultraCosmetics) {
        this.baseStack = ItemFactory.create(XMaterial.DIAMOND,"Special");
        this.pm = ultraCosmetics.getPermissionManager();
        this.menus = ultraCosmetics.getMenus();
    }

    @Override
    public ItemStack getDisplayItem(UltraPlayer ultraPlayer) {
        ItemStack stack = baseStack.clone();
        String lore = MessageManager.getLegacyMessage("Menu.Special.Button.Lore"
        );
        List<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.addAll(Arrays.asList(lore.split("\n")));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(MessageManager.getLegacyMessage("Menu.Special.Button.Name"));
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void onClick(ClickData clickData) {
        menus.getSpecialsMenu().open(clickData.getClicker());
    }
}
