package is.swan.tokens.chance;

import is.swan.tokens.Tokens;
import is.swan.tokens.utils.YamlFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ChanceManager {

    private Tokens instance;

    private YamlFile chancesFile;
    private Map<Material, DropChance> dropChances = new HashMap<>();

    public ChanceManager(Tokens instance) {
        this.instance = instance;

        chancesFile = new YamlFile("dropchances", instance);
        loadChances();
    }

    public DropChance getChance(Material material) {
        return dropChances.get(material);
    }

    private void loadChances() {
        FileConfiguration config = chancesFile.getConfig();

        config.getKeys(false).forEach(string -> {
            Material material = Material.valueOf(config.getString(string + ".type"));
            double chance = config.getDouble(string + ".chance");
            short data = (short) config.getInt(string + ".data");
            dropChances.put(material, new DropChance(chance, data));
        });
    }

    public void reloadChances() {
        chancesFile = new YamlFile("dropchances", instance);
        dropChances.clear();
        loadChances();
    }

    public Map<Material, DropChance> getDropChances() {
        return dropChances;
    }
}
