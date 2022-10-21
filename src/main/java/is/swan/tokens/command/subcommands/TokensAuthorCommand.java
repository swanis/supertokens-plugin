package is.swan.tokens.command.subcommands;

import is.swan.tokens.Tokens;
import is.swan.tokens.utils.command.Command;
import is.swan.tokens.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;

public class TokensAuthorCommand extends PluginCommand {

    public TokensAuthorCommand(Tokens instance) {
        super(instance);
    }

    @Command(command = "author", subCommand = true, baseCommand = "tokens")
    public void onCommand(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("This server is running SuperTokens v1.1 created by Swanis ( https://www.mc-market.org/members/71127/ )");
    }
}
