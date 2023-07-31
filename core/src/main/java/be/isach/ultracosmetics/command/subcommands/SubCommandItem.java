package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandItem extends SubCommand {

    public SubCommandItem(UltraCosmetics ultraCosmetics) {
        super("item", "Gives the player the inventory item", "<player>", ultraCosmetics, true);
    }

    @Override
    protected void onExePlayer(Player sender, String[] args) {
        if (!SettingsManager.isAllowedWorld(sender.getWorld())) {
            MessageManager.send(sender, "World-Disabled");
            return;
        }

        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(sender);
        if (args.length < 2) {
            ultraPlayer.giveMenuItem();
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            error(sender, "Player " + args[3] + " not found!");
            return;
        }
        ultraCosmetics.getPlayerManager().getUltraPlayer(target).giveMenuItem();
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        notAllowed(sender);
    }
}
