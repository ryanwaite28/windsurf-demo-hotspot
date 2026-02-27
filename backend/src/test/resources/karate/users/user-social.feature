Feature: User Social Interactions API Tests

  Background:
    * url usersUrl

  Scenario: Follow a user
    * def user1 = createTestUser('fol1')
    * def user2 = createTestUser('fol2')
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200
    And match response.success == true

  Scenario: Follow self returns 400
    * def user = createTestUser('folself')
    Given path '/' + user.userId + '/follow'
    And header Authorization = 'Bearer ' + user.accessToken
    When method post
    Then status 400

  Scenario: Follow same user twice returns 409
    * def user1 = createTestUser('foldup1')
    * def user2 = createTestUser('foldup2')
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 409

  Scenario: Unfollow a user
    * def user1 = createTestUser('unfol1')
    * def user2 = createTestUser('unfol2')
    # Follow first
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    # Then unfollow
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method delete
    Then status 200

  Scenario: Unfollow user not followed returns 404
    * def user1 = createTestUser('unfnf1')
    * def user2 = createTestUser('unfnf2')
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method delete
    Then status 404

  Scenario: Mute and unmute a user
    * def user1 = createTestUser('mute1')
    * def user2 = createTestUser('mute2')
    # Mute
    Given path '/' + user2.userId + '/mute'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    # Unmute
    Given path '/' + user2.userId + '/mute'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method delete
    Then status 200

  Scenario: Block a user removes follow relationships
    * def user1 = createTestUser('blk1')
    * def user2 = createTestUser('blk2')
    # user1 follows user2
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    # user1 blocks user2
    Given path '/' + user2.userId + '/block'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    # Verify follow is gone — unfollow should return 404
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method delete
    Then status 404

  Scenario: Unblock a user
    * def user1 = createTestUser('ublk1')
    * def user2 = createTestUser('ublk2')
    # Block
    Given path '/' + user2.userId + '/block'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    # Unblock
    Given path '/' + user2.userId + '/block'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method delete
    Then status 200

  Scenario: Cannot follow a user who blocked you
    * def user1 = createTestUser('blkfol1')
    * def user2 = createTestUser('blkfol2')
    # user2 blocks user1
    Given path '/' + user1.userId + '/block'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    # user1 tries to follow user2
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 400

  Scenario: Report a user
    * def user1 = createTestUser('rpt1')
    * def user2 = createTestUser('rpt2')
    Given path '/' + user2.userId + '/report'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { reason: 'Spam', details: 'Posting spam content repeatedly' }
    When method post
    Then status 200

  Scenario: Get followers list
    * def user1 = createTestUser('gfol1')
    * def user2 = createTestUser('gfol2')
    # user2 follows user1
    Given path '/' + user1.userId + '/follow'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    # Get user1's followers
    Given path '/' + user1.userId + '/followers'
    When method get
    Then status 200
    And match response.data.totalElements == 1

  Scenario: Get following list
    * def user1 = createTestUser('gfng1')
    * def user2 = createTestUser('gfng2')
    # user1 follows user2
    Given path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    # Get user1's following
    Given path '/' + user1.userId + '/following'
    When method get
    Then status 200
    And match response.data.totalElements == 1
