package be.isach.ultracosmetics.permissions;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public interface CosmeticPermissionGetter {
    default boolean hasPermission(Player player, CosmeticType<?> type) {

       return UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).getProfile().hasUnlocked(type);
    }

    default Set<CosmeticType<?>> getEnabledUnlocked(Player player) {
        Set<CosmeticType<?>> types = new HashSet<>();
        for (Category cat : Category.enabled()) {
            types.addAll(getEnabledUnlocked(player, cat));
        }
        return types;
    }

    default Set<CosmeticType<?>> getEnabledUnlocked(Player player, Category cat) {
        Set<CosmeticType<?>> types = new HashSet<>();
        for (CosmeticType<?> type : cat.getEnabled()) {
            if (!type.isSpecial() && hasPermission(player, type)) {
                types.add(type);
            }
        }
        return types;
    }

}
