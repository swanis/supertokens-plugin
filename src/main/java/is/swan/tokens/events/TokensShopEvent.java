package is.swan.tokens.events;

import is.swan.tokens.profile.Profile;
import is.swan.tokens.reward.Reward;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TokensShopEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;

    private Profile profile;
    private Reward reward;
    private int price;

    public TokensShopEvent(Profile profile, Reward reward, int price) {
        this.profile = profile;
        this.reward = reward;
        this.price = price;
    }

    public Profile getProfile() {
        return profile;
    }

    public Reward getReward() {
        return reward;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
