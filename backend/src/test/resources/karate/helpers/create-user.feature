@ignore
Feature: Helper - Create a test user and return auth info

  Scenario: Create user
    Given url authUrl + '/signup'
    And request signupPayload
    When method post
    Then status 201

    * def accessToken = response.data.accessToken
    * def refreshToken = response.data.refreshToken
    * def userId = response.data.user.id
    * def username = response.data.user.username
    * def email = response.data.user.email
