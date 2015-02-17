package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfiguration;
import io.dropwizard.testing.junit.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SqliteDbRebuildCommandTest {

    public static String dbTestFile = "homieTest.db";

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml",
                                    ConfigOverride.config("dbPath", dbTestFile));

    @After
    public void tearDown() throws Exception {
        new File(dbTestFile).delete();
    }

    @Test
    public void createdFileWhenRebuildsTheDb() throws Exception {
        // given
        SqliteDbRebuildCommand dbRebuildCommand = new SqliteDbRebuildCommand();

        // when
        dbRebuildCommand.run(null, null, app.getConfiguration());

        // then
        assertThat(new File(dbTestFile)).exists().isFile();
    }

    @Test
    public void populatesTheDbWithCorrectTables() throws Exception {
        // given
        SqliteDbRebuildCommand dbRebuildCommand = new SqliteDbRebuildCommand();

        // when
        dbRebuildCommand.run(null, null, app.getConfiguration());

        // then
        DBI dbi = SqliteConnectionFactory.jdbiFrom(app.getConfiguration().dbPath());
        assertCreatedATable(dbi, "room");
        assertCreatedATable(dbi, "appliance");
        assertCreatedATable(dbi, "shape");
        assertCreatedATable(dbi, "shapePoint");
        assertCreatedATable(dbi, "roomAppliance");
    }

    private void assertCreatedATable(DBI dbi, String tableName) {
        Handle h = dbi.open();
        Map<String, Object> first = h.createQuery("select * from " + tableName).first();
        assertThat(first).isNullOrEmpty();
        h.close();
    }

}