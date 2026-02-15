package com.oryanend.tom_perfeito_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oryanend.tom_perfeito_api.dto.CommentDTO;
import com.oryanend.tom_perfeito_api.dto.MusicDTO;
import com.oryanend.tom_perfeito_api.dto.UserDTO;
import com.oryanend.tom_perfeito_api.repositories.CommentRepository;
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
import java.util.UUID;

import static com.oryanend.tom_perfeito_api.factory.MusicDTOFactory.createValidMusicDTO;
import static com.oryanend.tom_perfeito_api.factory.UserDTOFactory.createUserDTOTemplate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository repository;

    private String baseUrl, baseMusicUrl, baseAuthUrl, baseLoginAuthUrl;
    private CommentDTO validCommentDTO;
    private MusicDTO validMusicDTO;
    private UserDTO validUserDTO, secondValidUserDTO;
    private UUID existingId, nonExistingId;

    private Long commentExistingId, commentNonExistingId;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @BeforeEach
    void setUp() {
        baseMusicUrl = "/musics";
        baseAuthUrl = "/auth/register";
        baseLoginAuthUrl = "/auth/login";

        validMusicDTO = createValidMusicDTO();

        validUserDTO = createUserDTOTemplate();
        secondValidUserDTO = createUserDTOTemplate();

        validCommentDTO = new CommentDTO();
        validCommentDTO.setBody("This is a valid comment.");

        commentNonExistingId = 9999L;

        nonExistingId = UUID.randomUUID();
    }

    // GET test
    @Test
    @DisplayName("GET `/comments/{id}` should return comment by ID")
    void getCommentByExistingId() throws Exception {
        // Get token user
        String registerAndGetToken = registerAndGetToken(validUserDTO);

        // Extract the created music ID from the POST response
        MusicDTO createdMusic = createMusic(validMusicDTO, registerAndGetToken);
        existingId = createdMusic.getId();

        // Insert comment
        CommentDTO createdComment = createComment(existingId, validCommentDTO, registerAndGetToken);
        Long commentId = createdComment.getId();

        // Perform GET request to retrieve the comment by ID
        ResultActions getResult = mockMvc.perform(get(baseMusicUrl + "/" + existingId + "/comments/" + commentId));

        getResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.body").value(validCommentDTO.getBody()))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
        ;
    }

    @Test
    @DisplayName("GET `/comments/{id}` should return 404 when comment ID does not exist")
    void getCommentByNonExistingId() throws Exception {
        // Perform GET request to retrieve the comment by non-existing ID
        ResultActions result = mockMvc.perform(get(baseMusicUrl + "/" + existingId + "/comments/" + commentNonExistingId));

        baseUrl = baseMusicUrl + "/" + existingId + "/comments/" + commentNonExistingId;

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Comment not found"))
                .andExpect(jsonPath("$.path").value(baseUrl))
        ;
    }

    @Test
    @DisplayName("GET `/comments` should return paged comments")
    void getComments() throws Exception {
        // Perform GET request to retrieve comments
        ResultActions result = mockMvc.perform(get(baseMusicUrl + "/" + existingId + "/comments"));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.last").exists())
        ;
    }

    // POST test
    @Test
    @DisplayName("POST `/comments` should create a new comment")
    void postComment() throws Exception {
        // Get token user
        String registerAndGetToken = registerAndGetToken(validUserDTO);

        // Extract the created music ID from the POST response
        MusicDTO createdMusic = createMusic(validMusicDTO, registerAndGetToken);
        existingId = createdMusic.getId();

        // Insert comment
        createComment(existingId, validCommentDTO, registerAndGetToken);
    }

    @Test
    @DisplayName("POST `/comments` should return 404 when music ID does not exist")
    void postCommentWhenMusicIdDoesntExists() throws Exception {
        // Get token user
        String registerAndGetToken = registerAndGetToken(validUserDTO);

        // Extract the created music ID from the POST response
        MusicDTO createdMusic = createMusic(validMusicDTO, registerAndGetToken);
        existingId = createdMusic.getId();

        // Insert comment
        CommentDTO commentDTO = createComment(existingId, validCommentDTO, registerAndGetToken);
        String jsonBody = objectMapper.writeValueAsString(commentDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseMusicUrl + "/" + nonExistingId + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + registerAndGetToken)
                                .accept(MediaType.APPLICATION_JSON));

        postResult
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Music not found"))
                .andExpect(jsonPath("$.path").value(baseMusicUrl + "/" + nonExistingId + "/comments"))
        ;
    }

    @Test
    @DisplayName("POST `/comments` should return 201 when your comment is a reply to another comment")
    void postCommentAsReply() throws Exception {
        // Get token user
        String registerAndGetToken = registerAndGetToken(validUserDTO);

        // Extract the created music ID from the POST response
        MusicDTO createdMusic = createMusic(validMusicDTO, registerAndGetToken);
        existingId = createdMusic.getId();

        // Insert comment
        CommentDTO commentDTO = createComment(existingId, validCommentDTO, registerAndGetToken);

        // Second User creates a comment to be the parent comment and Create a reply comment DTO
        String secondUserToken = registerAndGetToken(secondValidUserDTO);
        CommentDTO replyDTO = createReplyComment(commentDTO, secondUserToken);

        // Check if the parent comment has the reply
        ResultActions getResult = mockMvc.perform(get(baseMusicUrl + "/" + existingId + "/comments/" + commentDTO.getId()));

        getResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDTO.getId()))
                .andExpect(jsonPath("$.body").value(commentDTO.getBody()))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.author").exists())
                .andExpect(jsonPath("$.author.id").value(commentDTO.getAuthor().getId().toString()))
                .andExpect(jsonPath("$.replies").isArray())
                .andExpect(jsonPath("$.replies").isNotEmpty())
                .andExpect(jsonPath("$.replies[0].body").value(replyDTO.getBody()))
                .andExpect(jsonPath("$.replies[0].author.id").value(replyDTO.getAuthor().getId().toString()))
        ;
    }

    // DELETE test
    @Test
    @DisplayName("DELETE `/comments/{id}` should delete comment by ID")
    void deleteComment() throws Exception {
        // Get token user
        String registerAndGetToken = registerAndGetToken(validUserDTO);

        // Post a valid music to ensure there is at least one music with the specified name
        MusicDTO createdMusic = createMusic(validMusicDTO, registerAndGetToken);
        existingId = createdMusic.getId();

        // Insert comment
        CommentDTO createdComment = createComment(existingId, validCommentDTO, registerAndGetToken);
        Long commentId = createdComment.getId();

        // Perform DELETE request to delete the comment by ID
        ResultActions deleteResult = mockMvc.perform(delete(baseMusicUrl + "/" + existingId + "/comments/" + commentId)
                .header("Authorization", "Bearer " + registerAndGetToken)
                .accept(MediaType.APPLICATION_JSON));

        deleteResult
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
        ;

        // Verify that the music is actually deleted
        assertFalse(repository.findById(commentId).isPresent());
    }

    @Test
    @DisplayName("DELETE `/comments/{id}` should return 404 when comment ID does not exist")
    void deleteCommentWhenIdDoesntExists() throws Exception {
        // Get token user
        String registerAndGetToken = registerAndGetToken(validUserDTO);

        // Perform DELETE request to delete the comment by ID
        ResultActions deleteResult = mockMvc.perform(delete(baseMusicUrl + "/" + existingId + "/comments/" + commentNonExistingId)
                .header("Authorization", "Bearer " + registerAndGetToken)
                .accept(MediaType.APPLICATION_JSON));

        deleteResult
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Comment not found"))
                .andExpect(jsonPath("$.path").value(baseMusicUrl + "/" + existingId + "/comments/" + commentNonExistingId))
        ;
    }

    // PATCH test
    @Test
    void patchComment() throws Exception {
        // Get token user
        String firstUserToken = registerAndGetToken(validUserDTO);

        // Extract the created music ID from the POST response
        MusicDTO createdMusic = createMusic(validMusicDTO, firstUserToken);
        existingId = createdMusic.getId();

        // Insert comment
        CommentDTO commentDTO = createComment(existingId, validCommentDTO, firstUserToken);
        Long commentId = commentDTO.getId();

        // Patch comment
        String updatedBody = "This is an updated comment body.";
        CommentDTO patchDTO = new CommentDTO();
        patchDTO.setBody(updatedBody);

        Instant createdAt = commentDTO.getCreatedAt();

        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        ResultActions patchResult =
                mockMvc
                        .perform(patch(baseMusicUrl + "/" + existingId + "/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + firstUserToken)
                                .accept(MediaType.APPLICATION_JSON));

        patchResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.body").value(updatedBody))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
        ;

        // Assert that updatedAt is after createdAt
        Instant updatedAt = Instant.parse(objectMapper.readTree(
                patchResult.andReturn().getResponse().getContentAsString()).get("updatedAt").asText());
        assertTrue(updatedAt.isAfter(createdAt));
    }

    // Methods to help tests
    private String obtainAcessToken(String email, String password) throws Exception {
        ResultActions tokenResult =
                mockMvc
                        .perform(post(baseLoginAuthUrl).with(httpBasic(clientId, clientSecret))
                                .param("email", email)
                                .param("password", password)
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult.andExpect(status().isOk());

        return objectMapper.readTree(tokenResult.andReturn().getResponse().getContentAsString()).get("access_token").asText();
    }

    private UserDTO registerUser(UserDTO dto) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions createUserResult =
                mockMvc
                        .perform(post(baseAuthUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        createUserResult.andExpect(status().isCreated());

        String response = createUserResult.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, UserDTO.class);
    }

    private String registerAndGetToken(UserDTO dto) throws Exception {
        UserDTO registeredUser = registerUser(dto);
        return obtainAcessToken(registeredUser.getEmail(), dto.getPassword());
    }

    private MusicDTO createMusic(MusicDTO dto, String token) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseMusicUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + token)
                                .accept(MediaType.APPLICATION_JSON));

        postResult.andExpect(status().isCreated());

        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(postResponse, MusicDTO.class);
    }

    private CommentDTO createComment(UUID musicId, CommentDTO dto, String token) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseMusicUrl + "/" + musicId + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .header("Authorization", "Bearer " + token)
                                .accept(MediaType.APPLICATION_JSON));

        postResult
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.body").value(dto.getBody()))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.author").exists())
                .andExpect(jsonPath("$.author.id").exists())
                .andExpect(jsonPath("$.music").exists())
        ;

        String postResponse = postResult.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(postResponse, CommentDTO.class);
    }

    private CommentDTO createReplyComment(CommentDTO dto, String token) throws Exception {
        CommentDTO replyDTO = new CommentDTO();
        replyDTO.setBody("This is a reply to the comment with ID " + dto.getId());
        replyDTO.setParentId(dto.getId());
        dto.addReply(replyDTO);
        replyDTO = createComment(dto.getMusic().getId(), replyDTO, token);
        String jsonBody = objectMapper.writeValueAsString(replyDTO);

        return objectMapper.readValue(jsonBody, CommentDTO.class);
    }
}
