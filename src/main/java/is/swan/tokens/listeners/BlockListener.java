package is.swan.tokens.listeners;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.chance.DropChance;
import is.swan.tokens.events.TokensReceiveEvent;
import is.swan.tokens.profile.Profile;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

    private Tokens instance;

    public BlockListener(Tokens instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        Block block = event.getBlock();
        DropChance dropChance = instance.getChanceManager().getChance(block.getType());

        if (dropChance == null) return;
        if (dropChance.getData() != block.getData()) return;
        if (Configuration.COREPROTECT_PREVENT_TOKENS_FROM_PLACES_BLOCKS && (instance.getCoreProtectAPI() == null || !instance.getCoreProtectAPI().blockLookup(block, 604800).isEmpty())) return;
        if (Math.random() * 100 > dropChance.getChance()) return;

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) return;

        TokensReceiveEvent tokensReceiveEvent = new TokensReceiveEvent(profile, 1);

        instance.getServer().getPluginManager().callEvent(tokensReceiveEvent);
    }
}
