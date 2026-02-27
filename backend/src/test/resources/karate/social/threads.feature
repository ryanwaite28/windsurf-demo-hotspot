Feature: Thread CRUD API Tests

  Background:
    * url socialUrl

  Scenario: Create a thread with text content
    * def user = createTestUser('thr1')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Hello world! This is my first thread.' }
    When method post
    Then status 201
    And match response.success == true
    And match response.data.id == '#notnull'
    And match response.data.content == 'Hello world! This is my first thread.'
    And match response.data.threadType == 'ORIGINAL'
    And match response.data.author.id == user.userId
    And match response.data.pinned == false
    And match response.data.likesCount == 0
    And match response.data.commentsCount == 0

  Scenario: Create a thread with media
    * def user = createTestUser('thrmedia')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Check out this photo!', media: [{ mediaType: 'IMAGE', url: 'https://example.com/photo.jpg' }] }
    When method post
    Then status 201
    And match response.data.media == '#[1]'
    And match response.data.media[0].mediaType == 'IMAGE'
    And match response.data.media[0].url == 'https://example.com/photo.jpg'

  Scenario: Create a thread with poll
    * def user = createTestUser('thrpoll')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'What do you prefer?', poll: { question: 'Cats or dogs?', options: ['Cats', 'Dogs', 'Both'], expiresInHours: 24 } }
    When method post
    Then status 201
    And match response.data.poll != null
    And match response.data.poll.question == 'Cats or dogs?'
    And match response.data.poll.options == '#[3]'
    And match response.data.poll.options[0].optionText == 'Cats'
    And match response.data.poll.options[1].optionText == 'Dogs'
    And match response.data.poll.options[2].optionText == 'Both'

  Scenario: Create thread without content, media, or poll returns 400
    * def user = createTestUser('thrempty')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request {}
    When method post
    Then status 400

  Scenario: Create thread without auth returns 403
    Given path '/threads'
    And request { content: 'No auth' }
    When method post
    Then status 403

  Scenario: Get a thread by id
    * def user = createTestUser('thrget')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Thread to fetch' }
    When method post
    Then status 201
    * def threadId = response.data.id

    Given path '/threads/' + threadId
    When method get
    Then status 200
    And match response.data.id == threadId
    And match response.data.content == 'Thread to fetch'

  Scenario: Get non-existent thread returns 404
    Given path '/threads/00000000-0000-0000-0000-000000000000'
    When method get
    Then status 404

  Scenario: Edit a thread within edit window
    * def user = createTestUser('thredit')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Original content' }
    When method post
    Then status 201
    * def threadId = response.data.id

    Given path '/threads/' + threadId
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Updated content' }
    When method put
    Then status 200
    And match response.data.content == 'Updated content'

  Scenario: Edit another user's thread returns 403
    * def user1 = createTestUser('thredt1')
    * def user2 = createTestUser('thredt2')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'User1 thread' }
    When method post
    Then status 201
    * def threadId = response.data.id

    Given path '/threads/' + threadId
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'Hijack attempt' }
    When method put
    Then status 403

  Scenario: Delete own thread
    * def user = createTestUser('thrdel')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Thread to delete' }
    When method post
    Then status 201
    * def threadId = response.data.id

    Given path '/threads/' + threadId
    And header Authorization = 'Bearer ' + user.accessToken
    When method delete
    Then status 200

    # Verify deleted
    Given path '/threads/' + threadId
    When method get
    Then status 404

  Scenario: Delete another user's thread returns 403
    * def user1 = createTestUser('thrdl1')
    * def user2 = createTestUser('thrdl2')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Protected thread' }
    When method post
    Then status 201
    * def threadId = response.data.id

    Given path '/threads/' + threadId
    And header Authorization = 'Bearer ' + user2.accessToken
    When method delete
    Then status 403

  Scenario: Repost a thread
    * def user1 = createTestUser('rp1')
    * def user2 = createTestUser('rp2')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Original to repost' }
    When method post
    Then status 201
    * def threadId = response.data.id

    Given path '/threads/' + threadId + '/repost'
    And header Authorization = 'Bearer ' + user2.accessToken
    When method post
    Then status 201
    And match response.data.threadType == 'REPOST'
    And match response.data.parentThreadId == threadId

  Scenario: Quote a thread
    * def user1 = createTestUser('qt1')
    * def user2 = createTestUser('qt2')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user1.accessToken
    And request { content: 'Original to quote' }
    When method post
    Then status 201
    * def threadId = response.data.id

    Given path '/threads/' + threadId + '/quote'
    And header Authorization = 'Bearer ' + user2.accessToken
    And request { content: 'My thoughts on this thread' }
    When method post
    Then status 201
    And match response.data.threadType == 'QUOTE'
    And match response.data.parentThreadId == threadId
    And match response.data.content == 'My thoughts on this thread'

  Scenario: Toggle pin on own thread
    * def user = createTestUser('pin')
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Thread to pin' }
    When method post
    Then status 201
    * def threadId = response.data.id
    And match response.data.pinned == false

    # Pin
    Given path '/threads/' + threadId + '/pin'
    And header Authorization = 'Bearer ' + user.accessToken
    When method put
    Then status 200
    And match response.data.pinned == true

    # Unpin
    Given path '/threads/' + threadId + '/pin'
    And header Authorization = 'Bearer ' + user.accessToken
    When method put
    Then status 200
    And match response.data.pinned == false

  Scenario: Get user threads
    * def user = createTestUser('usrthr')
    # Create 2 threads
    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Thread 1' }
    When method post
    Then status 201

    Given path '/threads'
    And header Authorization = 'Bearer ' + user.accessToken
    And request { content: 'Thread 2' }
    When method post
    Then status 201

    Given path '/users/' + user.userId + '/threads'
    When method get
    Then status 200
    And match response.data.totalElements == 2
