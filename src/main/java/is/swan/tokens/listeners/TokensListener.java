package is.swan.tokens.listeners;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.events.TokensReceiveEvent;
import is.swan.tokens.events.TokensRedeemEvent;
import is.swan.tokens.events.TokensShopEvent;
import is.swan.tokens.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TokensListener implements Listener {

    private Tokens instance;

    public TokensListener(Tokens instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTokensReceive(TokensReceiveEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        if(String.valueOf(event.getAmount()).length() > 10) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG_MESSAGE);
            return;
        }

        profile.setTokens(profile.getTokens() + event.getAmount());

        if(Configuration.RECEIVED_TOKEN_FROM_BLOCK_MESSAGE_SENT) {
            profile.setAmountMined(profile.getAmountMined() + event.getAmount());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTokensRedeem(TokensRedeemEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        if(String.valueOf(event.getAmount()).length() > 10) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG_MESSAGE);
            return;
        }

        profile.setTokens(profile.getTokens() + event.getAmount());

        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);

        String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(event.getAmount()) : String.valueOf(event.getAmount());

        player.sendMessage(Configuration.REDEEMED_TOKENS_MESSAGE.replace("%amount%", amount));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTokensShop(TokensShopEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        if(String.valueOf(event.getReward().getPrice()).length() > 10) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG_MESSAGE);
            return;
        }

        event.getReward().getCommands().forEach(command -> instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), command.replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString())));

        profile.setTokens(profile.getTokens() - event.getPrice());

        String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(event.getReward().getPrice()) : String.valueOf(event.getReward().getPrice());

        player.sendMessage(Configuration.BOUGHT_REWARD_MESSAGE.replace("%reward%", event.getReward().getName()).replace("%amount%", amount));
    }
}
