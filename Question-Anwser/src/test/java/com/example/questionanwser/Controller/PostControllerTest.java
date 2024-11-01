package com.example.questionanwser.Controller;

import static org.mockito.Mockito.*;
import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Service.AuthenticationService;
import com.example.questionanwser.Service.JwtService;
import com.example.questionanwser.Service.PostService;
import com.example.questionanwser.Service.UserProfileService;
import dto.PostDTO;
import dto.PostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;


public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private AuthenticationService authService;

    @Mock
    private JwtService jwtService;

    private final String token = "Bearer some_jwt_token";
    private final Long postId = 1L;
    private final String username = "testuser";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllPosts() {
        Post post = new Post();
        post.setPostId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setCreatedAt(LocalDateTime.now());
        post.setUpvotes(5);
        post.setDownvotes(2);

        Page<Post> pagedPosts = new PageImpl<>(Collections.singletonList(post));

        when(postService.getAllPosts(any())).thenReturn(pagedPosts);

        ResponseEntity<Page<PostDTO>> response = postController.getAllPosts(0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    public void testGetPostById_Success() {
        Post post = new Post();
        post.setPostId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setCreatedAt(LocalDateTime.now());
        post.setUpvotes(5);
        post.setDownvotes(2);

        when(postService.getPostById(1L)).thenReturn(post);

        ResponseEntity<PostDTO> response = postController.getPostById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Title", response.getBody().getTitle());
    }



    @Test
    public void testCreatePost_Success() {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Test Title");
        postRequest.setContent("Test Content");

        String token = "Bearer validToken";
        UserCredentials user = new UserCredentials();
        user.setUsername("testUser");

        when(jwtService.getUsernameFromToken("validToken")).thenReturn("testUser");
        when(authService.getUserByUsername("testUser")).thenReturn(Optional.of(user));

        Post createdPost = new Post();
        createdPost.setPostId(1L);
        createdPost.setTitle("Test Title");
        createdPost.setContent("Test Content");
        createdPost.setUser(user);

        when(postService.createPost(any(Post.class), any())).thenReturn(createdPost);

        ResponseEntity<PostDTO> response = postController.createPost(postRequest, token);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Title", response.getBody().getTitle());
    }


    @Test
    public void testUpdatePost_Success() {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Updated Title");
        postRequest.setContent("Updated Content");

        Post updatedPost = new Post();
        updatedPost.setPostId(1L);
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");

        when(postService.updatePost(anyLong(), any(PostRequest.class))).thenReturn(updatedPost);

        ResponseEntity<PostDTO> response = postController.updatePost(1L, postRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Title", response.getBody().getTitle());
    }

    @Test
    public void testDeletePost_Success() {
        String token = "Bearer validToken";
        UserCredentials user = new UserCredentials();
        user.setUsername("testUser");

        Post post = new Post();
        post.setUser(user);

        when(jwtService.getUsernameFromToken("validToken")).thenReturn("testUser");
        when(postService.getPostById(anyLong())).thenReturn(post);

        ResponseEntity<Void> response = postController.deletePost(1L, token);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(postService, times(1)).deletePost(1L);
    }

    @Test
    public void testDeletePost_Forbidden() {
        String token = "Bearer validToken";
        UserCredentials user = new UserCredentials();
        user.setUsername("otherUser");

        Post post = new Post();
        post.setUser(user);

        when(jwtService.getUsernameFromToken("validToken")).thenReturn("testUser");
        when(postService.getPostById(anyLong())).thenReturn(post);

        ResponseEntity<Void> response = postController.deletePost(1L, token);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    @Test
    void testGetPostById() {
        Post post = new Post();
        post.setPostId(postId);
        post.setTitle("Title");
        post.setContent("Content");
        post.setCreatedAt(LocalDateTime.now());

        when(postService.getPostById(postId)).thenReturn(post);

        ResponseEntity<PostDTO> response = postController.getPostById(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(postId, response.getBody().getPostId());
    }

    @Test
    void testCreatePost() {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Title");
        postRequest.setContent("Content");

        UserCredentials user = new UserCredentials();
        user.setUsername(username);

        when(jwtService.getUsernameFromToken(any())).thenReturn(username);
        when(authService.getUserByUsername(username)).thenReturn(Optional.of(user));

        Post createdPost = new Post();
        createdPost.setPostId(postId);
        createdPost.setTitle("Title");
        createdPost.setContent("Content");
        createdPost.setUser(user);

        when(postService.createPost(any(), any())).thenReturn(createdPost);

        ResponseEntity<PostDTO> response = postController.createPost(postRequest, token);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(postId, response.getBody().getPostId());
    }

    @Test
    void testUpdatePost() {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Updated Title");
        postRequest.setContent("Updated Content");

        Post updatedPost = new Post();
        updatedPost.setPostId(postId);
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");

        when(postService.updatePost(postId, postRequest)).thenReturn(updatedPost);

        ResponseEntity<PostDTO> response = postController.updatePost(postId, postRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Title", response.getBody().getTitle());
    }

    @Test
    void testDeletePost() {
        Post post = new Post();
        post.setPostId(postId);
        UserCredentials user = new UserCredentials();
        user.setUsername(username);
        post.setUser(user);

        when(jwtService.getUsernameFromToken(any())).thenReturn(username);
        when(postService.getPostById(postId)).thenReturn(post);

        ResponseEntity<Void> response = postController.deletePost(postId, token);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(postService).deletePost(postId);
    }

    @Test
    void testSearchPosts() {
        List<Post> posts = new ArrayList<>();
        Post post = new Post();
        post.setPostId(postId);
        posts.add(post);

        when(postService.searchPosts("query")).thenReturn(posts);

        ResponseEntity<List<Post>> response = postController.searchPosts("query");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetRecentPosts() {
        List<Post> recentPosts = new ArrayList<>();
        Post post = new Post();
        post.setPostId(postId);
        recentPosts.add(post);

        when(postService.findRecentPosts()).thenReturn(recentPosts);

        ResponseEntity<List<Post>> response = postController.getRecentPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetPostsByTag() {
        List<Post> posts = new ArrayList<>();
        Post post = new Post();
        post.setPostId(postId);
        posts.add(post);

        Page<Post> postPage = new PageImpl<>(posts);
        when(postService.getPostsByTag("tag", PageRequest.of(0, 10))).thenReturn(postPage);

        ResponseEntity<Page<Post>> response = postController.getPostsByTag("tag", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }


    @Test
    void testGetPostsByUserId() {
        List<Post> posts = new ArrayList<>();
        Post post = new Post();
        post.setPostId(postId);
        posts.add(post);

        Page<Post> postPage = new PageImpl<>(posts);
        when(postService.getPostsByUserId(anyLong(), any())).thenReturn(postPage);

        ResponseEntity<Page<PostDTO>> response = postController.getPostsByUserId(1L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }



    @Test
    void testIsFollowingPost() {
        when(jwtService.getUsernameFromToken(any())).thenReturn(username);
        when(postService.isFollowingPost(postId, username)).thenReturn(true);

        ResponseEntity<Boolean> response = postController.isFollowingPost(postId, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testUpvotePost() {
        Post post = new Post();
        post.setPostId(postId);
        post.setUpvotes(1);

        when(jwtService.getUsernameFromToken(any())).thenReturn(username);
        when(postService.upvotePost(postId, username)).thenReturn(post);

        ResponseEntity<PostDTO> response = postController.upvotePost(postId, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getUpvotes());
    }

    @Test
    void testDownvotePost() {
        Post post = new Post();
        post.setPostId(postId);
        post.setDownvotes(1); // Ensure this is set correctly

        when(jwtService.getUsernameFromToken(any())).thenReturn(username);
        when(postService.downvotePost(postId, username)).thenReturn(post); // Returning the post with downvotes

        ResponseEntity<PostDTO> response = postController.downvotePost(postId, token);

        // Debugging output
        System.out.println("Downvotes in response: " + response.getBody().getDownvotes());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getDownvotes()); // Assert for expected downvotes
    }



}
