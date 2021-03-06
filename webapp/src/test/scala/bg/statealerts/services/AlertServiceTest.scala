package bg.statealerts.services

import scala.collection.JavaConversions.seqAsJavaList
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional
import bg.statealerts.model.Alert
import bg.statealerts.model.User
import bg.statealerts.util.TestProfile
import javax.persistence.Entity
import javax.inject.Inject
import bg.statealerts.model.AlertPeriod._
import org.junit.Assert
import org.hamcrest.CoreMatchers
import org.joda.time.DateTime
import org.apache.commons.lang3.ArrayUtils
import scala.collection.mutable.ListBuffer
import bg.statealerts.model.AlertTrigger

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(Array("classpath*:/applicationContext.xml"))
@ActiveProfiles(Array("test-profile"))
//TODO: use scalatest
class AlertServiceTest {

  @Inject var service: AlertService = _
  @Inject var userService: UserService = _

  @Test
  def test() {
    val user1 = userService.completeUserRegistration(null, null, null,  false)
    val user2 = userService.completeUserRegistration(null, null, null,  false)

    val alert1 = new Alert()
    alert1.name = "alert 1"
    alert1.keywords = "word"
    alert1.period = Daily.toString
    service.saveAlert(alert1, user1)

    val alert2 = new Alert()
    alert2.name = "alert 2"
    alert2.period = Daily.toString
    alert2.keywords = "word"
    service.saveAlert(alert2, user2)

    val all = ListBuffer[(Alert, AlertTrigger)]()
    service.performBatched(DateTime.now.plusDays(1).plusSeconds(10)) {
      (alert: Alert, trigger: AlertTrigger) =>
        val e = (alert, trigger)
        all += e
    }
     
    Assert.assertEquals(2, all.size)
    val executionKeywords1 = all(0)._1.keywords
    val executionKeywords2 = all(1)._1.keywords
    Assert.assertTrue(executionKeywords1.contains("word"))
    Assert.assertTrue(executionKeywords2.contains("word"))

  }
}