/*
 * Copyright 2013 http4s.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.http4s.blaze

import cats.effect._
import com.codahale.metrics.{Timer => _, _}
import com.comcast.ip4s._
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.metrics.dropwizard._
import org.http4s.server.HttpMiddleware
import org.http4s.server.Router
import org.http4s.server.Server
import org.http4s.server.middleware.Metrics

class EmberMetricsExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    EmberMetricsExampleApp.resource.use(_ => IO.never).as(ExitCode.Success)
}

object EmberMetricsExampleApp {
  def httpApp: HttpApp[IO] = {
    val metricsRegistry: MetricRegistry = new MetricRegistry
    val metrics: HttpMiddleware[IO] = Metrics[IO](Dropwizard(metricsRegistry, "server"))

    val apiService = HttpRoutes.of[IO] { case GET -> Root / "api" =>
      Ok()
    }

    Router(
      "/http4s" -> metrics(apiService),
      "/http4s/metrics" -> metricsService[IO](metricsRegistry),
    ).orNotFound
  }

  def resource: Resource[IO, Server] = {
    val app = httpApp
    EmberServerBuilder
      .default[IO]
      .withPort(port"8080")
      .withHttpApp(app)
      .build
  }
}
