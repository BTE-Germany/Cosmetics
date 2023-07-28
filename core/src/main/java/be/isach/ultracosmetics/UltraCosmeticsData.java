package be.isach.ultracosmetics;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.Problem;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.SmartLogger;
import be.isach.ultracosmetics.util.SmartLogger.LogLevel;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

/**
 * This class is only for cleaning main class a bit.
 *
 * @author iSach
 * @since 08-05-2016
 */
public class UltraCosmeticsData {

    private static UltraCosmeticsData instance;

    /**
     * True -> should execute custom command when going back to main menu.
     */
    private boolean customCommandBackArrow;

    /**
     * Command to execute when going back to Main Menu.
     */
    private String customBackMenuCommand;

    /**
     * Determines if Ammo Use is enabled.
     */
    private boolean ammoEnabled;

    /**
     * Determines if File Storage is enabled.
     */
    private boolean fileStorage = true;

    /**
     * Determines if Treasure Chests are enabled.
     */
    private boolean treasureChests;

    /**
     * Determines if Treasure Chest Money Loot enabled.
     */
    private boolean moneyTreasureLoot;

    /**
     * Determines if Gadget Cooldown should be shown in action bar.
     */
    private boolean cooldownInBar;

    /**
     * Should the GUI close after Cosmetic Selection?
     */
    private boolean closeAfterSelect;

    /**
     * If true, the color will be removed in placeholders.
     */
    private boolean placeHolderColor;

    /**
     * Language set in config.yml
     */
    private String language;

    /**
     * If false, players will not be able to purchase ammo
     */
    private boolean ammoPurchase;

    /**
     * Whether NMS support should be loaded. Options are "auto", "no", and "force"
     */
    private String useNMS;

    /**
     * Server NMS version.
     */
    private ServerVersion serverVersion;

    private String nmsVersion = "???";

    private int nmsRev = -1;

    /**
     * NMS Version Manager.
     */
    private VersionManager versionManager;

    private UltraCosmetics ultraCosmetics;

    private boolean cosmeticsProfilesEnabled;

    public UltraCosmeticsData(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    public static UltraCosmeticsData get() {
        return instance;
    }

    public static void init(UltraCosmetics ultraCosmetics) {
        instance = new UltraCosmeticsData(ultraCosmetics);
    }

    /**
     * Check Treasure Chests requirements.
     */
    protected void checkTreasureChests() {
        moneyTreasureLoot = true;
        treasureChests = SettingsManager.getConfig().getBoolean("TreasureChests.Enabled");
    }

    protected boolean initModule() {
        SmartLogger logger = ultraCosmetics.getSmartLogger();
        logger.write("Initializing module " + serverVersion + " (expected version: " + serverVersion.getName() + ")");
        if (useNMS.equalsIgnoreCase("no")) {
            logger.write("NMS support has been disabled in the config, will run without it.");
        } else if (serverVersion.isNmsSupported() && serverVersion.getNMSRevision() == nmsRev) {
            if (startNMS()) return true;
        } else {
            logger.write("Loading NMS-less mode...");
        }

        try {
            versionManager = new VersionManager(serverVersion, false);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            logger.write(LogLevel.ERROR, "Failed to start NMS-less module, please report this error.");
            return false;
        }
        logger.write("Loaded NMS-less mode");
        return true;
    }

    /*
     * Returns false if UC should use NMS-less mode, or true if no further setup is needed.
     */
    protected boolean startNMS() {
        SmartLogger logger = ultraCosmetics.getSmartLogger();
        if (!checkMappingsVersion(serverVersion)) {
            ultraCosmetics.addProblem(Problem.BAD_MAPPINGS_VERSION);
            if (useNMS.equalsIgnoreCase("force")) {
                logger.write(LogLevel.WARNING, "Server internals seem to have changed since this build was created,");
                logger.write(LogLevel.WARNING, "but you have chosen to override version checking!");
                logger.write(LogLevel.WARNING, "Please check for a server update and an UltraCosmetics update.");
                logger.write(LogLevel.WARNING, "UltraCosmetics will continue running but you will likely experience issues!");
            } else {
                logger.write(LogLevel.WARNING, "Server internals have changed since this build was created, so");
                logger.write(LogLevel.WARNING, "NMS support will be disabled. If you're sure you know what you're doing,");
                logger.write(LogLevel.WARNING, "you can override this in the config.");
                return false;
            }
        }

        try {
            versionManager = new VersionManager(serverVersion, true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            logger.write(LogLevel.ERROR, "Couldn't find module for " + serverVersion + ", please report this issue.");
            logger.write("UC will try to continue in NMS-less mode.");
            ultraCosmetics.addProblem(Problem.NMS_LOAD_FAILURE);
            return false;
        }
        if (!versionManager.getModule().enable()) {
            logger.write(LogLevel.ERROR, "Failed to start NMS module, please report this issue.");
            logger.write("UC will try to continue in NMS-less mode.");
            ultraCosmetics.addProblem(Problem.NMS_LOAD_FAILURE);
            return false;
        }
        logger.write("Loaded NMS support");
        return true;
    }

    /**
     * Checks to make sure UC is OK to run on this MC version
     *
     * @return the reason the check failed, or null if it succeeded.
     */
    protected Problem checkServerVersion() {
        int versionNum;
        try {
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            String mcVersion = nmsVersion.substring(nmsVersion.indexOf('_') + 1, nmsVersion.lastIndexOf('_'));
            versionNum = Integer.parseInt(mcVersion);
            nmsRev = Integer.parseInt(nmsVersion.substring(nmsVersion.lastIndexOf('R') + 1));
        } catch (ArrayIndexOutOfBoundsException e) {
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "Unable to determine server version. Please report this error.");
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "Server package: " + Bukkit.getServer().getClass().getPackage().getName());
            return Problem.UNKNOWN_MC_VERSION;
        }

        ServerVersion serverVersion = ServerVersion.byId(versionNum);

        // If we don't know the server version, or if the server version is a
        // newer revision of one we know, use NEW.
        if (serverVersion == null || (serverVersion.getNMSRevision() > 0 && serverVersion.getNMSRevision() < nmsRev)) {
            // Error message printed in onEnable so it's more visible
            this.serverVersion = ServerVersion.NEW;
            return Problem.BAD_MC_VERSION;
        }

        this.serverVersion = serverVersion;

        return null;
    }

    protected boolean checkMappingsVersion(ServerVersion version) {
        if (version.getMappingsVersion() == null) return true;
        String currentMappingsVersion = null;
        @SuppressWarnings("deprecation")
        Object magicNumbers = Bukkit.getUnsafe();
        Class<?> magicNumbersClass = magicNumbers.getClass();
        try {
            Method mappingsVersionMethod = magicNumbersClass.getDeclaredMethod("getMappingsVersion");
            currentMappingsVersion = (String) mappingsVersionMethod.invoke(magicNumbers);
        } catch (ReflectiveOperationException ignored) {
        }
        return currentMappingsVersion.equals(version.getMappingsVersion());
    }

    public void initConfigFields() {
        this.fileStorage = !SettingsManager.getConfig().getBoolean("MySQL.Enabled");
        this.placeHolderColor = SettingsManager.getConfig().getBoolean("Chat-Cosmetic-PlaceHolder-Color");
        this.ammoEnabled = SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Enabled");
        this.cooldownInBar = SettingsManager.getConfig().getBoolean("Categories.Gadgets.Cooldown-In-ActionBar");
        this.customCommandBackArrow = ultraCosmetics.getConfig().getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled");
        this.customBackMenuCommand = ultraCosmetics.getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "");
        this.closeAfterSelect = ultraCosmetics.getConfig().getBoolean("Categories.Close-GUI-After-Select");
        this.cosmeticsProfilesEnabled = ultraCosmetics.getConfig().getBoolean("Auto-Equip-Cosmetics");
        this.language = SettingsManager.getConfig().getString("Language");
        this.ammoPurchase = SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Allow-Purchase");
        this.useNMS = SettingsManager.getConfig().getString("Use-NMS", "auto");
        // I'm not sure why "no" is translated to "false", but this changes it back
        if (useNMS.equalsIgnoreCase("false")) useNMS = "no";
    }

    public boolean isAmmoEnabled() {
        return ammoEnabled;
    }

    public boolean shouldCloseAfterSelect() {
        return closeAfterSelect;
    }

    public boolean displaysCooldownInBar() {
        return cooldownInBar;
    }

    public boolean usingCustomCommandBackArrow() {
        return customCommandBackArrow;
    }

    public boolean usingFileStorage() {
        return fileStorage;
    }

    public boolean useMoneyTreasureLoot() {
        return moneyTreasureLoot;
    }

    public boolean arePlaceholdersColored() {
        return placeHolderColor;
    }

    public boolean areTreasureChestsEnabled() {
        return treasureChests;
    }

    public String getCustomBackMenuCommand() {
        return customBackMenuCommand;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isAmmoPurchaseEnabled() {
        return ammoEnabled && ammoPurchase;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    /**
     * @return UltraCosmetics instance. (As Plugin)
     */
    public UltraCosmetics getPlugin() {
        return ultraCosmetics;
    }

    public boolean areCosmeticsProfilesEnabled() {
        return cosmeticsProfilesEnabled;
    }

    public void setFileStorage(boolean fileStorage) {
        this.fileStorage = fileStorage;
    }

    public String getNmsConfigOption() {
        return useNMS;
    }

    public String getRawNMSVersion() {
        return nmsVersion;
    }
}
