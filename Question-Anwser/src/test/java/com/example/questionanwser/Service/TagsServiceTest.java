package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Tags;
import com.example.questionanwser.Repository.TagsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagsServiceTest {

    @InjectMocks
    private TagsService tagsService;

    @Mock
    private TagsRepository tagsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTags() {
        // Arrange
        Tags tag1 = new Tags();
        tag1.setName("Tag1");
        Tags tag2 = new Tags();
        tag2.setName("Tag2");

        when(tagsRepository.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        // Act
        List<Tags> tags = tagsService.getAllTags();

        // Assert
        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertEquals("Tag1", tags.get(0).getName());
        assertEquals("Tag2", tags.get(1).getName());
    }

    @Test
    void getTagById() {
        // Arrange
        Tags tag = new Tags();
        tag.setName("Tag1");
        when(tagsRepository.findById(1L)).thenReturn(Optional.of(tag));

        // Act
        Optional<Tags> foundTag = tagsService.getTagById(1L);

        // Assert
        assertTrue(foundTag.isPresent());
        assertEquals("Tag1", foundTag.get().getName());
    }

    @Test
    void createTag() {
        // Arrange
        Tags tag = new Tags();
        tag.setName("Tag1");
        when(tagsRepository.save(any(Tags.class))).thenReturn(tag);

        // Act
        Tags createdTag = tagsService.createTag(tag);

        // Assert
        assertNotNull(createdTag);
        assertEquals("Tag1", createdTag.getName());
        verify(tagsRepository, times(1)).save(tag);
    }

    @Test
    void deleteTag() {
        // Arrange
        Long id = 1L;

        // Act
        tagsService.deleteTag(id);

        // Assert
        verify(tagsRepository, times(1)).deleteById(id);
    }

    @Test
    void getTagsByName() {
        // Arrange
        Tags tag = new Tags();
        tag.setName("Tag1");
        when(tagsRepository.findByNameContainingIgnoreCase("tag")).thenReturn(Arrays.asList(tag));

        // Act
        List<Tags> foundTags = tagsService.getTagsByName("tag");

        // Assert
        assertNotNull(foundTags);
        assertEquals(1, foundTags.size());
        assertEquals("Tag1", foundTags.get(0).getName());
    }

    @Test
    void getOrCreateTag_CreatesNewTag() {
        // Arrange
        String tagName = "NewTag";
        when(tagsRepository.findByNameIgnoreCase(tagName)).thenReturn(null); // Tag doesn't exist

        Tags newTag = new Tags();
        newTag.setName(tagName);
        when(tagsRepository.save(any(Tags.class))).thenReturn(newTag);

        // Act
        Tags result = tagsService.getOrCreateTag(tagName);

        // Assert
        assertNotNull(result);
        assertEquals("NewTag", result.getName());
        verify(tagsRepository, times(1)).save(any(Tags.class));
    }

    @Test
    void getOrCreateTag_ReturnsExistingTag() {
        // Arrange
        String tagName = "ExistingTag";
        Tags existingTag = new Tags();
        existingTag.setName(tagName);
        when(tagsRepository.findByNameIgnoreCase(tagName)).thenReturn(existingTag); // Tag exists

        // Act
        Tags result = tagsService.getOrCreateTag(tagName);

        // Assert
        assertNotNull(result);
        assertEquals("ExistingTag", result.getName());
        verify(tagsRepository, times(0)).save(any(Tags.class)); // Ensure save is not called
    }

    @Test
    void getPopularTags() {
        // Arrange
        Tags tag1 = new Tags();
        tag1.setName("PopularTag1");
        Tags tag2 = new Tags();
        tag2.setName("PopularTag2");

        when(tagsRepository.findPopularTags(any())).thenReturn(new PageImpl<>(Arrays.asList(tag1, tag2)));

        // Act
        List<Tags> popularTags = tagsService.getPopularTags(2);

        // Assert
        assertNotNull(popularTags);
        assertEquals(2, popularTags.size());
        assertEquals("PopularTag1", popularTags.get(0).getName());
        assertEquals("PopularTag2", popularTags.get(1).getName());
    }
}
