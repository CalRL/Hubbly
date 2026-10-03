package me.calrl.hubbly.storage.mysql;

import com.zaxxer.hikari.HikariConfig;

public final class MySQLConfig {
    private MySQLConfig() {}

    public static void apply(HikariConfig config) {
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("useSSL", false);

        config.setConnectionTestQuery("SELECT 1");
    }
}
