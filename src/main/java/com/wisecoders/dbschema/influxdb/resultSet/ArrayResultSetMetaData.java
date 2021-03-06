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

public class ArrayResultSetMetaData implements ResultSetMetaData
{

	private final String tableName;
    private final String[] columnNames;
    private final int[] javaTypes;
    private final int[] displaySizes;

	public ArrayResultSetMetaData(String tableName, String[] columnNames, int[] javaTypes, int[] displaySizes)
	{
		this.tableName = tableName;
		this.columnNames = columnNames;
		this.displaySizes = displaySizes;
        this.javaTypes = javaTypes;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException	{
		return false;
	}

	/**
	 * @see ResultSetMetaData#getColumnCount()
	 */
	@Override
	public int getColumnCount() throws SQLException
	{
		return this.columnNames.length;
	}

	/**
	 * @see ResultSetMetaData#isAutoIncrement(int)
	 */
	@Override
	public boolean isAutoIncrement(int column) throws SQLException
	{
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

	/**
	 * @see ResultSetMetaData#isNullable(int)
	 */
	@Override
	public int isNullable(int column) throws SQLException
	{
		return ResultSetMetaData.columnNoNulls;
	}

	/**
	 * @see ResultSetMetaData#isSigned(int)
	 */
	@Override
	public boolean isSigned(int column) throws SQLException
	{
		return true;
	}

	/**
	 * @see ResultSetMetaData#getColumnDisplaySize(int)
	 */
	@Override
	public int getColumnDisplaySize(int column) throws SQLException
	{
		return displaySizes[column - 1];
	}

	/**
	 * @see ResultSetMetaData#getColumnLabel(int)
	 */
	@Override
	public String getColumnLabel(int column) throws SQLException
	{
		return columnNames[column - 1];
	}

	/**
	 * @see ResultSetMetaData#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) throws SQLException	{
		return columnNames[column - 1];
	}

	/**
	 * @see ResultSetMetaData#getSchemaName(int)
	 */
	@Override
	public String getSchemaName(int column) throws SQLException
	{
		return null;
	}

	@Override
	public int getPrecision(int column) throws SQLException
	{
		return 0;
	}

	/**
	 * @see ResultSetMetaData#getScale(int)
	 */
	@Override
	public int getScale(int column) throws SQLException
	{
		return 0;
	}

	/**
	 * @see ResultSetMetaData#getTableName(int)
	 */
	@Override
	public String getTableName(int column) throws SQLException
	{
		return tableName;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		return null;
	}

	/**
	 * @see ResultSetMetaData#getColumnType(int)
	 */
	@Override
	public int getColumnType(int column) throws SQLException {
		return javaTypes[ column -1 ];
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException
	{
        switch ( javaTypes[column - 1] ){
            case Types.JAVA_OBJECT : return "map";
            default : return "varchar";
        }
	}

	/**
	 * @see ResultSetMetaData#isReadOnly(int)
	 */
	@Override
	public boolean isReadOnly(int column) throws SQLException
	{
		return false;
	}

	/**
	 * @see ResultSetMetaData#isWritable(int)
	 */
	@Override
	public boolean isWritable(int column) throws SQLException
	{
		return true;
	}

	/**
	 * @see ResultSetMetaData#isDefinitelyWritable(int)
	 */
	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException
	{
		return true;
	}

	/**
	 * @see ResultSetMetaData#getColumnClassName(int)
	 */
	@Override
	public String getColumnClassName(int column) throws SQLException
	{
		return "java.lang.String";
	}

}
