package is.swan.tokens;

import is.swan.tokens.utils.ItemBuilder;
import is.swan.tokens.utils.Sounds;
import is.swan.tokens.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.*;

public class Configuration {

    public static boolean MYSQL_ENABLED;
    public static String MYSQL_HOST;
    public static int MYSQL_PORT;
    public static String MYSQL_DATABASE;
    public static String MYSQL_USER;
    public static String MYSQL_PASSWORD;
    public static String GUI_TITLE;
    public static int GUI_ROWS;
    public static Map<Integer, ItemStack> GUI_DECORATION_ITEMS = new HashMap();
    public static boolean GUI_FILLER_ENABLED;
    public static Material GUI_FILLER_ITEM_MATERIAL;
    public static String GUI_FILLER_ITEM_NAME;
    public static short GUI_FILLER_ITEM_DURABILITY;
    public static boolean GUI_FILLER_ITEM_GLOW;
    public static Material TOKEN_ITEM_MATERIAL;
    public static String TOKEN_ITEM_NAME;
    public static List<String> TOKEN_ITEM_LORE = new ArrayList<>();
    public static boolean FORMAT_ENABLED;
    public static NumberFormat FORMAT_NUMBER_FORMAT;
    public static int TOKEN_NORMAL_SHOP_UPDATE_HOURS;
    public static int TOKEN_SPECIAL_SHOP_UPDATE_HOURS;
    public static int TOKEN_RECEIVE_MESSAGE_INTERVAL_SECONDS;
    public static boolean GUI_OPEN_SOUND_ENABLED;
    public static Sound GUI_OPEN_SOUND_TYPE;
    public static boolean TOKENS_ONLY_FROM_NATURALLY_SPAWNED_MOBS;
    public static boolean RECEIVED_TOKEN_FROM_BLOCK_MESSAGE_SENT;
    public static boolean CLOSE_GUI_ON_BUY;
    public static boolean STACKING_SUPPORT;
    public static List<String> DISABLED_WORLDS = new ArrayList<>();
    public static boolean COREPROTECT_PREVENT_TOKENS_FROM_PLACES_BLOCKS;

    public static String NO_PERMISSION_MESSAGE;
    public static String USAGE_MESSAGE;
    public static String PLAYER_NOT_FOUND_MESSAGE;
    public static String PROFILE_NOT_FOUND_MESSAGE;
    public static String NOT_NUMERIC_MESSAGE;
    public static String GAVE_TOKENS_MESSAGE;
    public static String RECEIVED_TOKENS_MESSAGE;
    public static String GAVE_TOKEN_ITEMS_MESSAGE;
    public static String RECEIVED_TOKEN_ITEMS_MESSAGE;
    public static String NOT_PLAYER_MESSAGE;
    public static String AMOUNT_CANT_BE_ZERO_MESSAGE;
    public static String NOT_ENOUGH_TOKENS_MESSAGE;
    public static String WITHDREW_TOKENS_MESSAGE;
    public static String BOUGHT_REWARD_MESSAGE;
    public static String RECEIVED_TOKEN_FROM_BLOCK_MESSAGE;
    public static String REDEEMED_TOKENS_MESSAGE;
    public static String TOKEN_NORMAL_SHOP_UPDATED_MESSAGE;
    public static String TOKEN_SPECIAL_SHOP_UPDATED_MESSAGE;
    public static String TOKENS_OF_PLAYER_MESSAGE;
    public static String PLAYER_NOT_ENOUGH_TOKENS;
    public static String TOOK_TOKENS_MESSAGE;
    public static String PLAYER_TOOK_TOKENS_MESSAGE;
    public static String SET_TOKENS_MESSAGE;
    public static String YOUR_TOKENS_SET_MESSAGE;
    public static String INVENTORY_FULL_MESSAGE;
    public static String INVENTORY_GOT_FILLED_MESSAGE;
    public static String AMOUNT_INPUT_TOO_LONG_MESSAGE;
    public static String CANNOT_PAY_YOURSELF_MESSAGE;
    public static List<String> TOKENS_HELP_LORE = new ArrayList<>();
    public static List<String> TOKENS_HELP_ADMIN_LORE = new ArrayList<>();

    public Configuration(Tokens instance) {
        MYSQL_ENABLED = instance.getConfig().getBoolean("mysql.enabled");
        MYSQL_HOST = instance.getConfig().getString("mysql.host");
        MYSQL_PORT = instance.getConfig().getInt("mysql.port");
        MYSQL_DATABASE = instance.getConfig().getString("mysql.database");
        MYSQL_USER = instance.getConfig().getString("mysql.user");
        MYSQL_PASSWORD = instance.getConfig().getString("mysql.password");

        GUI_TITLE = StringUtil.color(instance.getConfig().getString("gui.title"));
        GUI_ROWS =  instance.getConfig().getInt("gui.rows");

        GUI_DECORATION_ITEMS.clear();
        instance.getConfig().getConfigurationSection("gui.decoration").getKeys(false).forEach(string -> {
            String prefix = "gui.decoration." + string;

            Material material = null;

            try {
                material = Material.valueOf(instance.getConfig().getString(prefix + ".material"));
            } catch (IllegalArgumentException e) {
                try {
                    if (instance.getConfig().getString(prefix + ".material").equals("STAINED_GLASS_PANE")) {
                        material = Material.valueOf("GRAY_STAINED_GLASS_PANE");
                    } else {
                        material = Material.valueOf("LEGACY_" + instance.getConfig().getString(prefix + ".material"));
                    }
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            String name = StringUtil.color(instance.getConfig().getString(prefix + ".name"));
            List<String> lore = new ArrayList<>();
            instance.getConfig().getStringList(prefix + ".lore").forEach(string1 -> lore.add(StringUtil.color(string1)));
            short durability = (short) instance.getConfig().getInt(prefix + ".durability");
            boolean glow = instance.getConfig().getBoolean(prefix + ".glow");
            int slot = instance.getConfig().getInt(prefix + ".slot");

            ItemStack itemStack = new ItemBuilder(material).setName(name).setLore(lore).setDurability(durability).addGlow(glow).toItemStack();

            GUI_DECORATION_ITEMS.put(slot, itemStack);
        });

        GUI_FILLER_ENABLED = instance.getConfig().getBoolean("gui.filler.enabled");

        try {
            GUI_FILLER_ITEM_MATERIAL = Material.valueOf(instance.getConfig().getString("gui.filler.item.material"));
        } catch (IllegalArgumentException e) {
            try {
                if (instance.getConfig().getString("gui.filler.item.material").equals("STAINED_GLASS_PANE")) {
                    GUI_FILLER_ITEM_MATERIAL = Material.valueOf("GRAY_STAINED_GLASS_PANE");
                } else {
                    GUI_FILLER_ITEM_MATERIAL = Material.valueOf("LEGACY_" + instance.getConfig().getString("gui.filler.item.material"));
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        GUI_FILLER_ITEM_NAME = StringUtil.color(instance.getConfig().getString("gui.filler.item.name"));
        GUI_FILLER_ITEM_DURABILITY = (short) instance.getConfig().getInt("gui.filler.item.durability");
        GUI_FILLER_ITEM_GLOW = instance.getConfig().getBoolean("gui.filler.item.glow");
        GUI_OPEN_SOUND_ENABLED = instance.getConfig().getBoolean("gui_open.sound.enabled");

        try {
            GUI_OPEN_SOUND_TYPE = Sounds.valueOf(instance.getConfig().getString("gui_open.sound.type")).bukkitSound();
        } catch (IllegalArgumentException e) {
            GUI_OPEN_SOUND_TYPE = Sound.valueOf(instance.getConfig().getString("gui_open.sound.type"));
        }

        try {
            TOKEN_ITEM_MATERIAL = Material.valueOf(instance.getConfig().getString("token_item.material"));
        } catch (IllegalArgumentException e) {
            try {
                TOKEN_ITEM_MATERIAL = Material.valueOf("LEGACY_" + instance.getConfig().getString("token_item.material"));
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        TOKEN_ITEM_NAME = StringUtil.color(instance.getConfig().getString("token_item.name"));
        Configuration.TOKEN_ITEM_LORE.clear();
        instance.getConfig().getStringList("token_item.lore").forEach(string -> Configuration.TOKEN_ITEM_LORE.add(StringUtil.color(string)));
        FORMAT_ENABLED = instance.getConfig().getBoolean("format.enabled");
        FORMAT_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.forLanguageTag(instance.getConfig().getString("format.locale")));
        TOKEN_NORMAL_SHOP_UPDATE_HOURS = instance.getConfig().getInt("token_normal_shop_update_hours");
        TOKEN_SPECIAL_SHOP_UPDATE_HOURS = instance.getConfig().getInt("token_special_shop_update_hours");
        TOKEN_RECEIVE_MESSAGE_INTERVAL_SECONDS = instance.getConfig().getInt("token_receive_message_interval_seconds");
        TOKENS_ONLY_FROM_NATURALLY_SPAWNED_MOBS = instance.getConfig().getBoolean("tokens_only_from_naturally_spawned_mods");
        RECEIVED_TOKEN_FROM_BLOCK_MESSAGE_SENT = instance.getConfig().getBoolean("received_token_from_block_message_sent");
        CLOSE_GUI_ON_BUY = instance.getConfig().getBoolean("close_gui_on_buy");
        STACKING_SUPPORT = instance.getConfig().getBoolean("stacking_support");
        instance.getConfig().getStringList("disabled_worlds").forEach(DISABLED_WORLDS::add);
        COREPROTECT_PREVENT_TOKENS_FROM_PLACES_BLOCKS = instance.getConfig().getBoolean("coreprotect_prevent_tokens_from_placed_blocks");

        NO_PERMISSION_MESSAGE = StringUtil.color(instance.getConfig().getString("NO_PERMISSION_MESSAGE"));
        USAGE_MESSAGE = StringUtil.color(instance.getConfig().getString("USAGE_MESSAGE"));
        PLAYER_NOT_FOUND_MESSAGE = StringUtil.color(instance.getConfig().getString("PLAYER_NOT_FOUND_MESSAGE"));
        PROFILE_NOT_FOUND_MESSAGE = StringUtil.color(instance.getConfig().getString("PROFILE_NOT_FOUND_MESSAGE"));
        NOT_NUMERIC_MESSAGE = StringUtil.color(instance.getConfig().getString("NOT_NUMERIC_MESSAGE"));
        GAVE_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("GAVE_TOKENS_MESSAGE"));
        RECEIVED_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("RECEIVED_TOKENS_MESSAGE"));
        GAVE_TOKEN_ITEMS_MESSAGE = StringUtil.color(instance.getConfig().getString("GAVE_TOKEN_ITEMS_MESSAGE"));
        RECEIVED_TOKEN_ITEMS_MESSAGE = StringUtil.color(instance.getConfig().getString("RECEIVED_TOKEN_ITEMS_MESSAGE"));
        NOT_PLAYER_MESSAGE = StringUtil.color(instance.getConfig().getString("NOT_PLAYER_MESSAGE"));
        AMOUNT_CANT_BE_ZERO_MESSAGE = StringUtil.color(instance.getConfig().getString("AMOUNT_CANT_BE_ZERO_MESSAGE"));
        NOT_ENOUGH_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("NOT_ENOUGH_TOKENS_MESSAGE"));
        WITHDREW_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("WITHDREW_TOKENS_MESSAGE"));
        BOUGHT_REWARD_MESSAGE = StringUtil.color(instance.getConfig().getString("BOUGHT_REWARD_MESSAGE"));
        RECEIVED_TOKEN_FROM_BLOCK_MESSAGE = StringUtil.color(instance.getConfig().getString("RECEIVED_TOKEN_FROM_BLOCK_MESSAGE"));
        REDEEMED_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("REDEEMED_TOKENS_MESSAGE"));
        TOKEN_NORMAL_SHOP_UPDATED_MESSAGE = StringUtil.color(instance.getConfig().getString("TOKEN_NORMAL_SHOP_UPDATED_MESSAGE"));
        TOKEN_SPECIAL_SHOP_UPDATED_MESSAGE = StringUtil.color(instance.getConfig().getString("TOKEN_SPECIAL_SHOP_UPDATED_MESSAGE"));
        TOKENS_OF_PLAYER_MESSAGE = StringUtil.color(instance.getConfig().getString("TOKENS_OF_PLAYER_MESSAGE"));
        PLAYER_NOT_ENOUGH_TOKENS = StringUtil.color(instance.getConfig().getString("PLAYER_NOT_ENOUGH_TOKENS"));
        TOOK_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("TOOK_TOKENS_MESSAGE"));
        PLAYER_TOOK_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("PLAYER_TOOK_TOKENS_MESSAGE"));
        SET_TOKENS_MESSAGE = StringUtil.color(instance.getConfig().getString("SET_TOKENS_MESSAGE"));
        YOUR_TOKENS_SET_MESSAGE = StringUtil.color(instance.getConfig().getString("YOUR_TOKENS_SET_MESSAGE"));
        INVENTORY_FULL_MESSAGE = StringUtil.color(instance.getConfig().getString("INVENTORY_FULL_MESSAGE"));
        INVENTORY_GOT_FILLED_MESSAGE = StringUtil.color(instance.getConfig().getString("INVENTORY_GOT_FILLED_MESSAGE"));
        AMOUNT_INPUT_TOO_LONG_MESSAGE = StringUtil.color(instance.getConfig().getString("AMOUNT_INPUT_TOO_LONG_MESSAGE"));
        CANNOT_PAY_YOURSELF_MESSAGE = StringUtil.color(instance.getConfig().getString("CANNOT_PAY_YOURSELF_MESSAGE"));
        TOKENS_HELP_LORE.clear();
        instance.getConfig().getStringList("TOKENS_HELP_LORE").forEach(string -> Configuration.TOKENS_HELP_LORE.add(StringUtil.color(string)));
        TOKENS_HELP_ADMIN_LORE.clear();
        instance.getConfig().getStringList("TOKENS_HELP_ADMIN_LORE").forEach(string -> Configuration.TOKENS_HELP_ADMIN_LORE.add(StringUtil.color(string)));
    }
}
