package is.swan.tokens.placeholder;

import is.swan.tokens.Tokens;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.utils.TimeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private Tokens instance;

    public PlaceholderAPIHook(Tokens instance) {
        this.instance = instance;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "supertokens";
    }

    @Override
    public String getAuthor() {
        return instance.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return instance.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if(player == null) return null;

        switch(params) {
            case "tokens": {
                Profile profile = instance.getProfileManager().getProfile(player);

                if(profile == null) return null;

                return String.valueOf(profile.getTokens());
            }

            case "normal_time": {
                long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(normalTimeLeft);
            }

            case "special_time": {
                long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(specialTimeLeft);
            }

            default:
                return null;
        }
    }
}
