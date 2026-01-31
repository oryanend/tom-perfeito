package com.oryanend.tom_perfeito_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oryanend.tom_perfeito_api.dto.ChordDTO;
import com.oryanend.tom_perfeito_api.repositories.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.oryanend.tom_perfeito_api.factory.ChordDTOFactory.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ChordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    private String baseUrl;
    private String validChordName, nonExistingChordName;
    private ChordDTO validChordDTO, withoutNotesChordDTO, withoutNameChordDTO, withoutTypeChordDTO;


    @BeforeEach
    void setUp() {
        baseUrl = "/chords";

        validChordName = "A Minor";
        nonExistingChordName = "NonExistingChordName";

        validChordDTO = createValidChordDTO(noteRepository);

        withoutNotesChordDTO = createWithoutNotesChordDTO();
        withoutNameChordDTO = createWithoutNameChordDTO(noteRepository);
        withoutTypeChordDTO = createWithoutTypeChordDTO(noteRepository);
    }

    // Tests for `/chords` endpoint
    @Test
    @DisplayName("GET `/chords` should return paginated list of chords")
    void findAllChords() throws Exception {
        ResultActions result = mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].type").exists())
                .andExpect(jsonPath("$.content[0].notes").isArray());
    }

    // Tests for `/chords/search` endpoint
    @Test
    @DisplayName("GET `/chords/search` should return chords matching name with valid parameter")
    void searchChordsWithName() throws Exception {
        ResultActions result =
                mockMvc.perform(get(baseUrl + "/search")
                                .param("name", validChordName)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value(validChordName))
                .andExpect(jsonPath("$[0].type").exists())
                .andExpect(jsonPath("$[0].notes").isArray())
                .andExpect(jsonPath("$[0].notes[0].id").value(1))
                .andExpect(jsonPath("$[0].notes[0].name").value("C"))
                .andExpect(jsonPath("$[0].notes[1].id").value(5))
                .andExpect(jsonPath("$[0].notes[1].name").value("E"))
                .andExpect(jsonPath("$[0].notes[2].id").value(10))
                .andExpect(jsonPath("$[0].notes[2].name").value("A"))
                .andExpect(jsonPath("$[0].notes[0].accidental").value("NATURAL"))
                .andExpect(jsonPath("$[0].notes[1].accidental").value("NATURAL"))
                .andExpect(jsonPath("$[0].notes[2].accidental").value("NATURAL"))
        ;
    }

    @Test
    @DisplayName("GET `/chords/search` should return any result when searching with non-existing name")
    void searchChordWithNonExistingName() throws Exception {
        ResultActions result =
                mockMvc.perform(get(baseUrl + "/search")
                        .param("name", nonExistingChordName)
                        .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
        ;
    }

    // Tests for `/chords` POST endpoint
    @Test
    @DisplayName("POST `/chords` should insert a new chord")
    void insertNewChord() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validChordDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(validChordDTO.getName()))
                .andExpect(jsonPath("$.type").value(validChordDTO.getType().toString()))
                .andExpect(jsonPath("$.notes").isArray())
                .andExpect(jsonPath("$.notes[0].id").value(1))
                .andExpect(jsonPath("$.notes[0].name").value("C"))
                .andExpect(jsonPath("$.notes[1].id").value(5))
                .andExpect(jsonPath("$.notes[1].name").value("E"))
                .andExpect(jsonPath("$.notes[2].id").value(10))
                .andExpect(jsonPath("$.notes[2].name").value("A"))
        ;
    }

    @Test
    @DisplayName("POST `/chords` should return 400 Bad Request when inserting a chord without notes")
    void insertWithoutNotes() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(withoutNotesChordDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("notes"))
                .andExpect(jsonPath("$.errors[0].message").value("A chord must have at least three note"))
        ;

    }

    @Test
    @DisplayName("POST `/chords` should return 400 Bad Request when inserting a chord without type")
    void insertWithoutType() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(withoutTypeChordDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("type"))
                .andExpect(jsonPath("$.errors[0].message").value("Chord type cannot be null"))
        ;
    }

    @Test
    @DisplayName("POST `/chords` should return 400 Bad Request when inserting a chord without name")
    void insertWithoutName() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(withoutNameChordDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Chord name cannot be null"))
        ;
    }
}
