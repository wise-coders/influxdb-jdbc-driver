# influxdb-jdbc-driver

I tested it against docker using

docker pull influxdb

docker run -d -p 8086:8086 --name influxdb2 -v /C/Temp:/var/lib/influxdb2 influxdb:latest

docker exec -it influxdb2 bash

influx setup --username dbschema --password dbschema --org dbschema --bucket sample

// THIS WILL LIST THE TOKEN REQUIRED TO LOGIN with the client tools

influx auth list --user dbschema --hide-headers | cut -f 3


https://github.com/influxdata/influxdb-client-java


## Import sample data

The following flux script will insert some sample data (more details here https://docs.influxdata.com/influxdb/v2.1/reference/sample-data/#noaa-sample-data) 

import "influxdata/influxdb/sample"

sample.data(set: "noaa")
  |> to(
      org: "dbschema",
      bucket: "sample"
  )
  
  This is how the script will look in the UI:
  
  ![image](https://user-images.githubusercontent.com/7541023/146947692-f5a709f0-8ecf-41e4-98b1-b3d4aab9a8c3.png)



# InfluxDB key concepts
Before working with InfluxDB it’s helpful to learn a few key concepts. Browse the topics below to learn more.

## InfluxDB data elements
InfluxDB structures data using elements such as timestamps, field keys, field values, tags, etc.
https://docs.influxdata.com/influxdb/v2.1/reference/key-concepts/data-elements/

## InfluxDB data schema
InfluxDB uses a tabular data schema for displaying raw data in Data Explorer and for returning query results in annotated CSV syntax.
https://docs.influxdata.com/influxdb/v2.1/reference/key-concepts/data-schema/

## InfluxDB design principles
Principles and tradeoffs related to InfluxDB design.
https://docs.influxdata.com/influxdb/v2.1/reference/key-concepts/design-principles/

