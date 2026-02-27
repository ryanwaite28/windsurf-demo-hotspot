Feature: Thread Interactions API Tests

  Background:
    * url socialUrl
    * def user1 = createTestUser('int1')
    * def user2 = createTestUser('int2')
    # Create a thread to interact with
    Given path '/threads'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Thread for interactions' }
    When method post
    Then status 201
    * def threadId = response.data.id

  Scenario: Like a thread
    Given path '/threads/' + threadId + '/like'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    # Verify like count
    Given path '/threads/' + threadId
    When method get
    Then status 200
    And match response.data.likesCount == 1

  Scenario: Like thread twice returns 409
    Given path '/threads/' + threadId + '/like'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    Given path '/threads/' + threadId + '/like'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 409

  Scenario: Unlike a thread
    Given path '/threads/' + threadId + '/like'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    Given path '/threads/' + threadId + '/like'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 200

    # Verify like count back to 0
    Given path '/threads/' + threadId
    When method get
    Then status 200
    And match response.data.likesCount == 0

  Scenario: React to a thread
    Given path '/threads/' + threadId + '/react'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { reactionType: 'HEART' }
    When method post
    Then status 200

    # Verify reaction count
    Given path '/threads/' + threadId
    When method get
    Then status 200
    And match response.data.reactionsCount == 1

  Scenario: Change reaction type (upsert)
    Given path '/threads/' + threadId + '/react'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { reactionType: 'HEART' }
    When method post
    Then status 200

    # Change to FIRE
    Given path '/threads/' + threadId + '/react'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { reactionType: 'FIRE' }
    When method post
    Then status 200

    # Still only 1 reaction
    Given path '/threads/' + threadId
    When method get
    Then status 200
    And match response.data.reactionsCount == 1

  Scenario: Remove reaction
    Given path '/threads/' + threadId + '/react'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { reactionType: 'LAUGH' }
    When method post
    Then status 200

    Given path '/threads/' + threadId + '/react'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 200

    Given path '/threads/' + threadId
    When method get
    Then status 200
    And match response.data.reactionsCount == 0

  Scenario: Save and unsave a thread
    Given path '/threads/' + threadId + '/save'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    # Save again returns 409
    Given path '/threads/' + threadId + '/save'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 409

    # Unsave
    Given path '/threads/' + threadId + '/save'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 200

  Scenario: Report a thread
    Given path '/threads/' + threadId + '/report'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { reason: 'Inappropriate content', details: 'Contains offensive material' }
    When method post
    Then status 200

  Scenario: Mute and unmute a thread
    Given path '/threads/' + threadId + '/mute'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    # Mute again returns 409
    Given path '/threads/' + threadId + '/mute'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 409

    # Unmute
    Given path '/threads/' + threadId + '/mute'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 200

  Scenario: Interactions require authentication
    Given path '/threads/' + threadId + '/like'
    When method post
    Then status 403

    Given path '/threads/' + threadId + '/react'
    And request { reactionType: 'HEART' }
    When method post
    Then status 403

    Given path '/threads/' + threadId + '/save'
    When method post
    Then status 403
