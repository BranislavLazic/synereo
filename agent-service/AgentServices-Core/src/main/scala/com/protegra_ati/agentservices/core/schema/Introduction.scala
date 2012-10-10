package com.protegra_ati.agentservices.core.schema

import com.protegra.agentservicesstore.AgentTS.acT._
import com.protegra_ati.agentservices.core.schema._

import java.util.UUID
import scala.reflect.BeanProperty

case class Introduction(@BeanProperty var alias:String, @BeanProperty var brokerCnxn:AgentCnxnProxy)extends Data {
  def this() = this ("", null)
}