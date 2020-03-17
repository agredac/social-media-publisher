package com.coderstower.socialmediapubisher.springpublisher.main.controller;


import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.PostPublisher;
import com.coderstower.socialmediapubisher.springpublisher.abstraction.post.socialmedia.PublishedPost;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@Component
public class PostsController {
    private final PostPublisher postPublisher;

    public PostsController(PostPublisher postPublisher) {
        this.postPublisher = postPublisher;
    }

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public Map<String, String> ping() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");
        return pong;
    }

    @RequestMapping(path = "/posts/next", method = RequestMethod.POST)
    public List<PublishedPost> postNext() {
        return postPublisher.publishNext();
    }
}
