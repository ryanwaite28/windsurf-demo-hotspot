Feature: Feed API Tests

  Background:
    * url socialUrl

  Scenario: Feed returns threads from followed users
    * def user1 = createTestUser('feed1')
    * def user2 = createTestUser('feed2')
    * def user3 = createTestUser('feed3')

    # user1 follows user2
    Given url usersUrl
    And path '/' + user2.userId + '/follow'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method post
    Then status 200

    # user2 creates a thread
    Given url socialUrl
    And path '/threads'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Thread from user2 in feed' }
    When method post
    Then status 201

    # user3 creates a thread (user1 does NOT follow user3)
    Given path '/threads'
    And header Authorization = 'Bearer ' + user3.accessToken
    And request { content: 'Thread from user3 not in feed' }
    When method post
    Then status 201

    # user1 checks feed — should see user2's thread only
    Given path '/feed'
    And header Authorization = 'Bearer ' + user1.accessToken
    When method get
    Then status 200
    And match response.data.totalElements == 1
    And match response.data.content[0].content == 'Thread from user2 in feed'

  Scenario: Feed is empty when not following anyone
    * def user = createTestUser('feednone')
    Given path '/feed'
    And header Authorization = 'Bearer ' + user.accessToken
    When method get
    Then status 200
    And match response.data.totalElements == 0

  Scenario: Feed requires authentication
    Given path '/feed'
    When method get
    Then status 403

  Scenario: Feed reflects multiple followed users
    * def viewer = createTestUser('feedv')
    * def author1 = createTestUser('feeda1')
    * def author2 = createTestUser('feeda2')

    # viewer follows both authors
    Given url usersUrl
    And path '/' + author1.userId + '/follow'
    And header Authorization = 'Bearer ' + viewer.accessToken
    When method post
    Then status 200

    Given url usersUrl
    And path '/' + author2.userId + '/follow'
    And header Authorization = 'Bearer ' + viewer.accessToken
    When method post
    Then status 200

    # Each author posts a thread
    Given url socialUrl
    And path '/threads'
    And header Authorization = 'Bearer ' + author1.accessToken
    And request { content: 'Author1 thread' }
    When method post
    Then status 201

    Given path '/threads'
    And header Authorization = 'Bearer ' + author2.accessToken
    And request { content: 'Author2 thread' }
    When method post
    Then status 201

    # viewer checks feed
    Given path '/feed'
    And header Authorization = 'Bearer ' + viewer.accessToken
    When method get
    Then status 200
    And match response.data.totalElements == 2
