package is.swan.tokens.storage;

import java.util.UUID;

public interface Storable {

    boolean init();

    void loadProfile(UUID uuid);

    void saveProfile(UUID uuid);

}
