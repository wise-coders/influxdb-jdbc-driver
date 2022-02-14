package com.dbschema.influxdb;

import com.influxdb.client.*;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestQueryApi {

    private InfluxDBClient influxDBClient;
    private final Properties prop = new Properties();

    @After
    public void closeConnection() {
        influxDBClient.close();
    }

    @Before
    public void prepareData() throws IOException {
        prop.load( new FileInputStream("gradle.properties"));

        this.influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", prop.getProperty("token").toCharArray(), prop.getProperty("org"), prop.getProperty("bucket"));

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        // where we save the clients of a shop, and the bill value.
        Instant today = Instant.now();
        Instant yesterday = today.minus(Period.ofDays(1));
        Instant daybefore = yesterday.minus(Period.ofDays(1));

        try { influxDBClient.getBucketsApi().deleteBucket("clients"); } catch ( Throwable ex ){}


        List<Point> pointsToAdd = new ArrayList<>();
        pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Peter").addTag("lastName", "Smithson").addField("bill", 52.00).time(daybefore, WritePrecision.S));
        pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Bill").addTag("lastName", "Coulam").addField("bill", 22.45).time(daybefore, WritePrecision.S));
        pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Peter").addTag("lastName", "Johnson").addField("bill", 57.67).time(yesterday, WritePrecision.S));
        pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Bill").addTag("lastName", "Coulam").addField("bill", 34.22).time(yesterday, WritePrecision.S));
        pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Jayne").addTag("lastName", "Johnson").addField("bill", 51.34).time(today, WritePrecision.S));
        pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Bill").addTag("lastName", "Coulam").addField("bill", 22.12).time(today, WritePrecision.S));
        writeApi.writePoints("clients",prop.getProperty("org"), pointsToAdd);


        List<Point> pointsToAdd2 = new ArrayList<>();
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "west").addField("value", 52D).time(daybefore, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "north").addField("value", 60D).time(daybefore, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "south").addField("value", 62D).time(daybefore, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "west").addField("value", 55D).time(yesterday, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "north").addField("value", 61D).time(yesterday, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "south").addField("value", 66D).time(yesterday, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "west").addField("value", 56D).time(today, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "north").addField("value", 67D).time(today, WritePrecision.S));
        pointsToAdd2.add(Point.measurement("temperature").addTag("location", "south").addField("value", 63D).time(today, WritePrecision.S));

        writeApi.writePoints(pointsToAdd2);

    }
    /**
        This Test function gets the buckets for the given organisation
        Buckets are Analogous to schemas within an traditional RDBMS.
        Org is similar in to a database in that its a group of Buckets
     */
    @Test
    public void listBuckets() throws SQLException {
        BucketsApi bucketApi = influxDBClient.getBucketsApi();
        List<Bucket> buckets = bucketApi.findBucketsByOrgName(prop.getProperty("org"));
        for (Bucket bucket : buckets) {
            System.out.printf("Bucket " + bucket.toString());
        }
        influxDBClient.close();
    }

    /**
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


}
