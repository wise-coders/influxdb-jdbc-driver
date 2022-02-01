package com.dbschema.influxdb;

import java.sql.ResultSet;
import java.sql.SQLException;

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