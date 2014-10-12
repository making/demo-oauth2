package demo;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@IntegrationTest({"server.port:0"})
public class TweetControllerTest {
    @Value("${local.server.port}")
    int port;

    @Autowired
    TweetController controller;
    TweetController.Tweet tweet1;
    TweetController.Tweet tweet2;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        controller.tweetMap.clear(); // clear test data
        tweet1 = new TweetController.Tweet(UUID.randomUUID(), "sample1");
        tweet2 = new TweetController.Tweet(UUID.randomUUID(), "sample2");
        controller.tweetMap.put(tweet1.getUuid(), tweet1);
        controller.tweetMap.put(tweet2.getUuid(), tweet2);
    }

    String accessToken() {
        return given()
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("clientapp:123456".getBytes()))
                .formParam("username", "making")
                .formParam("password", "pass")
                .formParam("grant_type", "password")
                .post("/oauth/token")
                .then()
                .extract()
                .path("access_token");
    }

    @Test
    public void testGetTweets() throws Exception {
        given().header("Authorization", "Bearer " + accessToken())
                .log().all()
                .then()
                .when().get("/api/v1/tweets")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].content", is(tweet1.getContent()))
                .body("[0].uuid", is(tweet1.getUuid().toString()))
                .body("[1].content", is(tweet2.getContent()))
                .body("[1].uuid", is(tweet2.getUuid().toString()));
    }


    @Test
    public void testGetTweet() throws Exception {
        given().header("Authorization", "Bearer " + accessToken())
                .log().all()
                .then()
                .when()
                .get("/api/v1/tweets/{uuid}", tweet1.getUuid().toString())
                .then()
                .log().all()
                .statusCode(200)
                .body("content", is(tweet1.getContent()))
                .body("uuid", is(tweet1.getUuid().toString()));

        given().header("Authorization", "Bearer " + accessToken())
                .log().all()
                .then()
                .when()
                .get("/api/v1/tweets/{uuid}", tweet2.getUuid().toString())
                .then()
                .log().all()
                .statusCode(200)
                .body("content", is(tweet2.getContent()))
                .body("uuid", is(tweet2.getUuid().toString()));
    }

    @Test
    public void testPostAndGetTweetsAndGetTheTweet() throws Exception {
        String uuid = given().header("Authorization", "Bearer " + accessToken())
                .header("Content-Type", "application/json")
                .body(Collections.singletonMap("content", "Hello World"))
                .log().all()
                .then()
                .when()
                .post("/api/v1/tweets")
                .then()
                .log().all()
                .statusCode(200)
                .body("content", is("Hello World"))
                .body("uuid", is(not(isEmptyString())))
                .extract()
                .path("uuid");

        given().header("Authorization", "Bearer " + accessToken())
                .log().all()
                .then()
                .when()
                .get("/api/v1/tweets")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(3))
                .body("[2].content", is("Hello World"))
                .body("[2].uuid", is(uuid));

        given().header("Authorization", "Bearer " + accessToken())
                .log().all()
                .then()
                .when()
                .get("/api/v1/tweets/{uuid}", uuid)
                .then()
                .log().all()
                .statusCode(200)
                .body("content", is("Hello World"))
                .body("uuid", is(uuid));
    }
}