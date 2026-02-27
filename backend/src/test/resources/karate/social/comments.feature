Feature: Comments API Tests

  Background:
    * url socialUrl
    * def user1 = createTestUser('cmt1')
    * def user2 = createTestUser('cmt2')
    # Create a thread to comment on
    Given path '/threads'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Thread for comments' }
    When method post
    Then status 201
    * def threadId = response.data.id

  Scenario: Create a comment on a thread
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Great thread!' }
    When method post
    Then status 201
    And match response.data.id == '#notnull'
    And match response.data.threadId == threadId
    And match response.data.content == 'Great thread!'
    And match response.data.author.id == user2.userId

  Scenario: Create comment without auth returns 403
    Given path '/threads/' + threadId + '/comments'
    And request { content: 'No auth comment' }
    When method post
    Then status 403

  Scenario: Get comments for a thread
    # Create 2 comments
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Comment 1' }
    When method post
    Then status 201

    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Comment 2' }
    When method post
    Then status 201

    Given path '/threads/' + threadId + '/comments'
    When method get
    Then status 200
    And match response.data.totalElements == 2

  Scenario: Get comments on non-existent thread returns 404
    Given path '/threads/00000000-0000-0000-0000-000000000000/comments'
    When method get
    Then status 404

  Scenario: Edit own comment within edit window
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Original comment' }
    When method post
    Then status 201
    * def commentId = response.data.id

    Given path '/comments/' + commentId
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Edited comment' }
    When method put
    Then status 200
    And match response.data.content == 'Edited comment'

  Scenario: Edit another user's comment returns 403
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'User1 comment' }
    When method post
    Then status 201
    * def commentId = response.data.id

    Given path '/comments/' + commentId
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Hijack attempt' }
    When method put
    Then status 403

  Scenario: Delete own comment (soft delete)
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Comment to delete' }
    When method post
    Then status 201
    * def commentId = response.data.id

    Given path '/comments/' + commentId
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 200

  Scenario: Delete another user's comment returns 403
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Protected comment' }
    When method post
    Then status 201
    * def commentId = response.data.id

    Given path '/comments/' + commentId
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 403

  Scenario: Report a comment
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Bad comment' }
    When method post
    Then status 201
    * def commentId = response.data.id

    Given path '/comments/' + commentId + '/report'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { reason: 'Spam', details: 'This is spam' }
    When method post
    Then status 200

  Scenario: Mute and unmute a comment
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Noisy comment' }
    When method post
    Then status 201
    * def commentId = response.data.id

    # Mute
    Given path '/comments/' + commentId + '/mute'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 200

    # Mute again returns 409
    Given path '/comments/' + commentId + '/mute'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 409

    # Unmute
    Given path '/comments/' + commentId + '/mute'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 200

  Scenario: Thread comment count reflects new comments
    Given path '/threads/' + threadId + '/comments'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Counted comment' }
    When method post
    Then status 201

    Given path '/threads/' + threadId
    When method get
    Then status 200
    And match response.data.commentsCount == '#number'
    And assert response.data.commentsCount >= 1
