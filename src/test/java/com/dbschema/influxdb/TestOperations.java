package com.dbschema.influxdb;

import com.influxdb.client.*;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class TestOperations {

    private InfluxDBClient influxDBClient;
    private final Properties prop = new Properties();

    @After
    public void closeConnection() {
        influxDBClient.close();
    }
    @Before
    public void testDriver() throws IOException {

        prop.load( new FileInputStream("gradle.properties"));

        this.influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", prop.getProperty("token").toCharArray(), prop.getProperty("org"), prop.getProperty("bucket"));

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        writeApi.writeRecord(WritePrecision.NS, "persons,firstname=John,lastname=Parker value=10.0");
        writeApi.writeRecord(WritePrecision.NS, "persons,firstname=Dan value=10.0");

    }


    /*
    This Test function gets the buckets for the given organisation
    Buckets are Analogous to schemas within an traditional RDBMS
    Org is similar in to a database in that its a group of Buckets
     */
    @Test
    public void listBuckets() throws SQLException {
        BucketsApi bucketApi = influxDBClient.getBucketsApi();
        List<Bucket> buckets = bucketApi.findBucketsByOrgName(prop.getProperty("org"));
        for (Bucket fluxTable : buckets) {
            System.out.printf("Bucket " + fluxTable.toString());
        }
        influxDBClient.close();
    }

    /*
    List measurements (similar with tables) and its columns
     */
    @Test
    public void listMeasurementAndFieldNames() throws SQLException {

        String bucket = prop.getProperty("bucket");
        QueryApi queryApi = influxDBClient.getQueryApi();

        String flux = "import \"influxdata/influxdb/schema\"\n" +
                "\n" +
                "schema.measurements(bucket: \"" + bucket + "\")";

        for (FluxTable fluxTable : queryApi.query(flux)) {
            for (FluxRecord fluxRecord : fluxTable.getRecords() ) {

                String measurement = String.valueOf( fluxRecord.getValueByKey("_value") );
                System.out.println("#### Measurement " + measurement);
                String flux2 = "import \"influxdata/influxdb/schema\"\n" +
                        "\n" +
                        "schema.measurementFieldKeys(bucket: \"" + bucket + "\", measurement: \"" + measurement + "\")";

                for (FluxTable fluxTable2 : queryApi.query(flux2)) {
                    for (FluxRecord fluxRecord2 : fluxTable2.getRecords()) {
                        String column = String.valueOf( fluxRecord2.getValueByKey("_value") );
                        System.out.println("  Column " + column );
                    }
                }
            }
        }
    }


    @Test
    public void testQuery() {
        String flux = "from(bucket:\"sample\") |> range(start: 0)";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            System.out.println("### Table " + fluxTable.toString());
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValues());
            }
        }

        influxDBClient.close();
    }

/*
    @Test
    public void getMeasurementAndFieldNames() throws SQLException {

        List<String> Measurements = getMeasurements("sample");

        for (String measurement : Measurements) {

            System.out.println("In the measurement " + measurement);
            System.out.println("There are the following column names" );
            List<String> colNames = getColumnNames("sample", measurement);
            for (String fluxRecord : colNames) {
                System.out.println(fluxRecord);
            }


        }
    }
    public List<String>  getMeasurements(String bucket) throws SQLException {
        String flux = "import \"influxdata/influxdb/schema\"\n" +
                "\n" +
                "schema.measurements(bucket: \"" + bucket + "\")";

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<String> values = new ArrayList<String>();
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {

            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                //this Flux query gets the fields used in the measurement
                //and then puts them in this value column

                values.add(fluxRecord.getValueByKey("_value").toString());
            }
        }
        return values;
    }

    public List<String> getColumnNames(String bucket, String measurement ) throws SQLException {
        String flux = "import \"influxdata/influxdb/schema\"\n" +
                "\n" +
                "schema.measurementFieldKeys(bucket: \"" + bucket + "\", measurement: \"" + measurement + "\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<String> values = new ArrayList<String>();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {

            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                //this Flux query gets the fields used in the measurement
                //and then puts them in this value column
                values.add(fluxRecord.getValueByKey("_value").toString());
            }
        }
        return values;
    }
*/

}
