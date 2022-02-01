package com.dbschema.influxdb;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class TestDriver extends AbstractTestCase {

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

        Statement stmt3=con.createStatement();
        printResultSet( stmt3.executeQuery("select * from sample" ) );
        stmt3.close();

        Statement stmt2=con.createStatement();
        printResultSet( stmt2.executeQuery("import \"influxdata/influxdb/schema\"\n\n  schema.keyFields(bucket: \"sample\")" ) );
        stmt2.close();

    }
}
