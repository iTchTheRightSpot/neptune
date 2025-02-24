package org.inventory;

import org.testcontainers.containers.PostgreSQLContainer;

abstract class SuperAbstract {
    protected static final PostgreSQLContainer<?> sql;

    static {
        sql = new PostgreSQLContainer<>("postgres:15.6")
                .withDatabaseName("neptune_db")
                .withUsername("neptune")
                .withPassword("neptune");

        if (Boolean.parseBoolean(System.getProperty("CI_PROFILE"))) {
            if (!sql.isCreated() || !sql.isRunning()) {
                sql.start();
                System.setProperty("DB_URL", sql.getJdbcUrl());
                System.setProperty("DB_USERNAME", sql.getUsername());
                System.setProperty("DB_PASSWORD", sql.getPassword());
            }
        }
    }
}
