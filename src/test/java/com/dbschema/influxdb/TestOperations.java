package com.dbschema.influxdb;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public class TestOperations {

    private static char[] token = "Qzbb07b2GVs83F1jDs6ayP3hi37qyj8hufY6tWeUz9pt9nyGbXA-dpp68Rlbl7WxQLR9qMFpkqB1TGpBie49iA==".toCharArray();
    private static String org = "dbschema";
    private static String bucket = "sample";

    private InfluxDBClient influxDBClient;

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
