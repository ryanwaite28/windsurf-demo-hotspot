package com.hotspot.karate;

import com.hotspot.HotSpotApplication;
import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class ApiTest {

    private static ConfigurableApplicationContext context;

    @BeforeAll
    static void beforeAll() {
        if (context == null) {
            SpringApplication app = new SpringApplication(HotSpotApplication.class);
            app.setAdditionalProfiles("test");
            context = app.run();
            String port = context.getEnvironment().getProperty("local.server.port");
            System.setProperty("server.port", port);
        }
    }

    @Karate.Test
    Karate testAuth() {
        return Karate.run("classpath:karate/auth/auth.feature");
    }

    @Karate.Test
    Karate testUsers() {
        return Karate.run("classpath:karate/users/users.feature");
    }

    @Karate.Test
    Karate testUserSocial() {
        return Karate.run("classpath:karate/users/user-social.feature");
    }

    @Karate.Test
    Karate testThreads() {
        return Karate.run("classpath:karate/social/threads.feature");
    }

    @Karate.Test
    Karate testThreadInteractions() {
        return Karate.run("classpath:karate/social/thread-interactions.feature");
    }

    @Karate.Test
    Karate testComments() {
        return Karate.run("classpath:karate/social/comments.feature");
    }

    @Karate.Test
    Karate testFeed() {
        return Karate.run("classpath:karate/social/feed.feature");
    }
}
