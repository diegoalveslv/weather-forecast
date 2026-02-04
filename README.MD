## Weather Forecast

This is a sample API implemented for an assessment.

It contains one API `GET /forecast?zipcode=<zipcode>` that returns weather forecast information from open API sources.
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

The rationale for the libraries I used were these:

- ben-manes.caffeine: this is a direct upgrade from Guava's cache, which were one of the most popular. Caffeine is actively maintained
and supported by default in spring-boot-starter-cache. Since it's simple API, I decided to use a local in-memory cache over Redis for simplicity. In a real scenario where availability
is a must, I would make the application stateless and use an external in-memory cache.
- lombok: very common on java projects. I use mainly with @Slf4j and @RequiredArgsConstructor on beans to facilitate injection by
constructor, per spring best practices.
- wiremock-spring-boot: wiremock is a library to mock APIs with a real HTTP server. It integrates well with spring
and facilitate building end-to-end tests, or "sociable unit tests".