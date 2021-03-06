package com.mooveit.moonitor.principal.actors

import akka.actor.{ActorRef, Props}
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.domain.metrics.{MetricId, MetricConfiguration}
import com.mooveit.moonitor.principal.serialization.JacksonJsonSupport._
import com.mooveit.moonitor.principal.actors.Mastermind._
import spray.http.MediaTypes.`application/json`
import spray.routing.HttpServiceActor

class ConfigurationService(mastermind: ActorRef) extends HttpServiceActor {

  val crudService = respondWithMediaType(`application/json`) {
    pathPrefix("hosts" / Segment) { host =>
      pathEndOrSingleSlash {
        post {
          complete {
            mastermind ! StartHost(host)
            "Ok"
          }
        } ~
        delete {
          complete {
            mastermind ! StopHost(host)
            "Ok"
          }
        }
      } ~
      path("metrics") {
        pathEndOrSingleSlash {
          put {
            entity(as[MetricConfiguration]) { mconf =>
              complete {
                mastermind ! StartCollecting(host, mconf)
                "Ok"
              }
            }
          } ~
          delete {
            entity(as[MetricId]) { metricId =>
              complete {
                mastermind ! StopCollecting(host, metricId)
                "Ok"
              }
            }
          }
        }
      } ~
      path("alerts") {
        pathEndOrSingleSlash {
          put {
            entity(as[AlertConfiguration]) { aconf =>
              complete {
                mastermind ! StartWatching(host, aconf)
                "Ok"
              }
            }
          } ~
          delete {
            entity(as[MetricId]) { metricId =>
              complete {
                mastermind ! StopWatching(host, metricId)
                "Ok"
              }
            }
          }
        }
      }
    }
  }

  override def receive = runRoute(crudService)
}

object ConfigurationService {

  def props(mastermind: ActorRef) = Props(new ConfigurationService(mastermind))
}
