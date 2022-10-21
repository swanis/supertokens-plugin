package is.swan.tokens.utils.command;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class PluginCommand implements CommandExecutor {

    private Tokens instance;

    private String command;
    private String permission;
    private boolean subCommand;
    private String[] subCommands;

    public PluginCommand(Tokens instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String string, String[] args) {
        if(args.length > 0) {
            for(String subCommand : subCommands) {
                if(args[0].equalsIgnoreCase(subCommand)) {
                    PluginCommand pluginCommand = instance.getCommandManager().getCommand(command + "." + subCommand);
                    if(pluginCommand != null) {
                        if(!pluginCommand.permission.equals("") && !commandSender.hasPermission(pluginCommand.permission)) {
                            commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                            return true;
                        }

                        pluginCommand.onCommand(commandSender, args);
                        return true;
                    }
                }
            }
        }

        if(!permission.equals("") && !commandSender.hasPermission(permission)) {
            commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
            return true;
        }

        onCommand(commandSender, args);
        return false;
    }

    public abstract void onCommand(CommandSender commandSender, String[] args);

    public void setCommand(String command) {
        this.command = command;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setSubCommand(boolean subCommand) {
        this.subCommand = subCommand;
    }

    public void setSubCommands(String[] subCommands) {
        this.subCommands = subCommands;
    }
}
