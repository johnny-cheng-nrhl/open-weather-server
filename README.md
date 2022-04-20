# open-weather-server
Http server that exposes the REST APIs for Open Weather API (one-call)

## Usage

### Running

Before running, a valid open-weather-api key would be needed. Replace 
anchor _{PROVIDE-OPEN-WEATHER-KEY}_ in _Server.scala_.

To start the server, first open the sbt console:
```
sbt run
```

To call the local server with curl 

```
curl -X GET  "http://localhost:8080/forecast?lat=41.40338&lon=2.1740"
```
OR

Use latitude and longitude together separated by a comma 
``` 
curl -X GET  "http://localhost:8080/forecast?latLong=41.4033,2.1740"
```

The response would look something like this:

```
{"lat":41.4034,"lon":2.174,"timezone":"Europe/Madrid","timezone_offset":7200,"current":{"temp":59.77,"feels_like":58.78,"humidity":71,"weather":[{"main":"Clouds","description":"broken clouds"}]},"alerts":[{"event":"Moderate coastalevent warning","description":"Viento del nordeste fuerza 7 y olas de 3 metros"},{"event":"Moderate rain warning","description":"One-hour accumulated precipitation: 20 mm. "}]}
```

### Notes: 
- The latitude and longitude from request would be returned in the response as well. 
- Based on the geographic location, "temp" and "feel_like" inside "current" node provides the temperature.
- Weather condition will be shown in the "weather" node with main and description.
- Any weather alert, if exist, will be shown in the "alert" list node.


### Unit Tests

Run unit tests with:

```
sbt test
```
(or simply `test` from the sbt console).
