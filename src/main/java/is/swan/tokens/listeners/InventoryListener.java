package is.swan.tokens.listeners;

import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.events.TokensShopEvent;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.reward.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryListener implements Listener {

    private Tokens instance;

    public InventoryListener(Tokens instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if(inventory == null) return;
        if(!event.getView().getTitle().equals(Configuration.GUI_TITLE)) return;
        if (inventory.getHolder() instanceof Player && ((Player) inventory.getHolder()).getName().equals(player.getName())) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }

            return;
        }

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            event.setCancelled(true);
            closeInventory(player);
            player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        if(inventory.getItem(event.getSlot()) != null) {
            Reward reward = instance.getRewardManager().getCurrentRewards().values().stream().filter(r -> r.getSlot() == event.getSlot()).findFirst().orElse(null);

            event.setCancelled(true);

            if(reward == null) {
                return;
            }

            if(profile.getTokens() < reward.getPrice()) {
                player.sendMessage(Configuration.NOT_ENOUGH_TOKENS_MESSAGE);
                return;
            }

            TokensShopEvent tokensShopEvent = new TokensShopEvent(profile, reward, reward.getPrice());
            instance.getServer().getPluginManager().callEvent(tokensShopEvent);

            if(Configuration.CLOSE_GUI_ON_BUY) {
                closeInventory(player);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if(inventory == null) return;
        if(!event.getView().getTitle().equals(Configuration.GUI_TITLE)) return;

        event.setCancelled(true);
    }

    private void closeInventory(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(instance, 1L);
    }
}
