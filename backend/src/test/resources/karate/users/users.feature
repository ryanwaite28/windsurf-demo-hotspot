Feature: User Profile API Tests

  Background:
    * url usersUrl

  Scenario: Get user profile by username (public, no auth required)
    * def user = createTestUser('profile')
    Given path '/' + user.username
    When method get
    Then status 200
    And match response.success == true
    And match response.data.username == user.username
    And match response.data.displayName == '#notnull'
    And match response.data.followersCount == 0
    And match response.data.followingCount == 0

  Scenario: Get non-existent user returns 404
    Given path '/nonexistent_user_999'
    When method get
    Then status 404

  Scenario: Update own profile
    * def user = createTestUser('update')
    Given path '/me'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { displayName: 'Updated Name', bio: 'My new bio', location: 'New York' }
    When method put
    Then status 200
    And match response.data.displayName == 'Updated Name'
    And match response.data.bio == 'My new bio'
    And match response.data.location == 'New York'

  Scenario: Update profile without auth returns 403
    Given path '/me'
    And request { displayName: 'Should Fail' }
    When method put
    Then status 403

  Scenario: Update profile with duplicate username returns 409
    * def user1 = createTestUser('upd1')
    * def user2 = createTestUser('upd2')
    Given path '/me'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { username: '#(user2.username)' }
    When method put
    Then status 409

  Scenario: Get privacy settings
    * def user = createTestUser('priv')
    Given path '/me/settings/privacy'
    And header Authorization = 'Bearer ' + user.accessToken
    When method get
    Then status 200
    And match response.data.profileVisibility == '#notnull'

  Scenario: Update privacy settings
    * def user = createTestUser('privupd')
    Given path '/me/settings/privacy'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { profileVisibility: 'FOLLOWERS', postsVisibility: 'FOLLOWERS' }
    When method put
    Then status 200
    And match response.data.profileVisibility == 'FOLLOWERS'
    And match response.data.postsVisibility == 'FOLLOWERS'

  Scenario: Get security settings
    * def user = createTestUser('sec')
    Given path '/me/settings/security'
    And header Authorization = 'Bearer ' + user.accessToken
    When method get
    Then status 200
    And match response.data.twoFactorEnabled == false

  Scenario: Update security settings
    * def user = createTestUser('secupd')
    Given path '/me/settings/security'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { twoFactorEnabled: true }
    When method put
    Then status 200
    And match response.data.twoFactorEnabled == true

  Scenario: Delete own account
    * def user = createTestUser('del')
    Given path '/me'
    And header Authorization = 'Bearer ' + user.accessToken
    When method delete
    Then status 200
    And match response.success == true

    # Verify user is now not found
    Given path '/' + user.username
    When method get
    Then status 404
