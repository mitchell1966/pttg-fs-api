package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.domain._
import uk.gov.digital.ho.proving.financialstatus.monitor.{Auditor, Timer}

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/maintenance"))
@ControllerAdvice
class ThresholdService @Autowired()(maintenanceThresholdCalculator: MaintenanceThresholdCalculator) extends Auditor with Timer {

  val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])

  val BIG_DECIMAL_SCALE = 2
  val courseLengthPattern = """^[0-9]$""".r
  val TEMP_ERROR_CODE: String = "0000"
  val headers = new HttpHeaders()

  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  logStartupInformation()

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@RequestParam(value = "studentType", required = false) studentType: String,
                         @RequestParam(value = "innerLondon") innerLondon: Boolean,
                         @RequestParam(value = "courseLength") courseLength: Int,
                         @RequestParam(value = "tuitionFees", required = false) tuitionFees: JBigDecimal,
                         @RequestParam(value = "tuitionFeesPaid", required = false) tuitionFeesPaid: JBigDecimal,
                         @RequestParam(value = "accommodationFeesPaid") accommodationFeesPaid: JBigDecimal
                        ): ResponseEntity[ThresholdResponse] = {

    val validatedStudentType = validateStudentType(studentType)

    timer("calculateThresholdForStudentType") {
      val auditMessage = s"calculateThreshold: validatedStudentType = $validatedStudentType, innerLondon = $innerLondon, " +
        s"courseLength = $courseLength, tuitionFees = $tuitionFees, tuitionFeesPaid = $tuitionFeesPaid, " +
        s"accommodationFeesPaid = $accommodationFeesPaid"
      audit(auditMessage) {
        calculateThresholdForStudentType(validatedStudentType, innerLondon, courseLength, tuitionFees, tuitionFeesPaid, accommodationFeesPaid)
      }
    }
  }

  def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus) = {
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def validateCourseLength(courseLength: Int, min: Int, max: Int) = {
    min <= courseLength && courseLength <= max
  }

  def validateTuitionFees(tuitionFees: JBigDecimal) = {
    tuitionFees != null && tuitionFees.compareTo(JBigDecimal.ZERO) > -1
  }

  def validateTuitionFeesPaid(tuitionFeesPaid: JBigDecimal) = {
    tuitionFeesPaid != null && tuitionFeesPaid.compareTo(JBigDecimal.ZERO) > -1
  }

  def validateAccommodationFeesPaid(accommodationFeesPaid: JBigDecimal) = {
    accommodationFeesPaid != null && accommodationFeesPaid.compareTo(JBigDecimal.ZERO) > -1
  }

  def validateStudentType(studentType: String) = {
    StudentType.getStudentType(studentType)
  }

  def setScale(value: JBigDecimal) = if (value != null) value.setScale(BIG_DECIMAL_SCALE, JBigDecimal.ROUND_HALF_UP) else value

  def calculateThresholdForStudentType(studentType: StudentType, innerLondon: Boolean, courseLength: Int,
                                       tuitionFees: JBigDecimal, tuitionFeesPaid: JBigDecimal, accommodationFeesPaid: JBigDecimal) = {
    val INVALID_COURSE_LENGTH = "Parameter error: Invalid courseLength"
    val INVALID_TUITION_FEES = "Parameter error: Invalid tuitionFees"
    val INVALID_TUITION_FEES_PAID = "Parameter error: Invalid tuitionFeesPaid"
    val INVALID_ACCOMMODATION_FEES_PAID = "Parameter error: Invalid accommodationFeesPaid"

    val INVALID_STUDENT_TYPE = "Parameter error: Invalid studentType"

    val OK = "OK"

    studentType match {

      case NonDoctorate =>
        val courseMinLength = 1
        val courseMaxLength = 9
        if (!validateCourseLength(courseLength, courseMinLength, courseMaxLength)) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST)
        } else if (!validateTuitionFees(setScale(tuitionFees))) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_TUITION_FEES, HttpStatus.BAD_REQUEST)
        } else if (!validateTuitionFeesPaid(setScale(tuitionFeesPaid))) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_TUITION_FEES_PAID, HttpStatus.BAD_REQUEST)
        } else if (!validateAccommodationFeesPaid(setScale(accommodationFeesPaid))) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST)
        } else {
          val thresholdResponse: ThresholdResponse = new ThresholdResponse(
            maintenanceThresholdCalculator.calculateNonDoctorate(innerLondon, courseLength,
              setScale(tuitionFees),
              setScale(tuitionFeesPaid),
              setScale(accommodationFeesPaid)),
            StatusResponse(HttpStatus.OK.toString, OK))
          new ResponseEntity[ThresholdResponse](thresholdResponse, HttpStatus.OK)
        }

      case Doctorate =>
        val courseMinLength = 1
        val courseMaxLength = 2
        if (!validateCourseLength(courseLength, courseMinLength, courseMaxLength)) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST)
        } else if (!validateAccommodationFeesPaid(setScale(accommodationFeesPaid))) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST)
        } else {
          val thresholdResponse: ThresholdResponse = new ThresholdResponse(
            maintenanceThresholdCalculator.calculateDoctorate(innerLondon, courseLength,
              setScale(accommodationFeesPaid)), StatusResponse(HttpStatus.OK.toString, OK))
          new ResponseEntity[ThresholdResponse](thresholdResponse, HttpStatus.OK)
        }
      case Unknown(unknownType) =>
        buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_STUDENT_TYPE + unknownType, HttpStatus.BAD_REQUEST)
    }
  }

  def logStartupInformation() = {
    LOGGER.info(maintenanceThresholdCalculator.parameters)
  }

}
