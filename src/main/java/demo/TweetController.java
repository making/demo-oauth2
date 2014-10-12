package demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
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
        private String tweetedBy;
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
    Tweet postTweets(@RequestBody Tweet tweet, Authentication authentication) {
        tweet.setUuid(UUID.randomUUID());
        tweet.setTweetedBy(authentication.getName());
        tweetMap.put(tweet.getUuid(), tweet);
        return tweet;
    }
}
