package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.Tags;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.TagsRepository;
import dto.PostRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagsRepository tagsRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private UserCredentials userCredentials;


    @Mock
    private RestTemplate questionAnswerRestTemplate;

    @InjectMocks
    private PostService postService;

    @Mock
    private Query query;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query); // Mock createNativeQuery
        when(query.executeUpdate()).thenReturn(1); // Mock executeUpdate result if applicable
        ReflectionTestUtils.setField(postService, "entityManager", entityManager);

    }

    @Test
    void getAllPosts() {
        Post post = new Post();
        post.setTitle("Test Title");
        Page<Post> posts = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findAll(PageRequest.of(0, 10))).thenReturn(posts);

        Page<Post> result = postService.getAllPosts(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Title", result.getContent().get(0).getTitle());
    }

    @Test
    void getPostById() {
        Long postId = 1L;
        Post post = new Post();
        post.setPostId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
    }

    @Test
    void createPost() {
        // Arrange
        Post post = new Post();
        post.setTitle("New Post");
        Set<String> tagNames = new HashSet<>();
        tagNames.add("Java");

        Tags tag = new Tags("Java");
        when(tagsRepository.findByName("Java")).thenReturn(Optional.of(tag));
        when(postRepository.save(post)).thenReturn(post);

        // Act
        Post result = postService.createPost(post, tagNames);

        // Assert
        assertNotNull(result);
        assertEquals("New Post", result.getTitle());
        assertTrue(result.getTags().contains(tag));

        // Verify interactions with mocks
        verify(entityManager, times(1)).createNativeQuery(anyString());
        verify(query, times(1)).executeUpdate();
    }
    @Test
    void updatePost_Success() {
        Long postId = 1L;

        // Create an existing post that will be updated
        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setTitle("Old Title");
        existingPost.setContent("Old Content");

        // Create a request object with updated values
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Updated Title");
        postRequest.setContent("Updated Content");

        // Simulating the tags that will be used in the request
        Set<String> tags = new HashSet<>();
        tags.add("Java");
        postRequest.setTags(tags);

        // Mock the repository behavior
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // Mock the tagsRepository to return a Tags object if the tag exists
        Tags existingTag = new Tags();
        existingTag.setName("Java");
        when(tagsRepository.findByName("Java")).thenReturn(Optional.of(existingTag));

        // Ensure that the save method returns the updated post
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method under test
        Post updatedPost = postService.updatePost(postId, postRequest);

        // Assertions to verify the updated values
        assertNotNull(updatedPost, "Updated post should not be null");
        assertEquals("Updated Title", updatedPost.getTitle(), "Post title should be updated");
        assertEquals("Updated Content", updatedPost.getContent(), "Post content should be updated");

        // Assert that the tags are updated correctly
        Set<String> updatedTags = updatedPost.getTags().stream()
                .map(Tags::getName)
                .collect(Collectors.toSet());
        assertTrue(updatedTags.contains("Java"), "Updated post should contain the 'Java' tag");

        // Verify interactions
        verify(postRepository, times(1)).save(existingPost);
        verify(postRepository, times(1)).findById(postId);
        verify(tagsRepository, times(1)).findByName("Java");
    }



    @Test
    void updatePost_PostNotFound() {
        Long postId = 1L;
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Updated Title");
        postRequest.setContent("Updated Content");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(postId, postRequest);
        });

        assertEquals("Post not found with id " + postId, exception.getMessage());
    }

    @Test
    void deletePost_Success() {
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setPostId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        postService.deletePost(postId);

        verify(postRepository, times(1)).delete(existingPost);
    }

    @Test
    void deletePost_PostNotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost(postId);
        });

        assertEquals("Post not found with id: " + postId, exception.getMessage());
    }

    @Test
    void searchPosts_Success() {
        String query = "test";
        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("This is a test post.");
        when(postRepository.findBySearch(query)).thenReturn(Collections.singletonList(post));

        List<Post> results = postService.searchPosts(query);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Title", results.get(0).getTitle());
    }

    @Test
    void getPostsByTag_Success() {
        String tag = "Java";
        Pageable pageable = PageRequest.of(0, 10);
        Post post = new Post();
        post.setTitle("Java Post");
        when(postRepository.findByTagsName(tag, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(post)));

        Page<Post> result = postService.getPostsByTag(tag, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Java Post", result.getContent().get(0).getTitle());
    }
    @Test
    void upvotePost_Success() {
        Long postId = 1L;
        String username = "user1";

        // Setup User and Post
        UserCredentials user = new UserCredentials();
        user.setId(1); // Set a fixed user ID

        // Mock the existing Post
        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUpvotes(0);
        existingPost.setUpvoters(new HashSet<>());
        existingPost.setUser(user); // Set the user for the post

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the upvote method
        postService.upvotePost(postId, username);

        // Assertions
        assertEquals(1, existingPost.getUpvotes()); // Check if upvotes incremented
        assertTrue(existingPost.getUpvoters().contains(username)); // Check if username added to upvoters
    }

    @Test
    void upvotePost_PreventMultipleUpvotes() {
        Long postId = 1L;
        String username = "user1";

        // Setup User and Post
        UserCredentials user = new UserCredentials();
        user.setId(1); // Set a fixed user ID

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUpvotes(1);
        existingPost.setUpvoters(new HashSet<>(Collections.singletonList(username)));
        existingPost.setUser(user); // Set the user for the post

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // Assert that an exception is thrown when trying to upvote again
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.upvotePost(postId, username);
        });

        assertEquals("You have already upvoted this post", exception.getMessage());
    }
    @Test
    void upvotePost_ReplaceDownvoteWithUpvote() {
        Long postId = 1L;
        String username = "user1";

        // Setup User and Post
        UserCredentials user = new UserCredentials();
        user.setId(1); // Set a fixed user ID

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUpvotes(0);
        existingPost.setDownvotes(1);
        existingPost.setDownvoters(new HashSet<>(Collections.singletonList(username)));
        existingPost.setUser(user); // Set the user for the post

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the upvote method
        postService.upvotePost(postId, username);

        // Assertions
        assertEquals(1, existingPost.getUpvotes()); // Check if upvotes incremented
        assertEquals(0, existingPost.getDownvotes()); // Check if downvotes decremented
        assertTrue(existingPost.getUpvoters().contains(username)); // Check if username added to upvoters
        assertFalse(existingPost.getDownvoters().contains(username)); // Check if username removed from downvoters
    }
    @Test
    void downvotePost_Success() {
        Long postId = 1L;
        String username = "user1";

        // Setup User and Post
        UserCredentials user = new UserCredentials();
        user.setId(1); // Set a fixed user ID

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUpvotes(1);
        existingPost.setDownvotes(0);
        existingPost.setUpvoters(new HashSet<>(Collections.singletonList(username)));
        existingPost.setUser(user); // Set the user for the post

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the downvote method (assuming similar logic exists)
        postService.downvotePost(postId, username);

        // Assertions
        assertEquals(0, existingPost.getUpvotes()); // Check if upvotes decremented
        assertEquals(1, existingPost.getDownvotes()); // Check if downvotes incremented
        assertFalse(existingPost.getUpvoters().contains(username)); // Check if username removed from upvoters
        assertTrue(existingPost.getDownvoters().contains(username)); // Check if username added to downvoters
    }
    @Test
    void downvotePost_PreventMultipleDownvotes() {
        Long postId = 1L;
        String username = "user1";

        // Setup User and Post
        UserCredentials user = new UserCredentials();
        user.setId(1); // Set a fixed user ID

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setDownvotes(1);
        existingPost.setDownvoters(new HashSet<>(Collections.singletonList(username)));
        existingPost.setUser(user); // Set the user for the post

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // Assert that an exception is thrown when trying to downvote again
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.downvotePost(postId, username);
        });

        assertEquals("You have already downvoted this post", exception.getMessage());
    }
    @Test
    void downvotePost_ReplaceUpvoteWithDownvote() {
        Long postId = 1L;
        String username = "user1";

        // Setup User and Post
        UserCredentials user = new UserCredentials();
        user.setId(1); // Set a fixed user ID

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUpvotes(1);
        existingPost.setDownvotes(0);
        existingPost.setUpvoters(new HashSet<>(Collections.singletonList(username)));
        existingPost.setUser(user); // Set the user for the post

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the downvote method
        postService.downvotePost(postId, username);

        // Assertions
        assertEquals(0, existingPost.getUpvotes()); // Check if upvotes decremented
        assertEquals(1, existingPost.getDownvotes()); // Check if downvotes incremented
        assertFalse(existingPost.getUpvoters().contains(username)); // Check if username removed from upvoters
        assertTrue(existingPost.getDownvoters().contains(username)); // Check if username added to downvoters
    }
    @Test
    void followPost_Success() {
        Long postId = 1L;
        String username = "user1";
        Long userId = 1L; // Define a user ID to avoid NullPointerException

        // Setup User and Post
        UserCredentials user = new UserCredentials();
        user.setId(1); // Set a fixed user ID

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setFollowers(new HashSet<>());
        existingPost.setUser(user); // Set the user associated with the post

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the follow method
        postService.followPost(postId, username);

        // Assertions
        assertTrue(existingPost.getFollowers().contains(username)); // Check if username added to followers
    }

    @Test
    void unfollowPost_Success() {
        Long postId = 1L;
        String username = "user1";

        // Setup User and Post
        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setFollowers(new HashSet<>(Collections.singletonList(username)));

        // Mock the behavior of the postRepository
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the unfollow method
        postService.unfollowPost(postId, username);

        // Assertions
        assertFalse(existingPost.getFollowers().contains(username)); // Check if username removed from followers
    }

}
