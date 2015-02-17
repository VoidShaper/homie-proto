package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import com.thoughtcrafters.homie.HomieConfiguration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class SqliteDbRebuildCommand extends ConfiguredCommand<HomieConfiguration>{

    public SqliteDbRebuildCommand() {
        super("rebuildDb", "Builds db from scratch, if there was a db before it will be taken down.");
    }

    @Override
    protected void run(Bootstrap<HomieConfiguration> bootstrap,
                       Namespace namespace,
                       HomieConfiguration homieConfiguration) throws Exception {
        rebuildDb(SqliteConnectionFactory.jdbiFrom(homieConfiguration.dbPath()));
    }

    public void rebuildDb(DBI jdbi) {
        try (Handle h = jdbi.open()) {
            h.execute("drop table if exists room");
            h.execute("drop table if exists appliance");
            h.execute("drop table if exists shape");
            h.execute("drop table if exists shapePoint");
            h.execute("drop table if exists roomAppliance");

            h.execute("create table room (roomId text primary key, name text not null)");

            h.execute("create table appliance (applianceId text primary key, name text not null, roomId text,"
                              + " foreign key(roomId) references room(roomId))"
            );

            h.execute("create table shape (shapeId integer primary key, roomId text,"
                              + " foreign key(roomId) references room(roomId))"
            );
            h.execute("create table shapePoint (shapePointId integer primary key,"
                              + " x real, y real, shapeId integer,"
                              + " foreign key(shapeId) references shape(shapeId))"
            );

            h.execute("create table roomAppliance (roomApplianceId integer, applianceId text not null, "
                              + " roomId text not null, x real not null, y real not null,"
                              + " foreign key(roomId) references room(roomId),"
                              + " foreign key(applianceId) references appliance(applianceId))"
            );
        }
    }
}
