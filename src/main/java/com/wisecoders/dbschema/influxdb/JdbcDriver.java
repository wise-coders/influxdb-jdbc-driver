
package com.wisecoders.dbschema.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;
import java.util.logging.*;

/**
 * Copyright  <a href="https://wisecoders.com">Wise Coders GmbH</a>
 * Driver is used in the  <a href="https://dbschema.com">DbSchema Database Designer</a>
 * Free to be used by everyone.
 * Code modifications allowed only to <a href="https://github.com/wise-coders/influxdb-jdbc-driver">GitHub repository</a>
 */

public class JdbcDriver implements Driver
{

    public static final Logger LOGGER = Logger.getLogger( JdbcDriver.class.getName() );

    static {
        try {
            final File logsFile = new File("~/.DbSchema/logs/");
            if ( !logsFile.exists()) {
                logsFile.mkdirs();
            }

            DriverManager.registerDriver( new JdbcDriver());
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

    public static final String DAYS = "days";

    /**
     * Connect to the database using a URL like :
     * https://{HOST}:{PORT}?org={DB}&token={PARAM}&days={PARAM2}
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if ( url != null && acceptsURL( url )){

            int idx;
            if ( ( idx = url.lastIndexOf("?") ) > -1 ){
                for ( String pair : url.substring( idx + 1 ).split("&")){
                    int idxEq = pair.indexOf("=") ;
                    if ( idxEq > 1 ) {
                        String key = pair.substring(0, idxEq);
                        String val = URLDecoder.decode( pair.substring(idxEq+1), StandardCharsets.UTF_8 );
                        if (!info.containsKey(key)) {
                            info.put(key, val);
                            LOGGER.log(Level.INFO, "Param " + key + "=" + val );
                        }
                    }
                }
            }

            String userName = ( info != null ? (String)info.get("user") : null );
            String password = ( info != null ? (String)info.get("password") : null );
            String token = ( info != null ? (String)info.get("token") : null );
            String org = ( info != null ? (String)info.get("org") : null );
            String startDaysStr = ( info != null ? (String)info.get(DAYS) : null );

            LOGGER.log( Level.INFO, "Connection URL=" + url + " user=" + userName  + " password=" + password + " org=" + org +" token=" + token  + " days=" + startDaysStr );

            int startDays = -30;
            if ( startDaysStr != null && !startDaysStr.isEmpty()) {
                try {
                    startDays = Integer.parseInt(startDaysStr);
                    if ( startDays > 0 ) startDays = -1* startDays;
                    LOGGER.log(Level.INFO, "Use days=" + startDays);
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.SEVERE, "Cannot parse parameter 'days'.", ex);
                }
            }
            InfluxDBClient client;

            if (userName != null && password != null) {
                client = InfluxDBClientFactory.create(url, userName, password.toCharArray());
            } else if (token == null) {
                client = InfluxDBClientFactory.create( url );
            } else {
                client = InfluxDBClientFactory.create( url, token.toCharArray(), org );
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
