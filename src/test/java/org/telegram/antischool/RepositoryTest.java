package org.telegram.antischool;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.antischool.dto.WordItem;
import org.telegram.antischool.repositories.WordRepository;
import org.telegram.antischool.services.WordService;
import org.telegram.antischool.services.impl.WordServiceImpl;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


@SpringBootTest
public class RepositoryTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    WordService wordService;
    @MockBean
    private WordRepository dbRepository;

    @Test
    public void getToLearnWords_shouldReturnWordItems() {
        int count = 3; // how many words you want to fetch

        Flux<List<WordItem>> result = wordService.getToLearnWords(count);

        StepVerifier.create(result)
                .expectNextMatches(list -> !list.isEmpty() && list.get(0).getWord() != null)
                .expectNextCount(2) // adjust based on expected messages
                .verifyComplete();

        verify(dbRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    public void translateWords_shouldSaveWord() {
        Flux<List<WordItem>> result = wordService.translateWord("apple");

        StepVerifier.create(result)
                .expectNextMatches(list -> !list.isEmpty()
                        && list.get(0).getWord() != null
                && list.get(0).getTranslation().equals("яблоко"))
                .verifyComplete();
    }

    @Test
    public void getLearnedWords_shouldReturnWordItems() {
        int count = 2;

        Flux<List<WordItem>> result = wordService.getLearnedWords(count);

        StepVerifier.create(result)
                .expectNextMatches(list -> !list.isEmpty() && list.get(0).getWord() != null)
                .verifyComplete();
    }
    @Test
    public void shouldDeserializeSimpleFields() throws Exception {
        String json = """
            {
              "Id": 101,
              "Word": "apple",
              "Translation": "яблоко",
              "Audio": "audio.mp3",
              "Status": "NEW",
              "LearnedWords": 3,
              "WordsToStudied": 7
            }
        """;

        WordItem item = mapper.readValue(json, WordItem.class);

        assertEquals(101, item.getId());
        assertEquals("apple", item.getWord());
        assertEquals("яблоко", item.getTranslation());
        assertEquals("audio.mp3", item.getAudio());
        assertEquals("NEW", item.getStatus());
        assertEquals(3, item.getLearnedWords());
        assertEquals(7, item.getWordsToStudied());
    }

    @Test
    public void shouldExtractTranslationFromTranslatesArray() throws Exception {
        String json = """
            {
              "Id": 55,
              "SourceText": "run",
              "Translates": [
                { "Text": "бегать" },
                { "Text": "запускать" }
              ]
            }
        """;

        WordItem item = mapper.readValue(json, WordItem.class);

        assertEquals(55, item.getId());
        assertEquals("run", item.getWord());
        assertEquals("бегать", item.getTranslation()); // picks first element
    }

    @Test
    public void shouldIgnoreUnknownProperties() throws Exception {
        String json = """
            {
              "Id": 12,
              "Word": "cat",
              "UnknownField": "shouldBeIgnored"
            }
        """;

        WordItem item = mapper.readValue(json, WordItem.class);

        assertEquals(12, item.getId());
        assertEquals("cat", item.getWord());
        assertNull(item.getTranslation()); // Not present
    }
}
