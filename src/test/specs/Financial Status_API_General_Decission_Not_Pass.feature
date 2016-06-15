Feature: Not Pass - Total Funds Required Calculation - Tier 4 (General) student (single current account and no dependants)

  Requirement to meet Tier 4 pass
  Applicant does not have required closing balance every day for a consecutive 28 day period from the date of the Maintenance End Date


  Scenario: Shelly is a general student and does not have sufficient financial funds

  Application Raised Date 1st of June
  She has < than the Total Fund Required of £2350 for the previous 28 days


    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | End date of 28-day period | 2016-06-01 |
      | Total funds required      | 2530.00    |
      | Sort code                 | 135609     |
      | Account number            | 23568498   |
    #  | Applicant Date of Birth     | Value |  *** Need to confirm with Barclay's ***


    Then The Financial Status API provides the following results:
      | HTTP Status               | 200        |
      | Minimum Above Threshold   | False      |
      | Total funds required      | 2530.00    |
      | Period Checked to         | 2016-05-04 |
      | End date of 28-day period | 2016-06-01 |
      | Sort code                 | 135609     |
      | Account number            | 23568498   |

  Scenario: Brian is general student and does not have sufficient financial funds

  Application Raised Date 4th of July
  He has < than the Total Funds Required of £2030 for the previous 28 days


    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:|
      | End date of 28-day period | 2016-07-01 |
      | Total funds required      | 2030.00    |
      | Sort code                 | 149302     |
      | Account number            | 01078911   |

     # | Applicant Date of Birth    |Value | *** Need to confirm with Barclay's ***

    Then The Financial Status API  provides the following results:
      | HTTP Status               | 200        |
      | Minimum Above Threshold   | False      |
      | Total funds required      | 2030.00    |
      | Period Checked to         | 2016-06-06 |
      | End date of 28-day period | 2016-07-04 |
      | Sort code                 | 149302     |
      | Account number            | 01078911   |


  Scenario: David is general student and does not have sufficient financial funds

  Application Raised Date 4th of July
  He has < than the Total Funds Required of £2537.48 for the previous 28 days


    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:|
      | End date of 28-day period | 2016-07-01 |
      | Total funds required      | 2537.48    |
      | Sort code                 | 139302     |
      | Account number            | 17926767   |

     # | Applicant Date of Birth    |Value | *** Need to confirm with Barclay's ***

    Then The Financial Status API  provides the following results:
      | HTTP Status               | 200        |
      | Minimum Above Threshold   | False      |
      | Total funds required      | 2537.48    |
      | Period Checked to         | 2016-06-06 |
      | End date of 28-day period | 2016-07-04 |
      | Sort code                 | 139302     |
      | Account number            | 17926767   |