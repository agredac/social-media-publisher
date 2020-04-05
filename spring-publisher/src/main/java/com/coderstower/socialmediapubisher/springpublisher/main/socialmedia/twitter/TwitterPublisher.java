package com.coderstower.socialmediapubisher.springpublisher.main.socialmedia.twitter;

import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.repository.Post;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Acknowledge;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.Publication;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.SocialMediaPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1Credentials;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.repository.credential.Oauth1CredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.NullAuthorization;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TwitterPublisher implements SocialMediaPublisher {
    private final String name;
    private final Oauth1CredentialsRepository oauth1CredentialsRepository;
    private final Twitter twitter;
    private final Clock clock;

    public TwitterPublisher(String name, Oauth1CredentialsRepository oauth1CredentialsRepository, Twitter twitter, Clock clock) {
        this.name = name;
        this.oauth1CredentialsRepository = oauth1CredentialsRepository;
        this.twitter = twitter;
        this.clock = clock;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Acknowledge ping() {
        Oauth1Credentials credentials = oauth1CredentialsRepository.getCredentials(name)
                .orElseThrow(() -> new IllegalArgumentException("The credentials for " + name + " doesn't exist"));

        try {
            if (areNotCredentialsReady(twitter)) {
                setCredentials(twitter, credentials);
            }

            Paging paging = new Paging(1, 1);
            List<Status> statuses = twitter.getHomeTimeline(paging);

            if (Objects.nonNull(statuses)) {
                return Acknowledge.builder()
                        .status(Acknowledge.Status.SUCCESS)
                        .build();
            } else {
                return Acknowledge.builder()
                        .status(Acknowledge.Status.FAILURE)
                        .build();
            }
        } catch (TwitterException e) {
            log.error("Error ping to " + name, e);

            return Acknowledge.builder()
                    .description("Ping error")
                    .status(Acknowledge.Status.FAILURE)
                    .exception(e)
                    .build();
        }
    }

    @Override
    public Publication publish(Post post) {
        Oauth1Credentials credentials = oauth1CredentialsRepository.getCredentials(name)
                .orElseThrow(() -> new IllegalArgumentException("The credentials for " + name + " doesn't exist"));

        try {
            if (areNotCredentialsReady(twitter)) {
                setCredentials(twitter, credentials);
            }

            Status statuses = twitter.updateStatus(post.basicFormat());

            if (Objects.nonNull(statuses)) {
                return Publication.builder()
                        .id(String.valueOf(statuses.getId()))
                        .status(Publication.Status.SUCCESS)
                        .publisher(name)
                        .publishedDate(LocalDateTime.now(clock))
                        .build();
            } else {
                return Publication.builder()
                        .status(Publication.Status.FAILURE)
                        .publisher(name)
                        .publishedDate(LocalDateTime.now(clock))
                        .build();
            }
        } catch (TwitterException e) {
            log.error("Error publishing to " + name, e);

            return Publication.builder()
                    .status(Publication.Status.FAILURE)
                    .publisher(name)
                    .publishedDate(LocalDateTime.now(clock))
                    .build();
        }
    }

    private void setCredentials(Twitter twitter, Oauth1Credentials credentials) {
        AccessToken accessToken = new AccessToken(credentials.getAccessToken(), credentials.getTokenSecret());
        twitter.setOAuthConsumer(credentials.getConsumerKey(), credentials.getConsumerSecret());
        twitter.setOAuthAccessToken(accessToken);
    }

    private boolean areNotCredentialsReady(Twitter twitter) throws TwitterException {
        return twitter.getAuthorization() == NullAuthorization.getInstance();
    }
}
