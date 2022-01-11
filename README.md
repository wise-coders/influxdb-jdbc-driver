# Influx JDBC Driver

## Setup Test Environment
Run in docker

```
docker pull influxdb
docker run -d -p 8086:8086 --name influxdb2 -v /C/Temp:/var/lib/influxdb2 influxdb:latest
docker exec -it influxdb2 bash
influx setup --username dbschema --password dbschema --org dbschema --bucket sample
// THIS WILL LIST THE TOKEN REQUIRED TO LOGIN with the client tools
influx auth list --user dbschema --hide-headers | cut -f 3
```

https://github.com/influxdata/influxdb-client-java


## Import sample data

The following flux script will insert some sample data more details (here)[https://docs.influxdata.com/influxdb/v2.1/reference/sample-data/#noaa-sample-data]

```
import "influxdata/influxdb/sample"

sample.data(set: "noaa")
  |> to(
      org: "dbschema",
      bucket: "sample"
  )
```

This is how the script will look in the UI:
  
![image](https://user-images.githubusercontent.com/7541023/146947692-f5a709f0-8ecf-41e4-98b1-b3d4aab9a8c3.png)



# Documentation
[Data Elements](https://docs.influxdata.com/influxdb/v2.1/reference/key-concepts/data-elements/)

[Data Schema](https://docs.influxdata.com/influxdb/v2.1/reference/key-concepts/data-schema/)

[Design](https://docs.influxdata.com/influxdb/v2.1/reference/key-concepts/design-principles/)


## Influx is a Timeseries Database

Influx is a timeseries database, so if you are going to store information it would have to be the stats over a period of time. 
So we will consider as example a 'clients' measurement (table), where we save the clients of a shop, and the bill value.


Instant today = Instant.now();
Instant yesterday = today.minus(Period.ofDays(1));
Instant daybefore = yesterday.minus(Period.ofDays(1));

List<Point> pointsToAdd = new ArrayList<>();
pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Peter").addTag("lastName", "Johnson").addField("value", 52D).time(daybefore, WritePrecision.S));
pointsToAdd.add(Point.measurement("clients").addTag("firstName", "Bill").addTag("lastName", "Coulam").addField("value", 22D).time(daybefore, WritePrecision.S));

writeApi.writePoints(pointsToAdd);

If you were to think of this data in a normal RDBMS it would come in a table like this:


| Date | FirstName | LastName | bill |
|---|---|---|---|
| 9 Jan 2022 | Peter | Johnson | 52 |
| 9 Jan 2022 | Bill | Coulam | 22 |

However Influx stores its data as a point in time with associated tags (2 of those tags being field name and measurement). 
  
So the data actually comes out like this:

### Foreign keys

Influx doesn’t really support this concept. Although you can join tables based on tags, if you are doing this its usually very ineffcient and you should consider your schema design 


### Indexes

Searching data by tags is fast

This is because of the tag “location” the flux language splits the data into tables on each change of the combination of tags

You should be very careful with the combination of tags, ensure they are limited to avoid high cardinality:
[avoid high cardinality](https://docs.influxdata.com/influxdb/cloud/write-data/best-practices/resolve-high-cardinality/)

If you have come from a RDBMS background the simpilist way to imaging the difference between fields and tags is to see the field as a column and a tag as a column that is indexed. You can learn more about querying [by fields and tags](https://docs.influxdata.com/influxdb/cloud/query-data/flux/query-fields/#filter-by-fields-and-tags)
  

??? 
