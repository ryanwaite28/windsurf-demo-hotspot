Feature: Auth API Tests

  Background:
    * url authUrl

  Scenario: Signup with valid data returns 201 and tokens
    * def unique = java.util.UUID.randomUUID().toString().substring(0, 8)
    Given path '/signup'
    And request { email: '#("signup_" + unique + "@test.com")', displayName: 'Signup User', username: '#("signup_" + unique)', password: 'Password123!', confirmPassword: 'Password123!' }
    When method post
    Then status 201
    And match response.success == true
    And match response.data.accessToken == '#notnull'
    And match response.data.refreshToken == '#notnull'
    And match response.data.tokenType == 'Bearer'
    And match response.data.user.email == '#notnull'
    And match response.data.user.username == '#notnull'

  Scenario: Signup with mismatched passwords returns 400
    * def unique = java.util.UUID.randomUUID().toString().substring(0, 8)
    Given path '/signup'
    And request { email: '#("mis_" + unique + "@test.com")', displayName: 'Mismatch User', username: '#("mis_" + unique)', password: 'Password123!', confirmPassword: 'Different456!' }
    When method post
    Then status 400

  Scenario: Signup with duplicate email returns 409
    * def user = createTestUser('dupemail')
    * def unique = java.util.UUID.randomUUID().toString().substring(0, 8)
    Given path '/signup'
    And request { email: '#(user.email)', displayName: 'Dup Email', username: '#("dupeml_" + unique)', password: 'Password123!', confirmPassword: 'Password123!' }
    When method post
    Then status 409

  Scenario: Signup with duplicate username returns 409
    * def user = createTestUser('dupuser')
    * def unique = java.util.UUID.randomUUID().toString().substring(0, 8)
    Given path '/signup'
    And request { email: '#("dup_" + unique + "@test.com")', displayName: 'Dup User', username: '#(user.username)', password: 'Password123!', confirmPassword: 'Password123!' }
    When method post
    Then status 409

  Scenario: Signup with invalid email returns 400
    Given path '/signup'
    And request { email: 'not-an-email', displayName: 'Bad Email', username: 'bademail1', password: 'Password123!', confirmPassword: 'Password123!' }
    When method post
    Then status 400

  Scenario: Login with valid credentials returns tokens
    * def user = createTestUser('login')
    Given path '/login'
    And request { email: '#(user.email)', password: 'Password123!' }
    When method post
    Then status 200
    And match response.success == true
    And match response.data.accessToken == '#notnull'
    And match response.data.refreshToken == '#notnull'
    And match response.data.user.email == user.email

  Scenario: Login with wrong password returns 400
    * def user = createTestUser('loginbad')
    Given path '/login'
    And request { email: '#(user.email)', password: 'WrongPassword!' }
    When method post
    Then status 400

  Scenario: Login with non-existent email returns 400
    Given path '/login'
    And request { email: 'nonexistent@test.com', password: 'Password123!' }
    When method post
    Then status 400

  Scenario: Refresh token returns new tokens
    * def user = createTestUser('refresh')
    Given path '/refresh'
    And request { refreshToken: '#(user.refreshToken)' }
    When method post
    Then status 200
    And match response.data.accessToken == '#notnull'
    And match response.data.refreshToken == '#notnull'

  Scenario: Logout returns success
    * def user = createTestUser('logout')
    Given path '/logout'
    And header Authorization = 'Bearer ' + user.accessToken
    When method post
    Then status 200
    And match response.success == true

  Scenario: Password reset request for existing user returns 200
    * def user = createTestUser('pwreset')
    Given path '/password-reset/request'
    And request { email: '#(user.email)' }
    When method post
    Then status 200
    And match response.success == true

  Scenario: Password reset request for non-existent email returns 404
    Given path '/password-reset/request'
    And request { email: 'nonexistent@test.com' }
    When method post
    Then status 404
