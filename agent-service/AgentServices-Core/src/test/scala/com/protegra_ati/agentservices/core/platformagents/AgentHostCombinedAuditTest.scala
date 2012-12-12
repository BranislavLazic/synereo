package com.protegra_ati.agentservices.core.platformagents

import com.protegra.agentservicesstore.extensions.StringExtensions._
import com.protegra.agentservicesstore.extensions.URIExtensions._
import com.protegra.agentservicesstore.extensions.URIExtensions._
import com.protegra.agentservicesstore.extensions.ResourceExtensions._
import java.util.UUID
import org.junit._
import Assert._
import com.protegra.agentservicesstore.usage.AgentKVDBScope._
import com.protegra.agentservicesstore.usage.AgentKVDBScope.acT._
import com.protegra_ati.agentservices.core.schema._
import java.net.URI
import com.protegra_ati.agentservices.core.messages._
import com.protegra_ati.agentservices.core.messages.content._
import com.protegra_ati.agentservices.core.schema._
import invitation._
import scala.util.Random

import org.specs2.mutable._
import org.specs2.time.Duration
import org.junit.runner._
import org.specs2.runner._
import com.protegra_ati.agentservices.core.events._
import java.net.URI
import com.protegra_ati.agentservices.core.schema.util._
import com.protegra_ati.agentservices.core._

class AgentHostCombinedAuditTest extends SpecificationWithJUnit
with Timeouts
{
  val jenUID = UUID.randomUUID()
  var cnxnJenJen = new AgentCnxnProxy(( "Jen" + jenUID.toString ).toURI, "CombinedTestUser", ( "Jen" + jenUID.toString ).toURI)

  val mikeUID = UUID.randomUUID()
  var cnxnMikeMike = new AgentCnxnProxy(( "Mike" + mikeUID.toString ).toURI, "CombinedTestUser", ( "Mike" + mikeUID.toString ).toURI)

  val cnxnUIStore = new AgentCnxnProxy(( "UI" + UUID.randomUUID().toString ).toURI, "", ( "Store" + UUID.randomUUID().toString ).toURI)
  val storeR = new AgentHostStorePlatformAgent
  val uiR = new AgentHostUIPlatformAgent
  AgentHostCombinedBase.setupPAs(storeR, uiR, cnxnUIStore)

  val eventKey = "content"
  AgentHostCombinedBase.setupIncrementalDisclosure(storeR, cnxnJenJen)

  val connJenMike = AgentHostCombinedBase.setupPersistedConnection(storeR, jenUID, mikeUID)
  storeR.updateDataById(cnxnJenJen, connJenMike)

//    storeR._cnxnUserSelfConnectionsList = List(cnxnJenJen, cnxnMikeMike)
    //  as soon as an new connection is stored, (esp. self connection) it is necessary to listen to this connection or like here to ALL selfconnections
    //  If no listening happened than connection as if would be "not visible"
//    storeR.listenForHostedCnxns()

  "Viewing a profile" should {
    "not return audit trail by composite search" in {
      skipped("issue 242 and verify audit is configured")
      val agentSessionId = UUID.randomUUID()

      AgentHostCombinedBase.setProfile(uiR, cnxnJenJen, agentSessionId, eventKey)
      //to create audit
      AgentHostCombinedBase.countProfile(uiR, cnxnJenJen, agentSessionId, eventKey) must be_==(1).eventually(10, TIMEOUT_EVENTUALLY)

      AgentHostCombinedBase.countAudit(uiR, cnxnJenJen, agentSessionId, eventKey) must be_==(0).eventually(5, TIMEOUT_EVENTUALLY)
    }

    //see if this can expose an intermittent 3 audit record write for 1 read
    "return audit trail by composite search" in {
      skipped("issue 242 and verify audit is configured")
      val agentSessionId = UUID.randomUUID()

//      AgentHostCombinedBase.setupIncrementalDisclosure(storeR, cnxnJenJen)
      storeR._cnxnUserSelfConnectionsList = List(cnxnJenJen, cnxnMikeMike)

      val connMikeJen = AgentHostCombinedBase.setupPersistedConnection(storeR, mikeUID, jenUID)

      AgentHostCombinedBase.setProfile(uiR, cnxnJenJen, agentSessionId, eventKey)
      //to create audit
      AgentHostCombinedBase.countProfile(uiR, connMikeJen.readCnxn, agentSessionId, eventKey) must be_==(1).eventually(5, TIMEOUT_EVENTUALLY)

      // countAudit(ui, cnxnJenSelf, agentSessionId, eventKey) must be_==(1).eventually(5, TIMEOUT_EVENTUALLY)
    }

    //see if this can expose an intermittent 3 audit record write for 1 read
    "return profile by composite search" in {
      skipped("issue 242 and verify audit is configured")
      val agentSessionId = UUID.randomUUID()

//      AgentHostCombinedBase.setupIncrementalDisclosure(storeR, cnxnJenJen)
      storeR._cnxnUserSelfConnectionsList = List(cnxnJenJen, cnxnMikeMike)

      val connMikeJen = AgentHostCombinedBase.setupPersistedConnection(storeR, mikeUID, jenUID)
      storeR.updateDataById(cnxnMikeMike, connMikeJen)

      AgentHostCombinedBase.setProfile(uiR, cnxnJenJen, agentSessionId, eventKey)

      //composite
      AgentHostCombinedBase.countCompositeProfile(uiR, cnxnMikeMike, agentSessionId, eventKey) must be_==(1).eventually(5, TIMEOUT_EVENTUALLY)

    }


  }
}
