package is.swan.tokens.command.subcommands;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.utils.command.Command;
import is.swan.tokens.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokensBalanceCommand extends PluginCommand {

    private Tokens instance;

    public TokensBalanceCommand(Tokens instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "balance", permission = "tokens.balance", subCommand = true, baseCommand = "tokens")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/tokens balance <player>"));
            return;
        }

        if(instance.getServer().getPlayer(args[1]) == null) {
            commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
            return;
        }

        Player player = instance.getServer().getPlayer(args[1]);
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(profile.getTokens()) : String.valueOf(profile.getTokens());

        commandSender.sendMessage(Configuration.TOKENS_OF_PLAYER_MESSAGE.replace("%player%", player.getName()).replace("%amount%", amount));
    }
}
