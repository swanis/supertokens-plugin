package is.swan.tokens.placeholder;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import is.swan.tokens.Tokens;
import is.swan.tokens.utils.TimeUtil;

public class HolographicDisplaysHook {

    private Tokens instance;

    public HolographicDisplaysHook(Tokens instance) {
        this.instance = instance;
    }

    public void hook() {
        HologramsAPI.registerPlaceholder(instance, "%supertokens_normal_time%", 1, new PlaceholderReplacer() {
            @Override
            public String update() {
                long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(normalTimeLeft);
            }
        });

        HologramsAPI.registerPlaceholder(instance, "%supertokens_special_time%", 1, new PlaceholderReplacer() {
            @Override
            public String update() {
                long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(specialTimeLeft);
            }
        });
    }
}
