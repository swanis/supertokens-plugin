package is.swan.tokens.command.subcommands;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.utils.command.Command;
import is.swan.tokens.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokensPayCommand extends PluginCommand {

    private Tokens instance;

    public TokensPayCommand(Tokens instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "pay", permission = "tokens.pay", subCommand = true, baseCommand = "tokens")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
            return;
        }

        Player player = (Player) commandSender;

        if(args.length < 3) {
            player.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/tokens pay <player> <amount>"));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(Configuration.CANNOT_PAY_YOURSELF_MESSAGE);
            return;
        }

        if(instance.getServer().getPlayerExact(args[1]) == null) {
            player.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
            return;
        }

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        Player target = instance.getServer().getPlayerExact(args[1]);
        Profile targetProfile = instance.getProfileManager().getProfile(target.getUniqueId());

        if(targetProfile == null) {
            player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
            return;
        }

        if(!StringUtils.isNumeric(args[2])) {
            player.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
            return;
        }

        if(args[2].length() > 10) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG_MESSAGE);
            return;
        }

        int amount = Integer.valueOf(args[2]);

        if (profile.getTokens() < amount) {
            player.sendMessage(Configuration.NOT_ENOUGH_TOKENS_MESSAGE);
            return;
        }

        profile.setTokens(profile.getTokens() - amount);
        targetProfile.setTokens(targetProfile.getTokens() + amount);

        String amountString = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(amount) : String.valueOf(amount);

        player.sendMessage(Configuration.GAVE_TOKENS_MESSAGE.replace("%amount%", amountString).replace("%player%", target.getName()));
        target.sendMessage(Configuration.RECEIVED_TOKENS_MESSAGE.replace("%amount%", amountString).replace("%sender%", player.getName()));
    }
}
