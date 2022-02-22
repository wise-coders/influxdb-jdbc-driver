package com.wisecoders.dbschema.influxdb.resultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Copyright Wise Coders GmbH https://wisecoders.com
 * Driver is used in the DbSchema Database Designer https://dbschema.com
 * Free to be used by everyone.
 * Code modifications allowed only to GitHub repository https://github.com/wise-coders/influxdb-jdbc-driver
 */

public class OkResultSet extends ResultSetIterator {

    public OkResultSet(){
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return "Ok";
    }

    @Override
    public boolean next() throws SQLException {
        return false;
    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new ArrayResultSetMetaData("Result", new String[]{"map"},  new int[]{Types.JAVA_OBJECT},new int[]{300});
    }

}
