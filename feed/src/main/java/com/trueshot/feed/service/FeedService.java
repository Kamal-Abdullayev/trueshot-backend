package com.trueshot.feed.service;

import com.trueshot.feed.dto.PostResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class FeedService {

    private final RestTemplate restTemplate;

    @Autowired
    public FeedService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Fetch all posts from the Post microservice
    public List<PostResponseDto> getFeed() {
        String url = "http://localhost:8086/api/v1/post/all";  // Updated to port 8086
        // Use ParameterizedTypeReference to ensure correct type deserialization
        ResponseEntity<List<PostResponseDto>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<PostResponseDto>>() {});
        return response.getBody();
    }
}
