package com.trueshot.feed.repository;

import com.trueshot.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    // Method to find a feed by userId
    Optional<Feed> findByUserId(Long userId); // Returns an Optional containing the feed for the user
}
