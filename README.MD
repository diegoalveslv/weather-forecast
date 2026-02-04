## Weather Forecast

This is a sample API implemented for an assessment.

It has one API `GET /forecast?zipcode=<zipcode>` that returns weather forecast information from open API sources.
Response header `X-Cache-Used=true|false` indicates the usage of cache.

### Build

Build and run with docker:

```bash
docker build -t weather-forecast-app .

docker run -p 8080:8080 weather-forecast-app
```

Then make a request:

```bash
curl --location 'http://localhost:8080/forecast?zipcode=05001200&country=BR' \
--header 'Cache-Control: no-cache'
```

Or run with gradlew:

```bash
./gradlew bootRun
```

### Considerations

Given time constrains and as this is a sample API, I glossed over a few things:

- Input validation and sanitization
- Rate limiting
- API documentation (could be done with OpenAPI)
- Proper error handling on external APIs
