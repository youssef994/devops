package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Vote;
import com.example.questionanwser.Service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class VoteControllerTest {

    @InjectMocks
    private VoteController voteController;

    @Mock
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllVotes() {
        Vote vote1 = new Vote();
        Vote vote2 = new Vote();
        List<Vote> votes = Arrays.asList(vote1, vote2);

        when(voteService.getAllVotes()).thenReturn(votes);

        ResponseEntity<List<Vote>> response = voteController.getAllVotes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(votes, response.getBody());
        verify(voteService).getAllVotes();
    }

    @Test
    void testGetVoteById() {
        Long id = 1L;
        Vote vote = new Vote();

        when(voteService.getVoteById(id)).thenReturn(vote);

        ResponseEntity<Vote> response = voteController.getVoteById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vote, response.getBody());
        verify(voteService).getVoteById(id);
    }

    @Test
    void testSaveVote() {
        Vote vote = new Vote();
        Vote savedVote = new Vote();

        when(voteService.saveVote(vote)).thenReturn(savedVote);

        ResponseEntity<Vote> response = voteController.saveVote(vote);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedVote, response.getBody());
        verify(voteService).saveVote(vote);
    }

    @Test
    void testDeleteVoteById() {
        Long id = 1L;

        ResponseEntity<Void> response = voteController.deleteVoteById(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(voteService).deleteVoteById(id);
    }
}
