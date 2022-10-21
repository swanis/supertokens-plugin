package is.swan.tokens.command.subcommands;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.utils.command.Command;
import is.swan.tokens.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;

public class TokensRefreshCommand extends PluginCommand {

    private Tokens instance;

    public TokensRefreshCommand(Tokens instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "refresh", permission = "tokens.refresh", subCommand = true, baseCommand = "tokens")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/tokens refresh <category>"));
            return;
        }

        if(!args[1].equalsIgnoreCase("normal") && !args[1].equalsIgnoreCase("special")) {
            commandSender.sendMessage("Category should be either: 'normal' or 'special'");
            return;
        }

        if(args[1].equalsIgnoreCase("normal")) {
            instance.setNormalTime(System.currentTimeMillis());
            commandSender.sendMessage("The normal items will refresh in a moment...");
            return;
        }

        if(args[1].equalsIgnoreCase("special")) {
            instance.setSpecialTime(System.currentTimeMillis());
            commandSender.sendMessage("The special items will refresh in a moment...");
            return;
        }
    }
}
