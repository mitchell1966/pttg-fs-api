package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.MockBankService
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.domain.AccountStatusChecker

import java.time.LocalDate

import static TestUtils.getMessageSource
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class DailyBalanceServiceSpec extends Specification {


    def mockBankService = Mock(MockBankService)

    def dailyBalanceService = new DailyBalanceService(new AccountStatusChecker(mockBankService, 28), getMessageSource())
    MockMvc mockMvc = standaloneSetup(dailyBalanceService).setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter()).build()

    def "daily balance threshold check pass"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/12-34-56/12345678/dailybalancestatus"
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)

        def minimum = new BigDecimal(2500.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def lower = new BigDecimal(2560.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def upper = new BigDecimal(3500.00).setScale(2, BigDecimal.ROUND_HALF_UP)

        1 * mockBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.generateRandomBankResponseOK(fromDate, toDate, lower, upper, true, false)

        when:
        def response = mockMvc.perform(
            get(url)
                .param("toDate", toDate.toString())
                .param("minimum", minimum.toString())
                .param("fromDate", fromDate.toString())
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.pass == true

    }


    def "daily balance threshold check fail (minimum below threshold)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/12-34-56/12345678/dailybalancestatus"

        def lowestIndex = 5
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def lowestDate = toDate.minusDays(5)

        def minimum = new BigDecimal(2500.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def lower = new BigDecimal(2660.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def upper = new BigDecimal(3500.00).setScale(2, BigDecimal.ROUND_HALF_UP)

        def lowest = 1800.00


        1 * mockBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.generateDailyBalancesForFail(fromDate, toDate, lower, upper, lowest, lowestIndex)

        when:
        def response = mockMvc.perform(
            get(url)
                .param("toDate", toDate.toString())
                .param("minimum", minimum.toString())
                .param("fromDate", fromDate.toString())
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.pass == false
        jsonContent.failureReason.lowestBalanceValue == 1800.00
        jsonContent.failureReason.lowestBalanceDate == lowestDate.toString()

    }

    def "daily balance threshold check fail (not enough entries)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/12-34-56/12345678/dailybalancestatus"
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def mockFromDate =toDate.minusDays(26)

        def minimum = new BigDecimal(2500.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def lower = new BigDecimal(2660.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def upper = new BigDecimal(3500.00).setScale(2, BigDecimal.ROUND_HALF_UP)

        1 * mockBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.generateRandomBankResponseOK(mockFromDate, toDate, lower, upper, true, false)

        when:
        def response = mockMvc.perform(
            get(url)
                .param("toDate", toDate.toString())
                .param("minimum", minimum.toString())
                .param("fromDate", fromDate.toString())
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.pass == false
        jsonContent.failureReason.recordCount == 27
        jsonContent.failureReason.lowestBalanceValue == null
        jsonContent.failureReason.lowestBalanceDate == null

    }

}
