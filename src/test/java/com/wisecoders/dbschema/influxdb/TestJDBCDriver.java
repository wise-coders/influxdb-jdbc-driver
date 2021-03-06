package com.wisecoders.dbschema.influxdb;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Copyright Wise Coders GmbH https://wisecoders.com
 * Driver is used in the DbSchema Database Designer https://dbschema.com
 * Free to be used by everyone.
 * Code modifications allowed only to GitHub repository https://github.com/wise-coders/influxdb-jdbc-driver
 */

public class TestJDBCDriver extends AbstractTestCase {

    private Connection con;

    @Before
    public void setUp() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.wisecoders.dbschema.influxdb.JdbcDriver");

        final Properties properties = new Properties();
        properties.load(new FileInputStream("gradle.properties"));

        final String url = "http://localhost:8086?" + "token=" + URLEncoder.encode(properties.getProperty("token"), StandardCharsets.UTF_8) + "&org=" + properties.getProperty("org");
        con = DriverManager.getConnection(url, null, null);
    }

    @Test
    public void testListDatabases() throws Exception {

        Statement stmt = con.createStatement();
        printResultSet(stmt.executeQuery("influx org list"));
        stmt.close();

        Statement stmt1 = con.createStatement();
        printResultSet(stmt1.executeQuery("buckets()"));
        stmt1.close();

        Statement stmt2 = con.createStatement();
        printResultSet(stmt2.executeQuery(" import \"influxdata/influxdb/schema\"\n\n  schema.measurements(bucket: \"sample\")"));
        stmt2.close();

        Statement stmt3 = con.createStatement();
        printResultSet(stmt3.executeQuery("import \"influxdata/influxdb/schema\"\n\n  schema.fieldKeys(bucket: \"sample\")"));
        stmt3.close();

    }

    @Test
    public void testDatabaseMetaData() throws Exception {
        final List<String> schemas = new ArrayList<>();
        ResultSet rs = con.getMetaData().getSchemas();
        while (rs.next()) {
            String schemaName = rs.getString(1);
            schemas.add(schemaName);
            System.out.println("Schema " + schemaName);
        }
        rs.close();

        for (String schema : schemas) {
            System.out.println("Schema " + schema);
            ResultSet rsTables = con.getMetaData().getTables(null, schema, null, new String[]{"TABLE"});
            List<String> tables = new ArrayList<>();
            while (rsTables.next()) {
                String tableName = rsTables.getString(3);
                tables.add(tableName);
            }
            rsTables.close();

            for (String table : tables) {
                System.out.println("  Measurement " + table);
                ResultSet rsColumns = con.getMetaData().getColumns(null, schema, table, null);
                while (rsColumns.next()) {
                    System.out.println("   Column " + rsColumns.getString(4) + " type " + rsColumns.getString(6));
                }

                ResultSet rsIndexes = con.getMetaData().getIndexInfo(null, schema, table, false, true);
                while (rsIndexes.next()) {
                    System.out.println("   Index " + rsIndexes.getString(6) + " Column " + rsIndexes.getString(9));
                }
            }
        }
    }


    @Test
    public void testQuery() throws Exception {
        String flux = "from(bucket:\"sample\") |> range(start: 0)";
        Statement statement = con.createStatement();
        statement.execute(flux);
        ResultSet rs = statement.getResultSet();
        printResultSet(rs);
        statement.close();
    }
}
