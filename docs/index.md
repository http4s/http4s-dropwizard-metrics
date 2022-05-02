# http4s-dropwizard-metrics

```scala
libraryDependencies += "org.http4s" %% "http4s-dropwizard-metrics" % "@VERSION@"
```

## Server example

```scala mdoc:reset:silent
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.middleware.Metrics
import org.http4s.metrics.dropwizard.Dropwizard
import com.codahale.metrics.SharedMetricRegistries

val apiService = HttpRoutes.of[IO] {
  case GET -> Root / "api" =>
    Ok()
}

val registry = SharedMetricRegistries.getOrCreate("default")

val meteredRoutes = Metrics[IO](Dropwizard(registry, "server"))(apiService)
```

## Client example

```scala mdoc:reset:silent
import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.client.middleware.Metrics
import org.http4s.metrics.dropwizard.Dropwizard
import com.codahale.metrics.SharedMetricRegistries

val httpClient: Client[IO] = JavaNetClientBuilder[IO].create

val registry = SharedMetricRegistries.getOrCreate("default")
val requestMethodClassifier = (r: Request[IO]) => Some(r.method.toString.toLowerCase)

val meteredClient =
  Metrics[IO](Dropwizard(registry, "prefix"), requestMethodClassifier)(httpClient)
```
