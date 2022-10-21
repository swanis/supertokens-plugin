package is.swan.tokens.command.subcommands;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.utils.ItemBuilder;
import is.swan.tokens.utils.command.Command;
import is.swan.tokens.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TokensWithdrawCommand extends PluginCommand {

    private Tokens instance;

    public TokensWithdrawCommand(Tokens instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "withdraw", permission = "tokens.withdraw", subCommand = true, baseCommand = "tokens")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
            return;
        }

        Player player = (Player) commandSender;

        if(args.length < 2) {
            player.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/tokens withdraw <amount>"));
            return;
        }

        if(!StringUtils.isNumeric(args[1])) {
            player.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[1]));
            return;
        }

        if(args[1].length() > 10) {
            commandSender.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG_MESSAGE);
            return;
        }

        int amount = Integer.valueOf(args[1]);

        if(amount == 0) {
            player.sendMessage(Configuration.AMOUNT_CANT_BE_ZERO_MESSAGE);
            return;
        }

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        if(profile.getTokens() < amount) {
            player.sendMessage(Configuration.NOT_ENOUGH_TOKENS_MESSAGE);
            return;
        }

        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Configuration.INVENTORY_FULL_MESSAGE);
            return;
        }

        int actualAmount = amount;
        boolean full = false;

        for (int i = 0; i < amount; i++) {
            if(player.getInventory().firstEmpty() == -1) {
                actualAmount = i;
                full = true;
                break;
            }

            ItemStack mobCoinItem = new ItemBuilder(Configuration.TOKEN_ITEM_MATERIAL)
                    .setName(Configuration.TOKEN_ITEM_NAME)
                    .setLore(Configuration.TOKEN_ITEM_LORE)
                    .toItemStack();

            player.getInventory().addItem(mobCoinItem);
        }

        profile.setTokens(profile.getTokens() - actualAmount);

        String amountString = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(actualAmount) : String.valueOf(actualAmount);

        if(!full) {
            player.sendMessage(Configuration.WITHDREW_TOKENS_MESSAGE.replace("%amount%", amountString));
        } else {
            player.sendMessage(Configuration.INVENTORY_GOT_FILLED_MESSAGE.replace("%amount%", amountString));
        }
    }
}
