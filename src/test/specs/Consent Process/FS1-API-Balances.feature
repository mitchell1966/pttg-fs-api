Feature: Outgoing API request to the Barclays Balances API and handling the incoming responses

    Sort code – 6 digits – sort code of the applicants current account
    Account number – 8 digits – account number of the applicants current account an Account number will be padded with leading zeroes to ensure it to be 8 digits
    Date of Birth of the applicant in DD-MMM-YYYY format
    User ID – the unique identifier of the user
    From Date - start date of data request in DD-MMM-YYYY format
    To Date - end date of data request in DD-MMM-YYYY format

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the service is consuming the Barclays Balances API
#        When the Financial Status API is invoked with the following:
#            | Sort code      | 111111   |
#            | Account number | 00001001   |
#            | Date of birth  | 1987-03-25 |
#            | User ID        | 12345      |
#            | Minimum        | 1234       |
#            | From Date      | 2016-05-03 |
#            | To Date        | 2016-05-30 |

    Scenario: Balances API request and consent has not been granted

        Given the test data for account 00001001
        Given the applicant has not granted consent
        When the Financial Status API is invoked with the following:
            | Sort code      | 111111   |
            | Account number | 00001001   |
            | Date of birth  | 1987-03-25 |
            | User ID        | 12345      |
            | Minimum        | 1234       |
            | From Date      | 2016-05-03 |
            | To Date        | 2016-05-30 |
        Then the Barclays Consent API provides the following error response:
            |Response Description| No user consent for sort code 222223 and account number 00001001 |
        |     Response Code               |          403                                                        |

    Scenario: Balances API request and consent has expired (e.g. greater than 24 hours)

        Given the consent request has expired
        When the Balances API is invoked
        Then The Barclays Consent API provides the following error response:
            |Response Description| No user consent for sort code 222223 and account number 22222223 |
            |     Response Code               |          403                                                        |
