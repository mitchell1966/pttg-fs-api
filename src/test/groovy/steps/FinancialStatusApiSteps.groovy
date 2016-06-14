package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.json.JSONObject

import static com.jayway.restassured.RestAssured.get

class FinancialStatusApiSteps {
    @Managed
    public Response resp
    String jsonAsString
    String dependants = ""
    String applicationRaisedDate
    String applicantDateOfBirth
    String accountNumber = ""
    String sortCode
    String courseLength
    String tutionFee
    String tutionFeePaid
    String accomodationFeePaid
    String threshold
    String days

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    def String getTableData(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        for (String s : tableKey) {

            if (s.equalsIgnoreCase("application raised date")) {
                applicationRaisedDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("appilcation date of birth")) {
                applicantDateOfBirth = entries.get(s)
            }

            if (s.equalsIgnoreCase("Account Number")) {
                accountNumber = entries.get(s)
            }

            if(s.equalsIgnoreCase("Sort Code")){
                sortCode = entries.get(s)
            }
            if(s.equalsIgnoreCase("course length")){
                courseLength = entries.get(s)
            }
            if(s.equalsIgnoreCase("tution fee")){
                tutionFee = entries.get(s)
            }
            if(s.equalsIgnoreCase("tution fee paid")){
                tutionFeePaid = entries.get(s)
            }
            if(s.equalsIgnoreCase("accomodation fee paid")){
                accomodationFeePaid = entries.get(s)
            }
            if(s.equalsIgnoreCase("threshold")){
                threshold = entries.get(s)
            }
            if(s.equalsIgnoreCase("days")){
                days = entries.get(s)
            }
        }

    }

    public String tocamelcase(String g) {
        StringBuilder sbl = new StringBuilder()

        String firstString
        String nextString
        String finalString = null
        char firstChar

        String[] f = g.split(" ")

        for (int e = 0; e < f.length; e++) {

            if (e == 0) {
                firstString = f[0].toLowerCase()
                sbl.append(firstString)

            }

            if (e > 0) {
                nextString = f[e].toLowerCase()
                firstChar = nextString.charAt(0)
                nextString = nextString.replaceFirst(firstChar.toString(), firstChar.toString().toUpperCase())
                sbl.append(nextString)
            }
            finalString = sbl.toString()

        }
        return finalString
    }

    public void validateJsonResult(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet();
        JSONObject json = new JSONObject(jsonAsString);

        Iterator<String> jasonKey = json.keys()

        while (jasonKey.hasNext()) {
            String Keys = jasonKey.next()
            if (Keys == "status") {
                assert entries.get("HTTP Status") == resp.getStatusCode().toString();
                break;
            }
            println "--------->" + Keys

            String jsonValue = json.get(Keys)

            println "===========>" + jsonValue

            JSONObject innerJson = new JSONObject(jsonValue);
            Iterator<String> innerJasonKey = innerJson.keys()

            while (innerJasonKey.hasNext()) {
                String keys2 = innerJasonKey.next()
                println "***********" + keys2
                //json.getJSONObject()
                String innerjsonValue =  innerJson.get(keys2).toString()
                println ">>>>>>>>>>>>>>>" + innerjsonValue
                for(String s: tableKey){
                    println ""+ entries.get(s)
                   assert entries.containsValue(innerjsonValue)

                }

            }

        }
    }

    @Given("^a Service is consuming Financial Status API\$")
    public void a_Service_is_consuming_Financial_Status_API() {

    }

    @When("^the Financial Status API is invoked with the following:\$")
    public void the_Financial_Status_API_is_invoked_with_the_following(DataTable arg1) {

        getTableData(arg1)
        resp = get("http://localhost:8080/incomeproving/v1/individual/dailybalancecheck/{sortCode}/{accountNumber}?applicationRaisedDate={applicationRaisedDate}&threshold={threshold}&days={days}",sortCode, accountNumber, applicationRaisedDate, threshold, days)
        jsonAsString = resp.asString()

        println ("Family Case Worker API: "+ jsonAsString)
    }

    @Then("^The Financial Status API provides the following results:\$")
    public void the_Financial_Status_API_provides_the_following_results(DataTable arg1) {
              validateJsonResult(arg1)
    }


}
