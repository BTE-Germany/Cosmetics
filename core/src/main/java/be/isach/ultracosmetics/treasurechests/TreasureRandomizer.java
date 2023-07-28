package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.treasurechests.loot.LootReward;
import be.isach.ultracosmetics.treasurechests.loot.MoneyLoot;
import be.isach.ultracosmetics.util.EntitySpawner;
import com.cryptomorin.xseries.XSound;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sacha on 19/08/15.
 */
public class TreasureRandomizer {
    private Location loc;
    private final Player player;
    private final boolean forceMessageToOwner;
    private final MoneyLoot money = new MoneyLoot();

    public TreasureRandomizer(final Player player, Location location, boolean forceMessageToOwner) {
        this.loc = location.add(0.5, 0, 0.5);
        this.player = player;
        this.forceMessageToOwner = forceMessageToOwner;

    }

    public TreasureRandomizer(final Player player, Location location) {
        this(player, location, false);
    }

    private static Color randomColor() {
        Random r = ThreadLocalRandom.current();
        return Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public LootReward giveRandomThing(TreasureChest chest) {
        UltraPlayer ultraPlayer = UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player);
        XSound.BLOCK_CHEST_OPEN.play(loc, 1.4f, 1.5f);

        LootReward reward = money.giveToPlayer(ultraPlayer, chest);
        broadcast(reward.getMessage(), reward.isBroadcast());
        if (reward.isFirework()) {
            spawnFirework();
        }

        return reward;
    }

    public void spawnFirework() {
        EntitySpawner.spawnFireworks(loc.clone().add(0.5, 0, 0.5), randomColor(), randomColor(), FireworkEffect.Type.BALL);
    }

    private void broadcast(Component message, boolean toOthers) {
        if (message == null) return;
        UltraCosmetics ultraCosmetics = UltraCosmeticsData.get().getPlugin();
        if (ultraCosmetics.getDiscordHook() != null) {
            ultraCosmetics.getDiscordHook().sendLootMessage(player, MessageManager.toLegacy(message));
        }
        BukkitAudiences audiences = MessageManager.getAudiences();
        if (!toOthers) {
            if (forceMessageToOwner) {
                audiences.player(player).sendMessage(message);
            }
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == this.player || (SettingsManager.isAllowedWorld(player.getWorld())
                    && ultraCosmetics.getPlayerManager().getUltraPlayer(player).isTreasureNotifying())) {
                audiences.player(player).sendMessage(message);
            }
        }
    }

    public void setLocation(Location newLoc) {
        loc = newLoc;
    }
}
