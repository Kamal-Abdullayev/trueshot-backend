package com.trueshot.feed.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Setter
@Getter
@Entity // Marks this class as an entity to be mapped to a table in the database.
public class Feed {

    // Getters and Setters for each field
    @Id // Marks this field as the primary key for the Feed entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID value.
    private Long id; // The unique identifier for the feed.

    private String content; // The content of the feed (e.g., post text, image URL, etc.).
    private Long userId; // The ID of the user who created the feed.

}
