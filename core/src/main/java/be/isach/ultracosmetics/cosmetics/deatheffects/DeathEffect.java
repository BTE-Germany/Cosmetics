package be.isach.ultracosmetics.cosmetics.deatheffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.DeathEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public abstract class DeathEffect extends Cosmetic<DeathEffectType> {

    public DeathEffect(UltraPlayer owner, DeathEffectType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics,false);
    }

    @Override
    protected void onEquip() {
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity() == getPlayer()) {
            displayParticles(event.getEntity());
        }
    }

    public void displayParticles(Player player) {
        getType().getEffect().display(player.getLocation());
    }
}
