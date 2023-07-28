package be.isach.ultracosmetics.command;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(sender instanceof Player) {
            UltraPlayer ultraPlayer = UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer((Player) sender);
            ultraPlayer.setInShop(true);
            UltraCosmeticsData.get().getPlugin().getMenus().openMainMenu(ultraPlayer);
        }


        return true;
    }
}
