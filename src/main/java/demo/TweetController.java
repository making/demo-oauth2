package demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/v1/tweets")
public class TweetController {
    Map<UUID, Tweet> tweetMap = Collections.synchronizedMap(new LinkedHashMap<>());

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Tweet {
        private UUID uuid;
        private String content;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Tweet> getTweets() {
        return tweetMap.values();
    }


    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    Tweet getTweet(@PathVariable UUID uuid) {
        return tweetMap.get(uuid);
    }

    @RequestMapping(method = RequestMethod.POST)
    Tweet postTweets(@RequestBody Tweet tweet) {
        tweet.setUuid(UUID.randomUUID());
        tweetMap.put(tweet.getUuid(), tweet);
        return tweet;
    }
}
