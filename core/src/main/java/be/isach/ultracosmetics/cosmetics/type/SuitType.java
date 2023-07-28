package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Suit types.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SuitType extends CosmeticType<Suit> {

    private final ArmorSlot slot;
    private final SuitCategory category;

    /**
     * @param material The suit part material
     * @param slot     The slot this suit part should occupy
     * @param category The Suit category this part belongs to
     */
    protected SuitType(XMaterial material, ArmorSlot slot, SuitCategory category,boolean special) {
        super(Category.suitsFromSlot(slot), category.getConfigName(), material, category.getSuitClass(), special);
        this.slot = slot;
        this.category = category;
        // delay permission registration until we've loaded slot and category fields
        registerPermission();
        category.setupConfig(SettingsManager.getConfig(), getConfigPath());
    }

    @Override
    public Component getName() {
        return MessageManager.getMessage("Suits." + getConfigName() + "." + slot.toString().toLowerCase() + "-name");
    }

    @Override
    protected String getPermissionSuffix() {
        return category.getPermissionSuffix() + "." + slot.toString().toLowerCase();
    }

    public ArmorSlot getSlot() {
        return slot;
    }

    public SuitCategory getSuitCategory() {
        return category;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack is = super.getItemStack();
        Color color = category.getColor(slot);
        if (color != null) {
            LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
            meta.setColor(color);
            is.setItemMeta(meta);
        }
        return is;
    }
}
