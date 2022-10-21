package is.swan.tokens.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Profile {

    private UUID uuid;
    private long tokens;
    private int amountMined;

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public long getTokens() {
        return tokens;
    }

    public void setTokens(long tokens) {
        this.tokens = tokens;
    }

    public int getAmountMined() {
        return amountMined;
    }

    public void setAmountMined(int amountMined) {
        this.amountMined = amountMined;
    }
}
