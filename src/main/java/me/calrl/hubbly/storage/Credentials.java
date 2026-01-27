package me.calrl.hubbly.storage;

import org.bukkit.configuration.file.FileConfiguration;

public class Credentials {
    private final String HOST;
    private final Integer PORT;
    private final String DATABASE;
    private final String USERNAME;
    private final String PASSWORD;

    public Credentials(
            String HOST,
            Integer PORT,
            String DATABASE,
            String USERNAME,
            String PASSWORD
    ) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.DATABASE = DATABASE;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    public String getHOST() {
        return this.HOST;
    }

    public Integer getPORT() {
        return this.PORT;
    }

    public String getDATABASE() {
        return this.DATABASE;
    }

    public String getUSERNAME() {
        return this.USERNAME;
    }

    public String getPASSWORD() {
        return this.PASSWORD;
    }

    public static Credentials fromConfig(FileConfiguration config) {
        return new Credentials(
                config.getString("database.host"),
                config.getInt("database.port"),
                config.getString("database.database"),
                config.getString("database.username"),
                config.getString("database.password")
        );
    }
}
