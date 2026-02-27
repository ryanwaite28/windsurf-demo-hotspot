function fn() {
  var port = java.lang.System.getProperty('server.port') || '8080';
  var config = {
    baseUrl: 'http://localhost:' + port,
    authUrl: 'http://localhost:' + port + '/api/v1/auth',
    usersUrl: 'http://localhost:' + port + '/api/v1/users',
    socialUrl: 'http://localhost:' + port + '/api/v1/social'
  };

  // helper to create a unique user and return token + user info
  config.createTestUser = function(suffix) {
    var unique = java.util.UUID.randomUUID().toString().substring(0, 8);
    var name = suffix ? suffix + '_' + unique : 'user_' + unique;
    var payload = {
      email: name + '@test.com',
      displayName: 'Test ' + name,
      username: name,
      password: 'Password123!',
      confirmPassword: 'Password123!'
    };
    var result = karate.call('classpath:karate/helpers/create-user.feature', { signupPayload: payload });
    return result;
  };

  return config;
}
