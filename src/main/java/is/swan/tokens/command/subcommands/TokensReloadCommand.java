package is.swan.tokens.command.subcommands;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.utils.command.Command;
import is.swan.tokens.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;

public class TokensReloadCommand extends PluginCommand {

    private Tokens instance;

    public TokensReloadCommand(Tokens instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "reload", permission = "tokens.reload", subCommand = true, baseCommand = "tokens")
    public void onCommand(CommandSender commandSender, String[] args) {
        instance.reloadConfig();
        new Configuration(instance);
        instance.loadInventory();
        instance.getRewardManager().reloadRewards();
        instance.getChanceManager().reloadChances();
        commandSender.sendMessage("The configuration has been reloaded");
    }
}
