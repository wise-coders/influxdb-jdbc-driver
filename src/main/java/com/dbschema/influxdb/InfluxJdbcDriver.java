
package com.dbschema.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import java.sql.*;
import java.util.Properties;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InfluxJdbcDriver implements Driver
{

    public static final Logger LOGGER = Logger.getLogger( InfluxJdbcDriver.class.getName() );

    static {
        try {
            DriverManager.registerDriver( new InfluxJdbcDriver());
            LOGGER.setLevel(Level.SEVERE);
            final ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.FINEST);
            consoleHandler.setFormatter(new SimpleFormatter());

            LOGGER.setLevel(Level.FINEST);
            LOGGER.addHandler(consoleHandler);

            final FileHandler fileHandler = new FileHandler(System.getProperty("user.home") + "/.DbSchema/logs/InfluxJdbcDriver.log");
            fileHandler.setFormatter( new SimpleFormatter());
            LOGGER.addHandler(fileHandler);

        } catch ( Exception ex ){
            ex.printStackTrace();
        }
    }

    public static final String START_DAYS_KEY = "startdays";

    /**
     * Connect to the database using a URL like :
     * jdbc:mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
     * The URL excepting the jdbc: prefix is passed as it is to the MongoDb native Java driver.
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if ( url != null && acceptsURL( url )){

            int idx;
            if ( ( idx = url.lastIndexOf("?") ) > -1 ){
                for ( String pair : url.substring( idx ).split("&")){
                    String[] keyVal = pair.split("=");
                    String key = keyVal[0];
                    String val = keyVal[1];
                    if ( !info.containsKey( key )) {
                        info.put( key, val );
                    }
                }
            }

            String userName = ( info != null ? (String)info.get("user") : null );
            String password = ( info != null ? (String)info.get("password") : null );
            String token = ( info != null ? (String)info.get("token") : null );
            String startDaysStr = ( info != null ? (String)info.get(START_DAYS_KEY) : null );

            int startDays = -30;
            if ( startDaysStr != null ) {
                try {
                    startDays = Integer.parseInt(startDaysStr);
                    if ( startDays > 0 ) startDays = -1* startDays;
                } catch (NumberFormatException ex) {
                    System.out.println(ex);
                }
            }
            InfluxDBClient client;

            if (userName != null && password != null) {
                client = InfluxDBClientFactory.create(url, userName, password.toCharArray());
            } else if (token == null) {
                client = InfluxDBClientFactory.create( url );
            } else {
                client = InfluxDBClientFactory.create( url, token.toCharArray() );
            }

            return new InfluxConnection( client, startDays );
        }
        return null;
    }


    /**
     * URLs accepted are of the form: jdbc:mongodb[+srv]://<server>[:27017]/<db-name>
     *
     * @see java.sql.Driver#acceptsURL(java.lang.String)
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("http");
    }

    /**
     * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
    {
        return null;
    }

    /**
     * @see java.sql.Driver#getMajorVersion()
     */
    @Override
    public int getMajorVersion()
    {
        return 1;
    }

    /**
     * @see java.sql.Driver#getMinorVersion()
     */
    @Override
    public int getMinorVersion()
    {
        return 0;
    }

    /**
     * @see java.sql.Driver#jdbcCompliant()
     */
    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

}
