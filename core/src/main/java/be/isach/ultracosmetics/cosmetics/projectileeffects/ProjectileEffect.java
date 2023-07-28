package be.isach.ultracosmetics.cosmetics.projectileeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.ProjectileEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class ProjectileEffect extends Cosmetic<ProjectileEffectType> implements Updatable {
    private final Map<Projectile,Location> projectiles = new HashMap<>();
    private Set<EntityType> types = new HashSet<>();

    public ProjectileEffect(UltraPlayer owner, ProjectileEffectType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics,false);
        for (String entity : SettingsManager.getConfig().getStringList("Projectile-Types")) {
            try {
                types.add(EntityType.valueOf(entity.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    protected void onEquip() {

    }

    @Override
    protected void scheduleTask() {
        runTaskTimerAsynchronously(getUltraCosmetics(), 0, getType().getRepeatDelay());
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != getPlayer()) return;
        if (!types.contains(event.getEntityType())) return;
        projectiles.put(event.getEntity(), null);
    }

    @Override
    public void onUpdate() {
        Iterator<Entry<Projectile,Location>> iter = projectiles.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Projectile,Location> entry = iter.next();
            Projectile proj = entry.getKey();
            // Check if projectile location is the same as the last observed location.
            // If so, the projectile is probably stuck in a block or something.
            // There's no 1.8.8 API for checking, so we have to do this.
            if (!proj.isValid() || proj.getLocation().equals(entry.getValue())) {
                iter.remove();
                continue;
            }
            showParticles(proj);
        }
        // Update all remaining projectiles with their new locations
        projectiles.replaceAll((p, l) -> p.getLocation());
    }

    @Override
    public void onClear() {
        projectiles.keySet().forEach(this::projectileLanded);
        projectiles.clear();
    }

    public void showParticles(Projectile projectile) {
        getType().getEffect().display(projectile.getLocation());
    }

    public void projectileLanded(Projectile projectile) {
    }
}
