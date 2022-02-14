package com.dbschema.influxdb;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestJDBCDriver extends AbstractTestCase {

    private Connection con;

    @Before
    public void setUp() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.dbschema.influxdb.InfluxJdbcDriver");

        final Properties properties = new Properties();
        properties.load( new FileInputStream("gradle.properties"));

        final String url = "http://localhost:8086?" + "token=" + properties.getProperty("token") + "&org=" + properties.getProperty("org");
        con = DriverManager.getConnection( url, null, null);
    }

    @Test
    public void testListDatabases() throws Exception{

        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("buckets()") );
        stmt.close();

        Statement stmt1=con.createStatement();
        printResultSet( stmt1.executeQuery(" import \"influxdata/influxdb/schema\"\n\n  schema.measurements(bucket: \"sample\")" ) );
        stmt1.close();

        Statement stmt2=con.createStatement();
        printResultSet( stmt2.executeQuery("import \"influxdata/influxdb/schema\"\n\n  schema.fieldKeys(bucket: \"sample\")" ) );
        stmt2.close();

    }

    @Test
    public void testDatabaseMetaData() throws Exception {
        final List<String> catalogs = new ArrayList<>();
        ResultSet rs = con.getMetaData().getCatalogs();
        while (rs.next()) {
            String catName = rs.getString(1);
            catalogs.add(catName);
            System.out.println("Catalog " + catName);
        }
        rs.close();

        for (String catalog : catalogs) {
            System.out.println("Catalog " + catalog);
            ResultSet rsTables = con.getMetaData().getTables(catalog, null, null, new String[]{"TABLE"});
            List<String> tables = new ArrayList<>();
            while (rsTables.next()) {
                String tableName = rsTables.getString(3);
                tables.add(tableName);
            }
            rsTables.close();

            for (String table : tables) {
                System.out.println("  Measurement " + table );
                ResultSet rsColumns = con.getMetaData().getColumns(catalog, null, table, null);
                while (rsColumns.next()) {
                    System.out.println("   Column " + rsColumns.getString(4));
                }

                ResultSet rsIndexes = con.getMetaData().getIndexInfo(catalog, null, table, false, true);
                while (rsIndexes.next()) {
                    System.out.println("   Index " + rsIndexes.getString(6) + " Column " + rsIndexes.getString(9));
                }
            }
        }
    }


}
