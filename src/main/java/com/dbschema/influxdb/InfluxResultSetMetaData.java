package com.dbschema.influxdb;

import com.influxdb.query.FluxRecord;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Copyright Wise Coders GmbH https://wisecoders.com
 * Driver is used in the DbSchema Database Designer https://dbschema.com
 * Free to be used by everyone.
 * Code modifications allowed only to GitHub repository https://github.com/wise-coders/influxdb-jdbc-driver
 */

public class InfluxResultSetMetaData implements ResultSetMetaData {

    private final InfluxResultSet influxResultSet;
    private String[] columnNames;
    private Class[] columnClasses;

    public InfluxResultSetMetaData( InfluxResultSet influxResultSet ){
        this.influxResultSet = influxResultSet;
    }

    private void init(){
        if ( columnNames == null ){
            FluxRecord fluxRecord = influxResultSet.getOneFluxRecord();
            if ( fluxRecord != null ){
                columnNames = fluxRecord.getValues().keySet().toArray(new String[0]);
                columnClasses = new Class[ columnNames.length ];
                for ( int i = 0; i < columnNames.length; i++ ){
                    String columnName = columnNames[i];
                    Object value = fluxRecord.getValues().get( columnName );
                    if ( value != null ){
                        columnClasses[i] = value.getClass();
                    }
                }
            }
        }
    }


    @Override
    public int getColumnCount() throws SQLException {
        init();
        return columnNames != null ? columnNames.length : 0;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        init();
        if ( columnNames != null && column < columnNames.length ){
            return columnNames[column];
        }
        return null;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if ( columnClasses != null && column < columnClasses.length ){
            Class cls = columnClasses[column];
            if ( String.class == cls) return Types.VARCHAR;
            else if ( Double.class == cls) return Types.DOUBLE;
            else if ( Integer.class == cls) return Types.DOUBLE;
        }
        return Types.VARCHAR;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        if ( columnClasses != null && column < columnClasses.length ) {
            Class cls = columnClasses[column];
            return cls != null ? cls.getSimpleName() : "string";
        }
        return "string";
    }


    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return 0;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return null;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return null;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return null;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return null;
    }


    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
