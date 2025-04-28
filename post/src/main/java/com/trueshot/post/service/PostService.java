package com.trueshot.post.service;

import com.trueshot.post.dto.PostCreateRequestDto;
import com.trueshot.post.dto.PostResponseDto;
import com.trueshot.post.dto.PostUpdateDto;
import com.trueshot.post.entity.Post;
import com.trueshot.post.exception.ResourceNotFoundException;
import com.trueshot.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public List<PostResponseDto> getAllProducts(Pageable pageable) {
        return postRepository.findAll(pageable).stream()
                .map(PostResponseDto::convert)
                .toList();
    }

    public PostResponseDto getProductById(String productId) {
        return PostResponseDto.convert(getProductObjectById(productId));
    }

    public Post getProductObjectById(String productId) {
        if (productId == null || productId.isEmpty()) {
            throw new InvalidParameterException("Invalid product id");
        }
        return postRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product not found")
        );
    }

    @Transactional
    public PostResponseDto saveProduct(PostCreateRequestDto postCreateRequestDto) {
        Post post = Post.builder()
                .title(postCreateRequestDto.getTitle())
                .content(postCreateRequestDto.getContent())
                .url(postCreateRequestDto.getUrl())
                .build();

        return PostResponseDto.convert(postRepository.save(post));
    }

    public PostResponseDto updateProduct(String postId, PostUpdateDto postUpdateDto) {
        Post dbPost = getProductObjectById(postId);
        if (!postUpdateDto.getTitle().equals(dbPost.getTitle())) {
            dbPost.setTitle(postUpdateDto.getTitle());
        }
        if (!postUpdateDto.getContent().equals(dbPost.getContent())) {
            dbPost.setContent(postUpdateDto.getContent());
        }
        if (!postUpdateDto.getUrl().equals(dbPost.getUrl())) {
            dbPost.setUrl(postUpdateDto.getUrl());
        }
        return PostResponseDto.convert(postRepository.save(dbPost));
    }

    public void deleteProduct(String postId) {
        Post dbPost = getProductObjectById(postId);
        postRepository.delete(dbPost);
    }

}
