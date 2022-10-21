package is.swan.tokens;

import is.swan.tokens.chance.ChanceManager;
import is.swan.tokens.profile.ProfileManager;
import is.swan.tokens.reward.RewardManager;
import is.swan.tokens.storage.Storable;
import org.bukkit.inventory.ItemStack;

public class TokensAPI {

    private static Tokens instance;

    public TokensAPI(Tokens instance) {
        this.instance = instance;
    }

    /*
    Retrieve the profile manager
    */
    public static ProfileManager getProfileManager() {
        return instance.getProfileManager();
    }

    /*
    Retrieve the reward manager
    */
    public static RewardManager getRewardManager() {
        return instance.getRewardManager();
    }

    /*
    Retrieve the dropchance manager
    */
    public static ChanceManager getChanceManager() {
        return instance.getChanceManager();
    }

    /*
    Retrieve the storage
    */
    public static Storable getStorage() {
        return instance.getStorage();
    }

    /*
    Retrieve the token itemstack
    */
    public static ItemStack getMobCoinItem() {
        return instance.getMobCoinItem();
    }
}
