# influxdb-jdbc-driver

I tested it against docker using

docker pull influxdb

docker run -d -p 8086:8086 --name influxdb2 -v /C/Temp:/var/lib/influxdb2 influxdb:latest
docker exec -it influxdb2 bash
influx setup --username dbschema --password dbschema --org dbschema --bucket sample
// THIS WILL LIST THE TOKEN REQUIRED TO LOGIN
influx auth list --user dbschema --hide-headers | cut -f 3


https://github.com/influxdata/influxdb-client-java



