package be.isach.ultracosmetics.cosmetics.emotes;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.ArmorCosmetic;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an instance of an emote summoned by a player.
 *
 * @author iSach
 * @since 06-17-2016
 */
public class Emote extends ArmorCosmetic<EmoteType> {

    private final EmoteAnimation animation;

    public Emote(UltraPlayer owner, EmoteType emoteType, UltraCosmetics ultraCosmetics) {
        super(owner, emoteType, ultraCosmetics,false);

        this.animation = new EmoteAnimation(getType().getTicksPerFrame(), this);
    }

    @Override
    protected void onEquip() {
        animation.start();
    }

    @Override
    protected void onClear() {
        animation.stop();
    }

    protected void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        writeAttributes(this.itemStack);
        setArmorItem(this.itemStack);
    }

    @Override
    protected ArmorSlot getArmorSlot() {
        return ArmorSlot.HELMET;
    }
}
