package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Answer;
import com.example.questionanwser.Model.Notification;
import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.AnswerRepository;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.UserCredentialRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserCredentialRepository userRepository;

    @Mock
    private RestTemplate questionAnswerRestTemplate;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    private Post post;
    private UserCredentials user;
    private Answer answer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup sample data
        post = new Post();
        post.setPostId(1L);

        user = new UserCredentials();
        user.setUsername("testUser");
        user.setId(1);

        answer = new Answer();
        answer.setAnswerId(1L);
        answer.setContent("This is an answer");
        answer.setPost(post);
        answer.setUser(user);
        answer.setUpvotes(0);
        answer.setDownvotes(0);
        answer.setUpvoters(new HashSet<>());
        answer.setDownvoters(new HashSet<>());

        // Set up a mock HTTP request
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void testGetAnswerById() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        Answer foundAnswer = answerService.getAnswerById(1L);

        assertNotNull(foundAnswer);
        assertEquals(answer.getContent(), foundAnswer.getContent());
    }

    @Test
    void testDeleteAnswer() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        answerService.deleteAnswer(1L);

        verify(answerRepository, times(1)).delete(answer);
    }

    @Test
    void testGetAnswersByPostId() {
        List<Answer> answers = new ArrayList<>();
        answers.add(answer);
        when(answerRepository.findByPost_PostId(1L)).thenReturn(answers);

        List<Answer> foundAnswers = answerService.getAnswersByPostId(1L);

        assertNotNull(foundAnswers);
        assertEquals(1, foundAnswers.size());
    }

    @Test
    void testSearchAnswersByContent() {
        List<Answer> answers = new ArrayList<>();
        answers.add(answer);
        when(answerRepository.findByContentContaining("answer")).thenReturn(answers);

        List<Answer> foundAnswers = answerService.searchAnswersByContent("answer");

        assertNotNull(foundAnswers);
        assertEquals(1, foundAnswers.size());
    }

    @Test
    void testGetTotalUpvotesByUsername() {
        when(answerRepository.countTotalUpvotesByUsername("testUser")).thenReturn(5);

        int upvotes = answerService.getTotalUpvotesByUsername("testUser");

        assertEquals(5, upvotes);
    }

    @Test
    void testGetTotalDownvotesByUsername() {
        when(answerRepository.countTotalDownvotesByUsername("testUser")).thenReturn(3);

        int downvotes = answerService.getTotalDownvotesByUsername("testUser");

        assertEquals(3, downvotes);
    }

    @Test
    void testGetUserAnswersCount() {
        when(answerRepository.countAnswersByUserId(1L)).thenReturn(10);

        int count = answerService.getUserAnswersCount(1L);

        assertEquals(10, count);
    }


    @Test
    void testUpvoteAnswer_AlreadyUpvoted() {
        // Arrange
        answer.getUpvoters().add("testUser"); // Simulate existing upvote
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> answerService.upvoteAnswer(1L, "testUser"));
    }

    @Test
    void testDownvoteAnswer_AlreadyDownvoted() {
        // Arrange
        answer.getDownvoters().add("testUser"); // Simulate existing downvote
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> answerService.downvoteAnswer(1L, "testUser"));
    }
    @Test
    void testUpvoteAnswer() {
        answer.getUpvoters().clear();
        answer.getDownvoters().clear();
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer upvotedAnswer = answerService.upvoteAnswer(1L, "testUser");

        assertNotNull(upvotedAnswer);
        assertTrue(upvotedAnswer.getUpvoters().contains("testUser"));
        assertEquals(1, upvotedAnswer.getUpvotes());
        verify(answerRepository, times(1)).save(any(Answer.class));
        verify(questionAnswerRestTemplate, times(1))
                .postForObject(anyString(), any(Notification.class), eq(Notification.class));
    }
    @Test
    void testDownvoteAnswer() {
        answer.getUpvoters().clear();
        answer.getDownvoters().clear();
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer downvotedAnswer = answerService.downvoteAnswer(1L, "testUser");

        assertNotNull(downvotedAnswer);
        assertTrue(downvotedAnswer.getDownvoters().contains("testUser"));
        assertEquals(1, downvotedAnswer.getDownvotes());
        verify(answerRepository, times(1)).save(any(Answer.class));
        verify(questionAnswerRestTemplate, times(1))
                .postForObject(anyString(), any(Notification.class), eq(Notification.class));
    }

    @Test
    void testCreateAnswer() {
        // Arrange
        Long postId = 1L;
        String username = "testUser";

        // Create a mock post and user
        Post post = new Post();
        UserCredentials user = new UserCredentials();
        user.setUsername(username);
        user.setId(1); // Set user ID for consistency
        post.setUser(user); // Link the user to the post

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Answer answer = new Answer();
        answer.setContent("This is a test answer");

        // Mock the save behavior
        when(answerRepository.save(any(Answer.class))).thenAnswer(invocation -> {
            Answer savedAnswer = invocation.getArgument(0);
            savedAnswer.setAnswerId(1L); // Simulate setting an ID on save
            return savedAnswer;
        });

        // Act
        Answer createdAnswer = answerService.createAnswer(answer, postId, username);

        // Assert
        assertNotNull(createdAnswer);
        assertEquals(answer.getContent(), createdAnswer.getContent());
        assertEquals(post, createdAnswer.getPost()); // Verify post association
        assertEquals(user, createdAnswer.getUser()); // Verify user association

    }

}
