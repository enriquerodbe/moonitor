package com.mooveit.moonitor.common.domain

import akka.actor.ActorRef

import scala.concurrent.duration.FiniteDuration

case class AgentConfiguration(host: String,
                              repository: ActorRef,
                              frequency: FiniteDuration,
                              monitor: ActorRef)