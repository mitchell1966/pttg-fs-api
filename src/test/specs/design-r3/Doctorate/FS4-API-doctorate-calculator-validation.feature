Feature: Validation of the API fields and data


    Inner London borough - Yes or No options
    Course Length - 1-2 months
    Accommodation fees already paid - Format should not contain commas or currency symbols
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd
    Minimum Funds Required - Format should not contain commas or currency symbols
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)

######################### Validation on the Inner London borough Field #########################

    Scenario: The API is not provided with Inner London borough Yes or No field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            |           |
            | Course Length                   | 6         |
            | Accommodation fees already paid | 0         |
        Then the service displays the following result
            | HTTP Status    | 400                                  |
            | Status code    | 0000                                 |
            | Status message | Parameter error: Invalid innerLondon |

######################### Validation on the Course Length Field #########################

    Scenario: The API is not provided with the Course length
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   |           |
            | Accommodation fees already paid | 0         |
        Then the service displays the following result
            | HTTP Status    | 400                                   |
            | Status code    | 0000                                  |
            | Status message | Parameter error: Invalid courseLength |


    Scenario: The API is provided with incorrect Course Length - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   | x         |
            | Accommodation fees already paid | 0         |
        Then the service displays the following result
            | HTTP Status    | 400                                   |
            | Status code    | 0000                                  |
            | Status message | Parameter error: Invalid courseLength |

    Scenario: The API is provided with incorrect Course Length - more than 9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   | 10        |
            | Accommodation fees already paid | 0         |
        Then the service displays the following result
            | HTTP Status    | 400                                   |
            | Status code    | 0000                                  |
            | Status message | Parameter error: Invalid courseLength |


######################### Validation on the Tuition fees already paid Field #########################

    Scenario: The API is not provided with  tuition fees already paid -
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   | 6         |
            | Accommodation fees already paid | 0         |
        Then the service displays the following result
            | HTTP Status    | 400                                      |
            | Status code    | 0000                                     |
            | Status message | Parameter error: Invalid courseLength |



######################### Validation on the Accommodation fees already paid Field #########################

    Scenario: The API is not provided with Accommodation fees already paid
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   | 1         |
            | Accommodation fees already paid |           |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   | 1         |
            | Accommodation fees already paid | %%        |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |