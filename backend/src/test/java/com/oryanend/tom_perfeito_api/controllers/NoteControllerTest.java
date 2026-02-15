package com.oryanend.tom_perfeito_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oryanend.tom_perfeito_api.dto.NoteDTO;
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

import static com.oryanend.tom_perfeito_api.factory.NoteDTOFactory.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private NoteDTO validNoteDTO, nullNameNoteDTO, nullAccidentalNoteDTO;

    @BeforeEach
    void setUp() {
        baseUrl = "/notes";

        validNoteDTO = createValidNoteDTO();
        nullNameNoteDTO = createNullNameNoteDTO();
        nullAccidentalNoteDTO = createNullAccidentalDTO();
    }

    @Test
    @DisplayName("GET `/notes` should return list of notes")
    void findAllNotes() throws Exception {
        ResultActions result = mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].name").value("C"))
                .andExpect(jsonPath("$[0].accidental").isNotEmpty())
                .andExpect(jsonPath("$[0].accidental").value("NATURAL"))
        ;
    }

    @Test
    @DisplayName("POST `/notes` should insert a new note and return it when note is valid")
    void insertNote() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validNoteDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(13))
                .andExpect(jsonPath("$.name").value("B"))
                .andExpect(jsonPath("$.accidental").value("NATURAL"))
                .andExpect(jsonPath("$.chords").isArray())
        ;
    }

    @Test
    @DisplayName("POST `/notes` SHOULD return 422 WHEN note `name` is null")
    void insertNoteWithoutName() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(nullNameNoteDTO);

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
                .andExpect(jsonPath("$.errors[0].message").value("Name cannot be null"))
        ;
    }

    @Test
    @DisplayName("POST `/notes` SHOULD return 422 WHEN note `accidental` is null")
    void insertNoteWithoutAccidental() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(nullAccidentalNoteDTO);

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
                .andExpect(jsonPath("$.errors[0].fieldName").value("accidental"))
                .andExpect(jsonPath("$.errors[0].message").value("Accidental cannot be null"))
        ;
    }
}
