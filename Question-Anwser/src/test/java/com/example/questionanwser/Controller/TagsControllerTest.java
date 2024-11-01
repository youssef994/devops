package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Tags;
import com.example.questionanwser.Service.TagsService;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagsControllerTest {

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private TagsController tagsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }







    @Test
    void testDeleteTag() {
        // Arrange
        Long tagId = 1L;

        // Act
        ResponseEntity<Void> response = tagsController.deleteTag(tagId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tagsService, times(1)).deleteTag(tagId);
    }



    @Test
    void testGetAllTags() {
        // Arrange
        List<Tags> tagsList = new ArrayList<>();
        Tags tag1 = new Tags();
        tag1.setTagId(1L); // Changed from setId to setTagId
        tag1.setName("Tag1");

        Tags tag2 = new Tags();
        tag2.setTagId(2L); // Changed from setId to setTagId
        tag2.setName("Tag2");

        tagsList.add(tag1);
        tagsList.add(tag2);

        when(tagsService.getAllTags()).thenReturn(tagsList);

        // Act
        ResponseEntity<List<Tags>> response = tagsController.getAllTags();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tagsList, response.getBody());
        verify(tagsService, times(1)).getAllTags();
    }

    @Test
    void testGetTagById_ReturnsTag() {
        // Arrange
        Long tagId = 1L;
        Tags tag = new Tags();
        tag.setTagId(tagId); // Changed from setId to setTagId
        tag.setName("Tag1");

        when(tagsService.getTagById(tagId)).thenReturn(Optional.of(tag));

        // Act
        ResponseEntity<Tags> response = tagsController.getTagById(tagId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tag, response.getBody());
        verify(tagsService, times(1)).getTagById(tagId);
    }

    @Test
    void testSearchTags() {
        // Arrange
        String query = "Tag1";
        List<Tags> tagsList = new ArrayList<>();
        Tags tag = new Tags();
        tag.setTagId(1L); // Changed from setId to setTagId
        tag.setName("Tag1");

        tagsList.add(tag);

        when(tagsService.getTagsByName(query)).thenReturn(tagsList);

        // Act
        List<Tags> response = tagsController.searchTags(query);

        // Assert
        assertEquals(tagsList, response);
        verify(tagsService, times(1)).getTagsByName(query);
    }

    @Test
    void testCreateTag() {
        // Arrange
        String tagName = "NewTag";
        Tags createdTag = new Tags();
        createdTag.setTagId(1L); // Changed from setId to setTagId
        createdTag.setName(tagName);

        when(tagsService.getOrCreateTag(tagName)).thenReturn(createdTag);

        // Act
        Tags response = tagsController.createTag(tagName);

        // Assert
        assertEquals(createdTag, response);
        verify(tagsService, times(1)).getOrCreateTag(tagName);
    }

    @Test
    void testGetPopularTags() {
        // Arrange
        List<Tags> popularTagsList = new ArrayList<>();
        Tags popularTag = new Tags();
        popularTag.setTagId(1L); // Changed from setId to setTagId
        popularTag.setName("PopularTag1");

        popularTagsList.add(popularTag);
        when(tagsService.getPopularTags(4)).thenReturn(popularTagsList);

        // Act
        ResponseEntity<List<Tags>> response = tagsController.getPopularTags(4);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(popularTagsList, response.getBody());
        verify(tagsService, times(1)).getPopularTags(4);
    }



}
