package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Button;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.menu.buttons.*;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.SmartLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;

public class CustomMainMenu extends Menu {
    private final File customMenuFile;
    private final YamlConfiguration config = new YamlConfiguration();
    private final Component title = MessageManager.getMessage("Menu.Main.Title");

    public CustomMainMenu(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics);
        customMenuFile = getFile(ultraCosmetics);
        try {
            config.load(customMenuFile);
        } catch (InvalidConfigurationException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEnabled() {
        return config.getBoolean("Enabled");
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player) {
        ConfigurationSection slots = config.getConfigurationSection("Slots");
        int slot;
        for (String key : slots.getKeys(false)) {
            try {
                slot = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                ultraCosmetics.getSmartLogger().write(SmartLogger.LogLevel.WARNING, "Non-numeric key in main menu config: '" + key + "'");
                continue;
            }
            if (slot < 0 || slot >= getSize()) {
                ultraCosmetics.getSmartLogger().write(SmartLogger.LogLevel.WARNING, "Slot index out of range 0-" + getSize() + ": " + slot);
                continue;
            }
            Button button = null;
            ConfigurationSection section = slots.getConfigurationSection(key);
            String typeName = section.getString("Type", "");
            // Item, ClearCosmetic, Keys, OpenChest, OpenCosmeticMenu, RenamePet, ToggleGadgets, ToggleMorphSelfView
            switch (typeName.toLowerCase()) {
                case "command":
                    try {
                        button = CommandButton.deserialize(section, ultraCosmetics);
                    } catch (IllegalArgumentException e) {
                        ultraCosmetics.getSmartLogger().write(SmartLogger.LogLevel.WARNING, e.getMessage());
                        continue;
                    }
                    break;
                case "clearcosmetic":
                    button = new ClearCosmeticButton();
                    break;
                case "keys":
                    button = new KeysButton(ultraCosmetics);
                    break;
                case "openchest":
                    button = new OpenChestButton(ultraCosmetics);
                    break;
                case "opencosmeticmenu":
                    String targetName = slots.getString(key + ".Menu", "");
                    Category target;
                    if (targetName.equalsIgnoreCase("suits")) {
                        target = Category.SUITS_HELMET;
                    } else if (targetName.equalsIgnoreCase("special")) {
                        button = new OpenSpecialsMenuButton(ultraCosmetics);
                        target = Category.PETS;
                    } else {
                        target = Category.fromString(targetName);
                    }
                    if (target == null) {
                        ultraCosmetics.getSmartLogger().write(SmartLogger.LogLevel.WARNING, "Unknown target menu: " + targetName);
                        continue;
                    }
                    if(button == null) button = new OpenCosmeticMenuButton(ultraCosmetics, target);
                    break;
                case "renamepet":
                    button = new RenamePetButton(ultraCosmetics);
                    break;
                case "togglegadgets":
                    button = new ToggleGadgetsButton();
                    break;
                case "togglemorphselfview":
                    button = new ToggleMorphSelfViewButton();
                    break;
                default:
                    ultraCosmetics.getSmartLogger().write(SmartLogger.LogLevel.WARNING, "Unknown button type: '" + typeName + "'");
                    continue;
            }
            putItem(inventory, slot, button, player);
        }
    }

    @Override
    protected int getSize() {
        return config.getInt("Size", 54);
    }

    @Override
    protected Component getName() {
        return title;
    }
    @Override
    protected Component getName(UltraPlayer player) {
        if(player.isInShop()) return MessageManager.getMiniMessage().deserialize("<bold>Shop - ").append(getName());

        return getName();
    }

    public static File getFile(UltraCosmetics ultraCosmetics) {
        return new File(ultraCosmetics.getDataFolder(), "custom_main_menu.yml");
    }
}
