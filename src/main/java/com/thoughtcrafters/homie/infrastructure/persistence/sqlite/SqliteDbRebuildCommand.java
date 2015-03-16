package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import com.thoughtcrafters.homie.HomieConfiguration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class SqliteDbRebuildCommand extends ConfiguredCommand<HomieConfiguration> {

    public SqliteDbRebuildCommand() {
        super("rebuildDb", "Builds db from scratch, if there was a db before it will be taken down.");
    }

    @Override
    protected void run(Bootstrap<HomieConfiguration> bootstrap,
                       Namespace namespace,
                       HomieConfiguration homieConfiguration) throws Exception {
        rebuildDb(SqliteConnectionFactory.jdbiConnectionTo(homieConfiguration.dbPath()));
    }

    public void rebuildDb(DBI jdbi) {
        try (Handle h = jdbi.open()) {
            h.execute("drop table if exists room");
            h.execute("drop table if exists appliance");
            h.execute("drop table if exists light");
            h.execute("drop table if exists shape");
            h.execute("drop table if exists shape_point");
            h.execute("drop table if exists room_appliance");

            h.execute("create table room (room_id text primary key, name text not null)");

            h.execute("create table appliance (appliance_id text primary key, name text not null,"
                              + " appliance_type text not null, room_id text,"
                              + " foreign key(room_id) references room(room_id))"
            );

            h.execute("create table light (appliance_id text primary key, switch_state text not null,"
                              + " brightness integer,"
                              + " foreign key(appliance_id) references appliance(appliance_id))");

            h.execute("create table shape_point (shape_point_id integer primary key,"
                              + " x real, y real, position integer, room_id text,"
                              + " foreign key(room_id) references room(room_id))"
            );

            h.execute("create table room_appliance (room_appliance_id integer primary key,"
                              + " x real, y real, room_id text, appliance_id text,"
                              + " foreign key(room_id) references room(room_id),"
                              + " foreign key(appliance_id) references appliance(appliance_id))"
            );

        }
    }
}
