package com.oryanend.tom_perfeito_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oryanend.tom_perfeito_api.dto.*;
import com.oryanend.tom_perfeito_api.repositories.MusicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static com.oryanend.tom_perfeito_api.factory.MusicDTOFactory.*;
import static com.oryanend.tom_perfeito_api.factory.UserDTOFactory.createUserDTOTemplate;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MusicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MusicRepository repository;

    private String baseUrl;
    private UUID existingId, nonExistingId;
    private String existingMusicName, nonExistingMusicName;
    private MusicDTO validMusicDTO;
    private MusicPatchDTO validMusicPatchDTO;
    private MusicDTO withoutTitleMusicDTO, withoutDescriptionMusicDTO, withoutReleaseDateMusicDTO, withoutLyricMusicDTO;

    private UserDTO validUserDTO, secondValidUserDTO;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @BeforeEach
    void setUp() {
        baseUrl = "/musics";

        nonExistingId = UUID.randomUUID();

        existingMusicName = "Imagine";
        nonExistingMusicName = "NonExistingMusicName123";

        validMusicDTO = createValidMusicDTO();
        validMusicPatchDTO = createValidMusicPatchDTO();

        withoutTitleMusicDTO = withoutTitleMusicDTO();
        withoutDescriptionMusicDTO = withoutDescriptionMusicDTO();
        withoutReleaseDateMusicDTO = withoutReleaseDateMusicDTO();
        withoutLyricMusicDTO = withoutLyricMusicDTO();

        validUserDTO = createUserDTOTemplate();
        secondValidUserDTO = createUserDTOTemplate();
    }

    // GET Tests
    @Test
    @DisplayName("GET `/musics` should return paged list of musics")
    void findAllShouldReturnPagedList() throws Exception {
        ResultActions result = mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
        ;
    }

    @Test
    @DisplayName("GET `/musics` should return paged list of musics sorted by `name`")
    void findByNameWhenContainsName() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Post a valid music to ensure there is at least one music with the specified name
        jsonBody = objectMapper.writeValueAsString(validMusicDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        postResult.andExpect(status().isCreated());

        // Extract the created music ID from the POST response
        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        MusicDTO createdMusic = objectMapper.readValue(postResponse, MusicDTO.class);
        existingId = createdMusic.getId();

        // GET request to find musics by name
        ResultActions getResult =
                mockMvc
                        .perform(get(baseUrl)
                                .param("name", validMusicDTO.getTitle())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        getResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value(validMusicDTO.getTitle()))
                .andExpect(jsonPath("$.content[0].description").value(validMusicDTO.getDescription()))
                .andExpect(jsonPath("$.content[0].link").exists())
                .andExpect(jsonPath("$.content[0].link").value(containsString(String.valueOf(existingId))))
        ;
    }

    @Test
    @DisplayName("GET `/musics` should return 404 when `name` doesn't exist")
    void findByNameWhenNameDoesntExists() throws Exception {
        ResultActions result =
                mockMvc
                        .perform(get(baseUrl)
                                .param("name", nonExistingMusicName)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No musics found with name containing: " + nonExistingMusicName))
                .andExpect(jsonPath("$.path").value(baseUrl))
        ;
    }

    @Test
    @DisplayName("GET `/musics/{id}` should return 200 when `id` doesn't exists")
    void findByIdWhenIdExists() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Post a valid music to ensure there is at least one music with the specified name
        jsonBody = objectMapper.writeValueAsString(validMusicDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        postResult.andExpect(status().isCreated());

        // Extract the created music ID from the POST response
        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        MusicDTO createdMusic = objectMapper.readValue(postResponse, MusicDTO.class);
        existingId = createdMusic.getId();

        ResultActions result =
                mockMvc
                        .perform(get(baseUrl + "/" + existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId.toString()))
                .andExpect(jsonPath("$.title").value(validMusicDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(validMusicDTO.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(validMusicDTO.getReleaseDate().toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.lyric").exists())
        ;

        // Check if createdAt and updatedAt are valid Instants
        String createdAtStr = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("createdAt").asText();
        String updatedAtStr = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("updatedAt").asText();

        assertDoesNotThrow(() -> Instant.parse(createdAtStr));
        assertDoesNotThrow(() -> Instant.parse(updatedAtStr));
    }

    @Test
    @DisplayName("GET `/musics/{id}` should return 404 when `id` doesn't exists")
    void findByIdWhenIdDoesntExists() throws Exception{
        ResultActions result =
                mockMvc
                        .perform(get(baseUrl + "/" + nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Music not found"))
                .andExpect(jsonPath("$.path").value(baseUrl + "/" + nonExistingId))
        ;
    }

    // POST Tests
    @Test
    @DisplayName("POST `/musics` should insert a new music")
    void insertNewMusic() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Post a valid music to ensure there is at least one music with the specified name
        jsonBody = objectMapper.writeValueAsString(validMusicDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        // Extract the created music ID from the POST response
        String postResponse = result.andReturn().getResponse().getContentAsString();
        MusicDTO createdMusic = objectMapper.readValue(postResponse, MusicDTO.class);
        existingId = createdMusic.getId();

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(validMusicDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(validMusicDTO.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(validMusicDTO.getReleaseDate().toString()))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.lyric").exists())
                .andExpect(jsonPath("$.lyric.text").value(validMusicDTO.getLyric().getText()))
                .andExpect(jsonPath("$.lyric.chords").isArray())
                .andExpect(jsonPath("$.lyric.chords.length()").value(2))
        ;

        // Check if createdAt and updatedAt are valid Instants
        String createdAtStr = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("createdAt").asText();
        String updatedAtStr = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("updatedAt").asText();

        assertDoesNotThrow(() -> Instant.parse(createdAtStr));
        assertDoesNotThrow(() -> Instant.parse(updatedAtStr));
    }

    @Test
    @DisplayName("POST `/musics` should return 422 when trying to insert a music without title")
    void insertMusicWithoutTitle() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(withoutTitleMusicDTO);

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
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName").value("title"))
                .andExpect(jsonPath("$.errors[0].message").value("Title cannot be null"))
        ;
    }

    @Test
    @DisplayName("POST `/musics` should return 422 when trying to insert a music without description")
    void insertMusicWithoutDescription() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(withoutDescriptionMusicDTO);

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
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName").value("description"))
                .andExpect(jsonPath("$.errors[0].message").value("Description cannot be null"))
        ;
    }

    @Test
    @DisplayName("POST `/musics` should return 422 when trying to insert a music without release date")
    void insertMusicWithoutReleaseDate() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(withoutReleaseDateMusicDTO);

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
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName").value("releaseDate"))
                .andExpect(jsonPath("$.errors[0].message").value("ReleaseDate cannot be null"))
        ;
    }

    @Test
    @DisplayName("POST `/musics` should return 422 when trying to insert a music without lyric")
    void insertMusicWithoutLyric() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(withoutLyricMusicDTO);

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
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName").value("lyric"))
                .andExpect(jsonPath("$.errors[0].message").value("Lyric cannot be null"))
        ;
    }

    // PATCH Tests
    @Test
    @DisplayName("PATCH `/musics/{id}` should update music when given valid credentials")
    void updateMusicWithValidCredentials() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Post a valid music to ensure there is at least one music with the specified name
        jsonBody = objectMapper.writeValueAsString(validMusicDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        // Extract the created music ID from the POST response
        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        Instant createdAt = Instant.parse(objectMapper.readTree(postResponse).get("createdAt").asText());
        MusicDTO createdMusic = objectMapper.readValue(postResponse, MusicDTO.class);
        existingId = createdMusic.getId();

        // Update some fields of the created music
        jsonBody = objectMapper.writeValueAsString(validMusicPatchDTO);
        ResultActions result =
                mockMvc
                        .perform(patch(baseUrl + "/" + existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(validMusicPatchDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(validMusicPatchDTO.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(validMusicPatchDTO.getReleaseDate().toString()))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.lyric").exists())
                .andExpect(jsonPath("$.lyric.text").value(validMusicPatchDTO.getLyric().getText()))
                .andExpect(jsonPath("$.lyric.chords").isArray())
                .andExpect(jsonPath("$.lyric.chords.length()").value(2))
        ;

        // Assert that updatedAt is after createdAt
        Instant updatedAt = Instant.parse(objectMapper.readTree(
                result.andReturn().getResponse().getContentAsString()).get("updatedAt").asText());
        assertTrue(updatedAt.isAfter(createdAt));

        // Assert that releaseDate is valid
        String releaseDateStr = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("releaseDate").asText();
        assertDoesNotThrow(() -> LocalDate.parse(releaseDateStr));
    }

    @Test
    @DisplayName("PATCH `/musics/{id}` should return 403 when given invalid credentials")
    void updateMusicWithInvalidCredentials() throws Exception{
        // Create first user
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the first user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Post a valid music to ensure there is at least one music with the specified name
        jsonBody = objectMapper.writeValueAsString(validMusicDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        postResult.andExpect(status().isCreated());

        // Extract the created music ID from the POST response
        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        MusicDTO createdMusic = objectMapper.readValue(postResponse, MusicDTO.class);
        existingId = createdMusic.getId();

        // Create second user
        jsonBody = objectMapper.writeValueAsString(secondValidUserDTO);

        ResultActions createSecondUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createSecondUserResult.andExpect(status().isCreated());

        // Try to get token with the second user
        tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", secondValidUserDTO.getEmail())
                                .param("password", secondValidUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Update some fields of the created music
        jsonBody = objectMapper.writeValueAsString(validMusicPatchDTO);
        ResultActions result =
                mockMvc
                        .perform(patch(baseUrl + "/" + existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Unauthorized action"))
                .andExpect(jsonPath("$.message").value("Access denied. Should be self or admin"))
                .andExpect(jsonPath("$.path").value(baseUrl + "/" + existingId))
        ;

    }

    @Test
    @DisplayName("PATCH `/musics/{id}` should return 404 when `id` doesn't exist")
    void updateMusicWithNonExistId() throws Exception {
        // Create first user
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the first user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        jsonBody = objectMapper.writeValueAsString(validMusicPatchDTO);
        ResultActions result =
                mockMvc
                        .perform(patch(baseUrl + "/" + nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Music not found"))
                .andExpect(jsonPath("$.path").value(baseUrl + "/" + nonExistingId))
        ;
    }

    // DELETE Tests
    @Test
    @DisplayName("DELETE `/musics/{id}` should delete music when given valid credentials")
    void deleteMusicWithValidCredentials() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Post a valid music to ensure there is at least one music with the specified name
        jsonBody = objectMapper.writeValueAsString(validMusicDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        // Extract the created music ID from the POST response
        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        MusicDTO createdMusic = objectMapper.readValue(postResponse, MusicDTO.class);
        existingId = createdMusic.getId();

        // Delete the created music
        ResultActions result =
                mockMvc
                        .perform(delete(baseUrl + "/" + existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
        ;
        // Verify that the music is actually deleted
        assertFalse(repository.existsById(existingId));
    }

    @Test
    @DisplayName("DELETE `/musics/{id}` should return 404 when `id` doesn't exist")
    void deleteMusicWithNonExistId() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions createUserResult =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        // Post a valid music to ensure there is at least one music with the specified name
        jsonBody = objectMapper.writeValueAsString(validMusicDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        // Extract the created music ID from the POST response
        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        MusicDTO createdMusic = objectMapper.readValue(postResponse, MusicDTO.class);
        existingId = createdMusic.getId();

        // Delete the created music
        ResultActions result =
                mockMvc
                        .perform(delete(baseUrl + "/" + nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + objectMapper.readTree(
                                        tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText())
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Music not found"))
                .andExpect(jsonPath("$.path").value(baseUrl + "/" + nonExistingId))
        ;
    }
}
