package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents an instance of a particle effect summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class ParticleEffect extends Cosmetic<ParticleEffectType> implements Updatable {

    /**
     * If false, no particle effects will appear when the player is standing.
     */
    protected boolean displayIfStanding = true;
    /**
     * If false, no particle effects will appear when the player is moving.
     */
    protected boolean displayIfMoving = true;
    /**
     * If true, display an alternative effect when the player is moving.
     */
    protected boolean alternativeEffect = false;

    protected Location lastLocation = null;
    protected boolean moving = true;
    protected boolean update = true;

    public ParticleEffect(UltraPlayer ultraPlayer, ParticleEffectType type, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, type, ultraCosmetics,false);
    }

    @Override
    protected void onEquip() {
    }

    @Override
    protected void scheduleTask() {
        runTaskTimerAsynchronously(getUltraCosmetics(), 0, getType().getRepeatDelay());
    }

    protected boolean locEquals(Location a, Location b) {
        if (a == null || b == null) return false;
        // Manual comparison so we don't take into account pitch and yaw
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    @Override
    public void run() {
        Player player = getPlayer();
        if (player == null || getOwner().getCurrentParticleEffect() != this) {
            cancel();
            return;
        }

        // Checking whether the player has moved every tick results in false negatives
        // (meaning the player will be marked as not moving when they actually are.)
        if (update) {
            moving = !locEquals(lastLocation, player.getLocation());
            lastLocation = player.getLocation();
            if (getType().getRepeatDelay() == 1) {
                update = false;
            }
        } else {
            update = true;
        }

        // If the player is not moving, display the default particle effect.
        if (!isMoving()) {
            if (displayIfStanding) {
                onUpdate();
            }
            return;
        }
        // If the player is moving:
        if (!displayIfMoving) return;

        if (alternativeEffect) {
            // Display the alternative effect.
            showAlternativeEffect();
        } else {
            // Display default particle effect.
            onUpdate();
        }
    }

    protected boolean isMoving() {
        return moving;
    }

    protected int getModifiedAmount(int originalAmount) {
        // always return at least 1 so the particles work
        return Integer.max((int) (originalAmount * getType().getParticleMultiplier()), 1);
    }

    public void showAlternativeEffect() {
        getType().getEffect().display(0.2, 0.2, 0.2, getPlayer().getLocation().add(0, 0.2, 0), getModifiedAmount(3));
    }

    protected void showColoredAlternativeEffect(int r, int g, int b) {
        for (int i = 0; i < getModifiedAmount(3); i++) {
            Particles.REDSTONE.display(r, g, b, getPlayer().getLocation().add(MathUtils.randomDouble(-0.2, 0.2), MathUtils.randomDouble(0, 0.4), MathUtils.randomDouble(-0.2, 0.2)));
        }
    }
}
