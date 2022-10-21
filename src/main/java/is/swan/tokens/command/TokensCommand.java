package is.swan.tokens.command;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.utils.ItemBuilder;
import is.swan.tokens.utils.TimeUtil;
import is.swan.tokens.utils.command.Command;
import is.swan.tokens.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TokensCommand extends PluginCommand {

    private Tokens instance;

    public TokensCommand(Tokens instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "tokens", permission = "tokens.use", subCommands = {"withdraw", "balance", "pay", "give", "take", "set", "giveitem", "refresh", "author", "reload"})
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 1) {
            if(!(commandSender instanceof Player)) {
                commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
                return;
            }

            Player player = (Player) commandSender;
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

            if(profile == null) {
                player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
                return;
            }


            Inventory inventory = clone(instance.getInventory(), Configuration.GUI_TITLE);

            Configuration.GUI_DECORATION_ITEMS.keySet().forEach(integer -> {
                ItemStack itemStack = Configuration.GUI_DECORATION_ITEMS.get(integer).clone();

                long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
                long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();

                List<String> lore = new ArrayList<>();

                if(itemStack.getItemMeta().hasLore()) {
                    itemStack.getItemMeta().getLore().forEach(string -> lore.add(string.replace("%coins%", String.valueOf(profile.getTokens())).replace("%normaltime%", TimeUtil.getFormattedString(normalTimeLeft)).replace("%specialtime%", TimeUtil.getFormattedString(specialTimeLeft))));
                }

                String name = itemStack.getItemMeta().getDisplayName().replace("%coins%", String.valueOf(profile.getTokens())).replace("%normaltime%", TimeUtil.getFormattedString(normalTimeLeft)).replace("%specialtime%", TimeUtil.getFormattedString(specialTimeLeft));

                ItemStack formattedItemStack = new ItemBuilder(itemStack).setName(name).setLore(lore).toItemStack();

                inventory.setItem(integer, formattedItemStack);
            });

            if(Configuration.GUI_OPEN_SOUND_ENABLED) {
                player.playSound(player.getLocation(), Configuration.GUI_OPEN_SOUND_TYPE, 10, 1);
            }

            player.openInventory(inventory);
            return;
        }

        if(commandSender.hasPermission("tokens.admin")) {
            Configuration.TOKENS_HELP_ADMIN_LORE.forEach(commandSender::sendMessage);
            return;
        }

        Configuration.TOKENS_HELP_LORE.forEach(commandSender::sendMessage);
    }

    private Inventory clone(Inventory inventory, String title) {
        Inventory clone = instance.getServer().createInventory(null, inventory.getSize(), title);
        clone.setContents(inventory.getContents());
        return clone;
    }
}
