package be.isach.ultracosmetics.menu.buttons;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitCategory;
import be.isach.ultracosmetics.menu.Button;
import be.isach.ultracosmetics.menu.ClickData;
import be.isach.ultracosmetics.menu.Menus;
import be.isach.ultracosmetics.permissions.PermissionManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.LazyTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OpenCosmeticMenuButton implements Button {
    private final Category category;
    private final PermissionManager pm;
    private final Menus menus;
    private final ItemStack baseStack;

    public OpenCosmeticMenuButton(UltraCosmetics ultraCosmetics, Category category) {
        this.category = category;
        this.baseStack = category.getItemStack();
        this.pm = ultraCosmetics.getPermissionManager();
        this.menus = ultraCosmetics.getMenus();
    }

    @Override
    public ItemStack getDisplayItem(UltraPlayer ultraPlayer) {
        ItemStack stack = baseStack.clone();
        String lore = MessageManager.getLegacyMessage("Menu." + category.getConfigPath() + ".Button.Lore",
                TagResolver.resolver("unlocked", new LazyTag(() -> Component.text(calculateUnlocked(ultraPlayer.getBukkitPlayer()))))
        );
        List<String> loreList = new ArrayList<>();
        loreList.add("");
        loreList.addAll(Arrays.asList(lore.split("\n")));
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        return stack;
    }

    private String calculateUnlocked(Player player) {
        Set<CosmeticType<?>> unlocked = new HashSet<>();
        int total;
        if (category.isSuits()) {
            for (Category cat : Category.enabled()) {
                if (!cat.isSuits()) continue;
                unlocked.addAll(pm.getEnabledUnlocked(player, cat));
            }
            total = SuitCategory.enabled().size() * 4;
        } else {
            unlocked = pm.getEnabledUnlocked(player, category);
            total = category.getEnabled().size();
            System.out.println(category+" "+unlocked+" "+total);
        }
        return unlocked.size() + "/" + total;
    }

    @Override
    public void onClick(ClickData clickData) {
        menus.getCategoryMenu(category).open(clickData.getClicker());
    }
}
