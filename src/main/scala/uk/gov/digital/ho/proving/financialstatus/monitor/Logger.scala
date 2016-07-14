package uk.gov.digital.ho.proving.financialstatus.monitor

import org.slf4j.Logger

trait Loggable {

  val LOGGER: Logger

}

trait Auditor extends Loggable {

  val AUDIT_MARKER = "AUDIT:"
  final def audit[T](comment: String)(functionToAudit: => T): T = {
    val result = functionToAudit
    LOGGER.info(s"$AUDIT_MARKER $comment => ${result.toString}")
    result
  }

}

trait Timer extends Loggable {

  val TIMER_MARKER = "TIMER:"
  final def timer[T](comment: String)(functionToTime: => T): T = {
    val start = System.currentTimeMillis()
    LOGGER.debug(s"$TIMER_MARKER Started @ ${start} $comment")
    val result = functionToTime
    val end = System.currentTimeMillis()
    LOGGER.debug(s"$TIMER_MARKER Ended @ ${end}, time (ms) = ${end - start} $comment")
    result
  }

}
