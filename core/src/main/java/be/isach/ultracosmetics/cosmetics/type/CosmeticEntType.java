package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;

import com.cryptomorin.xseries.XMaterial;

/**
 * Represents a Cosmetic Type with an entity type.
 *
 * @author iSach
 * @since 08-04-2016
 */
public abstract class CosmeticEntType<T extends Cosmetic<?>> extends CosmeticType<T> {

    private final EntityType entityType;

    public CosmeticEntType(Category category, String configName, XMaterial material, EntityType entityType, Class<? extends T> clazz,boolean special) {
        super(category, configName, material, clazz,true,special);
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public boolean isMonster() {
        // If the entity is a monster and the world is set to peaceful, we can't spawn it
        return Monster.class.isAssignableFrom(entityType.getEntityClass())
                // no idea why Slime doesn't implement Monster but we have to check for it
                || Slime.class.isAssignableFrom(entityType.getEntityClass());
    }
}
