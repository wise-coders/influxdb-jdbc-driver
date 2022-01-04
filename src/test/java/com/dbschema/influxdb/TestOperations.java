package com.dbschema.influxdb;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.*;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestOperations {

    //private static char[] token = "Qzbb07b2GVs83F1jDs6ayP3hi37qyj8hufY6tWeUz9pt9nyGbXA-dpp68Rlbl7WxQLR9qMFpkqB1TGpBie49iA==".toCharArray();

    //This is Ivan's Token
    private static char[] token = "5CbH7vMJ-tx6p68E4egEcKDLMCE0Nd64AYpS2J5zzDc68ddOQD0Thq4gXPUIUut__rVuo37-6MHRUZ3_4GwkHw==".toCharArray();
    private static String org = "dbschema";
    private static String bucket = "sample";

    private InfluxDBClient influxDBClient;


    @After
    public void closeConnection() throws SQLException {
        influxDBClient.close();
    }
    @Before
    public void testDriver() throws SQLException {

        this.influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        writeApi.writeRecord(WritePrecision.NS, "persons,firstname=Mihai value=10.0");
        writeApi.writeRecord(WritePrecision.NS, "persons,firstname=Dan value=10.0");

        /*
        // Write by Data Point
        Point point = Point.measurement("temperature")
                .addTag("location", "west")
                .addField("value", 55D)
                .time(Instant.now().toEpochMilli(), WritePrecision.MS);

        writeApi.writePoint(point);

        // Write by LineProtocol
        writeApi.writeRecord(WritePrecision.NS, "temperature,location=north value=60.0");

        // Write by POJO
        Temperature temperature = new Temperature();
        temperature.location = "south";
        temperature.value = 62D;
        temperature.time = Instant.now();

        writeApi.writeMeasurement( WritePrecision.NS, temperature);
        */

    }


    @Test
    public void test() throws SQLException {
        String flux = "from(bucket:\"sample\") |> range(start: 0)";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            System.out.printf("Table " + fluxTable.toString());
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValues());
            }
        }

        influxDBClient.close();
    }

    @Test
    /*
    This Test function gets the buckets for the given organisation
    Buckets are Analogous to schemas within an traditional RDBMS
    The org is similar in to a database in that its a group of Buckets

     */
    public void getBuckets() throws SQLException {


        BucketsApi bucketApi = influxDBClient.getBucketsApi();




        List<Bucket> buckets = bucketApi.findBucketsByOrgName(org);
        for (Bucket fluxTable : buckets) {
            System.out.printf("Bucket " + fluxTable.toString());
        }

        influxDBClient.close();



    }
    @Test
    /*
    This Test function gets the measurements and its columns for the sample bucket

     */
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

     //   influxDBClient.close();
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

      //  influxDBClient.close();
        return values;
    }



    @Measurement(name = "temperature")
    private static class Temperature {

        @Column(tag = true)
        String location;

        @Column
        Double value;

        @Column(timestamp = true)
        Instant time;
    }



}
