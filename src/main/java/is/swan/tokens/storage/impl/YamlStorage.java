package is.swan.tokens.storage.impl;

import is.swan.tokens.Tokens;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.storage.Storable;
import is.swan.tokens.utils.YamlFile;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class YamlStorage implements Storable {

    private Tokens instance;

    private YamlFile file;
    private FileConfiguration config;

    public YamlStorage(Tokens instance) {
        this.instance = instance;
    }

    @Override
    public boolean init() {
        file = new YamlFile("profiles", instance);
        config = file.getConfig();

        return true;
    }

    @Override
    public void loadProfile(UUID uuid) {
        String prefix = "Profile." + uuid.toString();

        Profile profile = new Profile(uuid);

        if(config.getConfigurationSection(prefix) == null) {
            instance.getProfileManager().load(profile);
            return;
        }

        int tokens = config.getInt(prefix + ".tokens");

        profile.setTokens(tokens);

        instance.getProfileManager().load(profile);
    }

    @Override
    public void saveProfile(UUID uuid) {
        Profile profile = instance.getProfileManager().getProfile(uuid);

        if(profile == null) return;

        String prefix = "Profile." + uuid.toString();

        config.set(prefix + ".tokens", profile.getTokens());

        file.save();

        instance.getProfileManager().unload(profile);
    }
}
