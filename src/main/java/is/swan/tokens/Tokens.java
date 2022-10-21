package is.swan.tokens;

import is.swan.tokens.chance.ChanceManager;
import is.swan.tokens.command.TokensCommand;
import is.swan.tokens.command.subcommands.*;
import is.swan.tokens.listeners.BlockListener;
import is.swan.tokens.listeners.InventoryListener;
import is.swan.tokens.listeners.TokensListener;
import is.swan.tokens.listeners.PlayerListener;
import is.swan.tokens.placeholder.HolographicDisplaysHook;
import is.swan.tokens.placeholder.PlaceholderAPIHook;
import is.swan.tokens.profile.ProfileManager;
import is.swan.tokens.reward.RewardManager;
import is.swan.tokens.storage.Storable;
import is.swan.tokens.storage.impl.MySQLStorage;
import is.swan.tokens.storage.impl.YamlStorage;
import is.swan.tokens.utils.ItemBuilder;
import is.swan.tokens.utils.MetricsLite;
import is.swan.tokens.utils.command.CommandManager;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Tokens extends JavaPlugin {

    /*
    The code in this plugin was written by Swanis (https://www.mc-market.org/members/71127/) and therefore he owns all rights to it and the plugin.
    */

    private Storable storage;
    private Inventory inventory;

    private ProfileManager profileManager;
    private RewardManager rewardManager;
    private ChanceManager chanceManager;
    private CommandManager commandManager;

    private long normalTime;
    private long specialTime;
    private boolean loaded;
    private boolean forceDisable;

    private CoreProtectAPI coreProtectAPI;

    @Override
    public void onEnable() {
        loadConfiguration();
        if (loadStorage()) {

            registerManagers();
            registerCommands();
            registerListeners();
            registerPlaceholders();

            getServer().getOnlinePlayers().stream().map(player -> player.getUniqueId()).forEach(storage::loadProfile);
            loadInventory();
            rewardManager.loadLastRewards();
            runTimers();

            if (getServer().getPluginManager().isPluginEnabled("CoreProtect")) {
                coreProtectAPI = getCoreProtect();
            }

            new TokensAPI(this);
            new MetricsLite(this);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to establish MySQL connection, disabling SuperTokens...");

            forceDisable = true;

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (!forceDisable) {
            getServer().getOnlinePlayers().stream().map(player -> player.getUniqueId()).forEach(storage::saveProfile);
            rewardManager.saveLastRewards();
        }
    }

    private void loadConfiguration() {
        saveDefaultConfig();
        new Configuration(this);
    }

    private boolean loadStorage() {
        if (Configuration.MYSQL_ENABLED) {
            storage = new MySQLStorage(this);
        } else {
            storage = new YamlStorage(this);
        }

        return storage.init();
    }

    private void registerManagers() {
        profileManager = new ProfileManager();
        rewardManager = new RewardManager(this);
        chanceManager = new ChanceManager(this);
        commandManager = new CommandManager(this);
    }

    private void registerCommands() {
        commandManager.register(new TokensCommand(this));

        //Subcommands
        commandManager.register(new TokensWithdrawCommand(this));
        commandManager.register(new TokensBalanceCommand(this));
        commandManager.register(new TokensPayCommand(this));
        commandManager.register(new TokensGiveCommand(this));
        commandManager.register(new TokensTakeCommand(this));
        commandManager.register(new TokensSetCommand(this));
        commandManager.register(new TokensGiveItemCommand(this));
        commandManager.register(new TokensRefreshCommand(this));
        commandManager.register(new TokensAuthorCommand(this));
        commandManager.register(new TokensReloadCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new TokensListener(this), this);
    }

    private void registerPlaceholders() {
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }

        if(getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            new HolographicDisplaysHook(this).hook();
        }
    }

    private void runTimers() {
        if(!loaded) {
            normalTime = System.currentTimeMillis() + (Configuration.TOKEN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
            specialTime = System.currentTimeMillis() + (Configuration.TOKEN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
            rewardManager.refreshNormalRewards();
            rewardManager.refreshSpecialRewards();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(normalTime < System.currentTimeMillis()) {
                    rewardManager.refreshNormalRewards();
                    normalTime = System.currentTimeMillis() + (Configuration.TOKEN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
                    getServer().broadcastMessage(Configuration.TOKEN_NORMAL_SHOP_UPDATED_MESSAGE);
                }

                if(specialTime < System.currentTimeMillis()) {
                    rewardManager.refreshSpecialRewards();
                    specialTime = System.currentTimeMillis() + (Configuration.TOKEN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
                    getServer().broadcastMessage(Configuration.TOKEN_SPECIAL_SHOP_UPDATED_MESSAGE);
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);

        if (Configuration.RECEIVED_TOKEN_FROM_BLOCK_MESSAGE_SENT) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    profileManager.getProfiles().stream().filter(profile -> profile.getAmountMined() != 0).forEach(profile -> {
                        String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(profile.getAmountMined()) : String.valueOf(profile.getAmountMined());

                        profile.getPlayer().sendMessage(Configuration.RECEIVED_TOKEN_FROM_BLOCK_MESSAGE.replace("%amount%", amount));
                        profile.setAmountMined(0);
                    });
                }
            }.runTaskTimer(this, Configuration.TOKEN_RECEIVE_MESSAGE_INTERVAL_SECONDS * 20L, Configuration.TOKEN_RECEIVE_MESSAGE_INTERVAL_SECONDS * 20L);
        }
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        return CoreProtect;
    }

    public void loadInventory() {
        inventory = getServer().createInventory(null, (Configuration.GUI_ROWS * 9), Configuration.GUI_TITLE);

        if(Configuration.GUI_FILLER_ENABLED) {
            ItemStack fillerItem = new ItemBuilder(Configuration.GUI_FILLER_ITEM_MATERIAL)
                    .setName(Configuration.GUI_FILLER_ITEM_NAME)
                    .setDurability(Configuration.GUI_FILLER_ITEM_DURABILITY)
                    .addGlow(Configuration.GUI_FILLER_ITEM_GLOW)
                    .toItemStack();

            for(int i = 0; i < inventory.getSize(); i++) {
                if(inventory.getItem(i) == null)
                    inventory.setItem(i, fillerItem);
            }
        }
    }

    public ItemStack getMobCoinItem() {
        return new ItemBuilder(Configuration.TOKEN_ITEM_MATERIAL).setName(Configuration.TOKEN_ITEM_NAME).setLore(Configuration.TOKEN_ITEM_LORE).toItemStack();
    }

    public Storable getStorage() {
        return storage;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public ChanceManager getChanceManager() {
        return chanceManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public long getNormalTime() {
        return normalTime;
    }

    public void setNormalTime(long normalTime) {
        this.normalTime = normalTime;
    }

    public long getSpecialTime() {
        return specialTime;
    }

    public void setSpecialTime(long specialTime) {
        this.specialTime = specialTime;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public CoreProtectAPI getCoreProtectAPI() {
        return coreProtectAPI;
    }
}
