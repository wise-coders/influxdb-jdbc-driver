package com.wisecoders.influxdb;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Copyright Wise Coders GmbH https://wisecoders.com
 * Driver is used in the DbSchema Database Designer https://dbschema.com
 * Free to be used by everyone.
 * Code modifications allowed only to GitHub repository https://github.com/wise-coders/influxdb-jdbc-driver
 */

class AbstractTestCase {

    void printResultSet(ResultSet rs ) throws SQLException {
        boolean printHeader = true;
        while ( rs != null && rs.next()){
            if ( printHeader ){
                for ( int i = 1; i <= rs.getMetaData().getColumnCount(); i++ ){
                    System.out.print( rs.getMetaData().getColumnName( i ) + " ");
                }
                System.out.println();
                printHeader = false;
            }
            for ( int i = 1; i <= rs.getMetaData().getColumnCount(); i++ ){
                System.out.print( rs.getString( i ) + " ");
            }
            System.out.println();
        }
    }
}