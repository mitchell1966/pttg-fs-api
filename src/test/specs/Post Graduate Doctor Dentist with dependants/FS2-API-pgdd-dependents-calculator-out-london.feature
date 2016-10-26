Feature: Total Funds Required Calculation - Tier 4 (General) Student Post Grad Doctor or Dentist out of London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate out of London borough = £1015 (the amount for when the student is studying in London)
    £680 per month per dependent and the same course length as a Tier 4 student – out of London
    Course length - this can be within the period of 1-2 months
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file
    Threshold =  (Required Maintenance funds doctorate not inner London
    borough (£1015 * remaining course length) + (required dependant maintenance funds * course length  * number of dependants) -  Accommodation fees already paid

    Tier 4 (General) Sudent - pgdd - out of London, In Country - (£1015 x 2) + (£680 x 2 x 1) - £0 = £3390
    Tier 4 (General) Sudent - pgdd - out of London, In Country - (£1015 x 1) + (£680 x 1 x 1) - £1000 = £695

    Scenario: Tony's Threshold calculated
    He is on a 1 month course
    He has 3 dependents
    No accommodation fees has been paid
    He is studying in Leeds

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd     |
            | In London                       | No       |
            | Course start date               | 2016-01-03|
            | Course end date                 | 2016-02-03|
            | Accommodation fees already paid | 0        |
            | dependants            | 3        |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 3055.00 |

    Scenario: Adam's Threshold calculated
    He is on a 1 month course
    He has 1 dependents
    He has pad £100 for his accommodation fees
    He is studying in Nottingham

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd     |
            | In London                       | No       |
            | Course start date               | 2016-01-03|
            | Course end date                 | 2016-02-03|
            | Accommodation fees already paid | 100.00   |
            | dependants            | 1        |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 1595.00 |


