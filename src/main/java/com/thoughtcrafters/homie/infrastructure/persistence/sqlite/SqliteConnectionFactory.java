package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import org.skife.jdbi.v2.DBI;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

public class SqliteConnectionFactory {
    public static DBI jdbiConnectionTo(String dbFilePath) {
        SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbFilePath);
        return new DBI(dataSource);
    }
}
