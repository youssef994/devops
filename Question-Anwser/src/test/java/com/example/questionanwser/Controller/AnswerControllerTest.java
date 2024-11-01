package com.example.questionanwser.Controller;


import com.example.questionanwser.Model.Answer;
import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Service.AnswerService;
import com.example.questionanwser.Service.JwtService;
import dto.AnswerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AnswerController.class)
public class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private JwtService jwtService;



    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testCreateAnswer() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setContent("New answer content");

        String token = "Bearer testToken";

        Post mockPost = new Post();
        mockPost.setPostId(1L);

        Answer mockAnswer = new Answer();
        mockAnswer.setContent("New answer content");
        mockAnswer.setPost(mockPost);

        when(jwtService.getUsernameFromToken("testToken")).thenReturn("testUser");
        when(answerService.createAnswer(any(Answer.class), eq(1L), eq("testUser"))).thenReturn(mockAnswer);

        mockMvc.perform(post("/api/answers/create")
                        .header("Authorization", token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"content\": \"New answer content\", \"postId\": 1 }"))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testGetAllAnswers() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswerId(1L);
        answerDTO.setContent("Test content");
        answerDTO.setPostId(1L);
        Post mockPost = new Post();
        mockPost.setPostId(1L);
        Answer mockAnswer = new Answer();
        mockAnswer.setAnswerId(1L);
        mockAnswer.setContent("Test content");
        mockAnswer.setPost(mockPost);
        when(answerService.getAllAnswers()).thenReturn(Collections.singletonList(mockAnswer));
        mockMvc.perform(get("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].answerId").value(answerDTO.getAnswerId()))
                .andExpect(jsonPath("$[0].content").value(answerDTO.getContent()))
                .andExpect(jsonPath("$[0].postId").value(answerDTO.getPostId()));
    }



    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testGetAnswersByPostId() throws Exception {
        Long postId = 1L;

        Post mockPost = new Post();
        mockPost.setPostId(postId);

        Answer answer = new Answer();
        answer.setAnswerId(1L);
        answer.setContent("Answer for post");
        answer.setPost(mockPost);

        when(answerService.getAnswersByPostId(postId)).thenReturn(Collections.singletonList(answer));

        mockMvc.perform(get("/api/answers/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].answerId").value(answer.getAnswerId()))
                .andExpect(jsonPath("$[0].content").value(answer.getContent()))
                .andExpect(jsonPath("$[0].postId").value(postId));
    }








    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testGetTotalUpvotesByUsername() throws Exception {
        String username = "testUser";
        when(answerService.getTotalUpvotesByUsername(username)).thenReturn(10);

        mockMvc.perform(get("/api/answers/upvotes/count")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testGetTotalDownvotesByUsername() throws Exception {
        String username = "testUser";
        when(answerService.getTotalDownvotesByUsername(username)).thenReturn(5);

        mockMvc.perform(get("/api/answers/downvotes/count")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testGetUserAnswersCount() throws Exception {
        Long userId = 1L;
        when(answerService.getUserAnswersCount(userId)).thenReturn(3);

        mockMvc.perform(get("/api/answers/count")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testValidateAnswer() throws Exception {
        Long answerId = 1L;
        String token = "testToken";
        when(jwtService.getUsernameFromToken(token)).thenReturn("testUser");
        Post mockPost = new Post();
        mockPost.setPostId(1L);
        Answer mockAnswer = new Answer();
        mockAnswer.setContent("Mock answer content");
        mockAnswer.setPost(mockPost);

        when(answerService.validateAnswer(answerId, "testUser")).thenReturn(mockAnswer);
        mockMvc.perform(post("/api/answers/{id}/validate", answerId)
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }



    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testSearchAnswersByContent() throws Exception {
        String content = "test";
        Post mockPost = new Post();
        mockPost.setPostId(1L);
        Answer mockAnswer = new Answer();
        mockAnswer.setPost(mockPost);
        mockAnswer.setAnswerId(1L);
        mockAnswer.setContent("Mock answer content");

        when(answerService.searchAnswersByContent(content)).thenReturn(Collections.singletonList(mockAnswer));

        mockMvc.perform(get("/api/answers/search")
                        .param("content", content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testDeleteAnswer() throws Exception {
        Long answerId = 1L;

        doNothing().when(answerService).deleteAnswer(answerId);

        mockMvc.perform(delete("/api/answers/delete/{id}", answerId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) //  CSRF token
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testUpvoteAnswer() throws Exception {
        Long answerId = 1L;
        String token = "testToken";

        when(jwtService.getUsernameFromToken(token)).thenReturn("testUser");
        Post mockPost = new Post();
        mockPost.setPostId(1L);

        Answer mockAnswer = new Answer();
        mockAnswer.setContent("Mock answer content");
        mockAnswer.setPost(mockPost);

        when(answerService.upvoteAnswer(answerId, "testUser")).thenReturn(mockAnswer);

        mockMvc.perform(post("/api/answers/{id}/upvote", answerId)
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testDownvoteAnswer() throws Exception {
        Long answerId = 1L;
        String token = "testToken";
        when(jwtService.getUsernameFromToken(token)).thenReturn("testUser");
        Post mockPost = new Post();
        mockPost.setPostId(1L);

        Answer mockAnswer = new Answer();
        mockAnswer.setContent("Mock answer content");
        mockAnswer.setPost(mockPost);
        when(answerService.downvoteAnswer(answerId, "testUser")).thenReturn(mockAnswer);

        mockMvc.perform(post("/api/answers/{id}/downvote", answerId)
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testUpdateAnswer() throws Exception {
        Long answerId = 1L;
        Post mockPost = new Post();
        mockPost.setPostId(1L);

        Answer mockAnswer = new Answer();
        mockAnswer.setContent("Updated content");
        mockAnswer.setPost(mockPost);
        when(answerService.updateAnswer(eq(answerId), any(Answer.class))).thenReturn(mockAnswer);

        mockMvc.perform(put("/api/answers/update/{id}", answerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"content\": \"Updated content\" }")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }


}
