package is.swan.tokens.storage.impl;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import is.swan.tokens.Configuration;
import is.swan.tokens.Tokens;
import is.swan.tokens.profile.Profile;
import is.swan.tokens.storage.Storable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLStorage implements Storable {

    private final Tokens instance;
    private final MysqlDataSource dataSource;

    public MySQLStorage(Tokens instance) {
        this.instance = instance;
        this.dataSource = new MysqlConnectionPoolDataSource();
    }

    @Override
    public boolean init() {
        dataSource.setServerName(Configuration.MYSQL_HOST);
        dataSource.setPort(Configuration.MYSQL_PORT);
        dataSource.setDatabaseName(Configuration.MYSQL_DATABASE);
        dataSource.setUser(Configuration.MYSQL_USER);
        dataSource.setPassword(Configuration.MYSQL_PASSWORD);

        String sql = "CREATE TABLE IF NOT EXISTS `supertokens` ( `uuid` CHAR(36) NOT NULL , `tokens` BIGINT DEFAULT 0 NOT NULL, PRIMARY KEY (uuid))";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {;
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void loadProfile(UUID uuid) {
        Profile profile = new Profile(uuid);

        String sql = "SELECT tokens FROM supertokens WHERE uuid = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                profile.setTokens(resultSet.getLong("tokens"));
            } else {
                profile.setTokens(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        instance.getProfileManager().load(profile);
    }

    @Override
    public void saveProfile(UUID uuid) {
        Profile profile = instance.getProfileManager().getProfile(uuid);

        if (profile == null) return;
        if (profile.getTokens() == 0) return;

        String sql = "REPLACE supertokens(uuid, tokens) VALUES (?, ?);";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setLong(2, profile.getTokens());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
