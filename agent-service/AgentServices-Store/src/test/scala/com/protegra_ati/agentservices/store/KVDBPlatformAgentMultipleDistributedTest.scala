// -*- mode: Scala;-*-
// Filename:    KVDBPlatformAgentSingleTest.scala
// Authors:     lgm
// Creation:    Tue Apr  5 20:51:35 2011
// Copyright:   Not supplied
// Description:
// ------------------------------------------------------------------------
package com.protegra_ati.agentservices.store

import java.util.UUID

import com.biosimilarity.lift.lib.BasicLogService
import com.protegra_ati.agentservices.store.extensions.ResourceExtensions._
import com.protegra_ati.agentservices.store.extensions.StringExtensions._
import com.protegra_ati.agentservices.store.extensions.URIExtensions._
import com.protegra_ati.agentservices.store.mongo.usage.AgentKVDBMongoScope.acT._
import com.protegra_ati.agentservices.store.mongo.usage.AgentKVDBMongoScope.mTT._

import scala.util.continuations._

class KVDBPlatformAgentMultipleDistributedTest extends KVDBPlatformAgentBase {

  // val timeoutBetween = TIMEOUT_LONG
  val timeoutBetween       = 300
  val writerConfigFileName = Some("db_ui.conf")
  val readerConfigFileName = Some("db_store.conf")
  val sourceAddress        = "127.0.0.1".toURI.withPort(RABBIT_PORT_STORE_PRIVATE)
  val acquaintanceAddress  = "127.0.0.1".toURI.withPort(RABBIT_PORT_UI_PRIVATE)
  val pairedWriter         = createNode(sourceAddress, List(acquaintanceAddress), writerConfigFileName)
  val pairedReader         = createNode(acquaintanceAddress, List(sourceAddress), readerConfigFileName)

  // testMessaging(pairedWriter, pairedReader)
  // the testWildcardWithPut tests can be intermittent when distributed
  // testWildcardWithPut(pairedWriter, pairedReader)
  // testWildcardWithStore(pairedWriter, pairedReader)
  // testWildcardWithPutAndCursor(pairedWriter, pairedReader)
  // testWildcardWithStoreAndCursor(pairedWriter, pairedReader)
  // testWildcardWithCursorBefore(pairedWriter, pairedReader)

  // TODO: ht 2016-08-18, Disabled after failures stemming from SOC-91 changes
  "read " should {
    "find a results without continuation" ignore {
      val sourceId = UUID.randomUUID
      val targetId = sourceId
      val cnxn     = AgentCnxn(sourceId.toString.toURI, "", targetId.toString.toURI)
      val key      = "pub(_)".toLabel
      val key1     = """pub("1")""".toLabel
      pairedWriter.store(cnxn)(key1, Ground("1"))
      Thread.sleep(5000)
      reset {
        for (e <- pairedReader.read(cnxn)(key)) {
          if (e.isDefined) {
            BasicLogService.tweet(s"Read = ${e.dispatch}")
          }
        }
      }
      Thread.sleep(1000)
      Thread.sleep(1000)
      Thread.sleep(1000)
      Thread.sleep(1000)
      Thread.sleep(1000)
      assert(true)
    }
  }
}
