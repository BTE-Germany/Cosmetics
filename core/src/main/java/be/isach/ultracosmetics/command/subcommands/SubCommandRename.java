package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.menu.buttons.RenamePetButton;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class SubCommandRename extends SubCommand {

    public SubCommandRename(UltraCosmetics ultraCosmetics) {
        super("rename", "Renames/clears the name of active pet", "[new name]", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        notAllowed(sender);
    }

    @Override
    protected void onExePlayer(Player player, String[] args) {
        if (!SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            error(player, "Pet renaming is disabled.");
            return;
        }
        UltraPlayer up = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        if (up.getCurrentPet() == null) {
            error(player, "Please equip a pet to rename it.");
            return;
        }
        String newName;
        if (args.length < 2) {
            newName = "";
        } else {
            StringJoiner sj = new StringJoiner(" ");
            for (int i = 1; i < args.length; i++) {
                sj.add(args[i]);
            }
            newName = sj.toString();
        }

        if (!newName.isEmpty()) {
            player.openInventory(RenamePetButton.buyRenamePet(up, newName, null));
        } else {
            up.setPetName(up.getCurrentPet().getType(), newName);
            player.sendMessage(ChatColor.GREEN + "Success!");
        }
    }

}
