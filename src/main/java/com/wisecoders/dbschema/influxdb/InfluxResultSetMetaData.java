package com.wisecoders.dbschema.influxdb;

import com.influxdb.query.FluxRecord;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright  <a href="https://wisecoders.com">Wise Coders GmbH</a>
 * Driver is used in the  <a href="https://dbschema.com">DbSchema Database Designer</a>
 * Free to be used by everyone.
 * Code modifications allowed only to <a href="https://github.com/wise-coders/influxdb-jdbc-driver">GitHub repository</a>
 */

public class InfluxResultSetMetaData implements ResultSetMetaData {

    private final InfluxResultSet influxResultSet;
    private final List<String> columnNames = new ArrayList<>();
    private final Map<String,Class> columnClasses = new HashMap<>();

    public InfluxResultSetMetaData( InfluxResultSet influxResultSet ){
        this.influxResultSet = influxResultSet;
    }

    private void init(){
        FluxRecord fluxRecord = influxResultSet.getOneFluxRecord();
        if ( fluxRecord != null ){
            for ( String columnName : fluxRecord.getValues().keySet() ){
                if( !columnNames.contains( columnName )){
                    columnNames.add( columnName );
                }
                if ( !columnClasses.containsKey( columnName ) ){
                    Object value = fluxRecord.getValues().get( columnName );
                    if ( value != null ) columnClasses.put( columnName, value.getClass() );
                }
            }
        }
    }


    @Override
    public int getColumnCount() throws SQLException {
        init();
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        init();
        if ( column < columnNames.size() ){
            return columnNames.get(column);
        }
        return null;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if ( column < columnNames.size() ){
            Class cls = columnClasses.get( columnNames.get(column));
            if ( String.class == cls) return Types.VARCHAR;
            else if ( Double.class == cls) return Types.DOUBLE;
            else if ( Integer.class == cls) return Types.DOUBLE;
        }
        return Types.VARCHAR;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        if ( column < columnClasses.size() ) {
            Class cls = columnClasses.get( columnNames.get(column));
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
        return getColumnName( column );
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
