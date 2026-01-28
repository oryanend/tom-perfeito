package com.oryanend.tom_perfeito_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oryanend.tom_perfeito_api.config.PasswordConfig;
import com.oryanend.tom_perfeito_api.dto.UserDTO;
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

import static com.oryanend.tom_perfeito_api.factory.UserDTOFactory.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordConfig passwordConfig;

    private String baseUrl;

    private UserDTO validUserDTO, secondValidUserWithSameUsernameDTO, secondValidUserWithSameEmailDTO;
    private UserDTO nullPasswordUserDTO, nullEmailUserDTO, nullUsernameUserDTO;
    private UserDTO invalidUsernameUserDTO, invalidEmailUserDTO, invalidPasswordUserDTO;

    private String validUsername, validEmail, validPassword;
    private String invalidUsername, invalidEmail, invalidPassword;
    private String invalidToken;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @BeforeEach
    void setUp() {
        baseUrl = "/users";

        invalidUsername = "iu";
        invalidPassword = "123";
        invalidEmail = "mail";

        validUsername = "testuser";
        validEmail = "email@test.com";
        validPassword = "testpassword";

        validUserDTO = createUserDTO(validUsername, validEmail, validPassword);

        secondValidUserWithSameUsernameDTO = createUserDTOWithUsername(validUsername);
        secondValidUserWithSameEmailDTO = createUserDTOWithEmail(validEmail);

        nullPasswordUserDTO = createUserDTOWithPassword(null);
        nullEmailUserDTO = createUserDTOWithEmail(null);
        nullUsernameUserDTO = createUserDTOWithUsername(null);

        invalidPasswordUserDTO = createUserDTOWithPassword(invalidPassword);
        invalidEmailUserDTO = createUserDTOWithEmail(invalidEmail);
        invalidUsernameUserDTO = createUserDTOWithUsername(invalidUsername);

        invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";
    }

    @Test
    @DisplayName("POST `/users` with valid data should return success")
    void insertUser() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(validUsername))
                .andExpect(jsonPath("$.email").value(validEmail))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[*].authority", hasItem("ROLE_CLIENT")));

        // Check if the returned id is a valid UUID
        String id = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("id").asText();
        assertDoesNotThrow(() -> UUID.fromString(id));

        // Check if the password is encoded correctly
        assertTrue(passwordConfig.passwordEncoder().matches(validPassword,
                result.andReturn()
                        .getResponse()
                        .getContentAsString()
                        .contains("password") ? objectMapper.readTree(result.andReturn()
                        .getResponse()
                        .getContentAsString()).get("password").asText() : ""));

        // Check if createdAt and updatedAt are valid Instants
        String createdAtStr = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("createdAt").asText();
        String updatedAtStr = objectMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("updatedAt").asText();
        assertDoesNotThrow(() -> Instant.parse(createdAtStr));
        assertDoesNotThrow(() -> Instant.parse(updatedAtStr));
    }

    @Test
    @DisplayName("POST `/users` without password should return unprocessable entity")
    void insertWithoutPassword() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(nullPasswordUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("Password cannot be null"));
    }

    @Test
    @DisplayName("POST `/users` without email should return unprocessable entity")
    void insertWithoutEmail() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(nullEmailUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("Email cannot be null"));
    }

    @Test
    @DisplayName("POST `/users` without username should return unprocessable entity")
    void insertWithoutUsername() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(nullUsernameUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("username"))
                .andExpect(jsonPath("$.errors[0].message").value("Username cannot be null"));
    }

    @Test
    @DisplayName("POST `/users` with a invalid password should return unprocessable entity")
    void insertWithInvalidPassword() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(invalidPasswordUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("Password must have 5 characters at least"));
    }

    @Test
    @DisplayName("POST `/users` with a invalid password should return unprocessable entity")
    void insertWithInvalidEmail() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(invalidEmailUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[*].fieldName").value(hasItem("email")))
                .andExpect(jsonPath("$.errors[*].message").value(hasItem("Email must be between 5 and 254 characters")))
                .andExpect(jsonPath("$.errors[*].message").value(hasItem("Email should be valid")));
    }

    @Test
    @DisplayName("POST `/users` with a invalid username should return unprocessable entity")
    void insertWithInvalidUsername() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(invalidUsernameUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("username"))
                .andExpect(jsonPath("$.errors[0].message").value("Username must be between 3 and 40 characters"));
    }

    @Test
    @DisplayName("POST `/users` with an already taken email should return bad request")
    void insertWithAlreadyTakenEmail() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());

        // Try to insert the same user again

        String secondJsonBody = objectMapper.writeValueAsString(secondValidUserWithSameEmailDTO);

        ResultActions secondResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondJsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        secondResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Resource already exists"))
                .andExpect(jsonPath("$.message").value("Email already in use, try another one."))
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("POST `/users` with an already taken username should return bad request")
    void insertWithAlreadyTakenUsername() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());

        // Try to insert the same user again

        String secondJsonBody = objectMapper.writeValueAsString(secondValidUserWithSameUsernameDTO);

        ResultActions secondResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondJsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        secondResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Resource already exists"))
                .andExpect(jsonPath("$.message").value("Username already in use, try another one."))
                .andExpect(jsonPath("$.path").value(baseUrl))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // GET Tests
    @Test
    @DisplayName("GET `/users/me` should return the authenticated user's data")
    void getMeWithValidToken() throws Exception {
        // First, insert a valid user
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        postResult.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post("/oauth2/token").with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").isNumber())
                .andExpect(jsonPath("$.access_token").isString())
        ;

        // Now, authenticate as that user and call /users/me
        ResultActions getResult =
                mockMvc
                        .perform(get(baseUrl + "/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization",
                                        "Bearer " +
                                                objectMapper.readTree(
                                                        tokenResult.andReturn()
                                                                .getResponse()
                                                                .getContentAsString()
                                                ).get("access_token").asText()
                                )
                                .accept(MediaType.APPLICATION_JSON));

        getResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(validUsername))
                .andExpect(jsonPath("$.email").value(validEmail))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[*].authority", hasItem("ROLE_CLIENT")));
    }

    @Test
    @DisplayName("GET `/users/me` with invalid token should return unauthorized")
    void getMeWithInvalidToken() throws Exception {
        ResultActions getResult =
                mockMvc
                        .perform(get(baseUrl + "/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + invalidToken)
                                .accept(MediaType.APPLICATION_JSON));

        getResult
                .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    @DisplayName("GET `/users/{id}` with existing id should return user")
    void getUserWithExistingId() throws Exception {
        // First, insert a valid user
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions postResult =
                mockMvc
                        .perform(post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        postResult.andExpect(status().isCreated());

        String id = objectMapper.readTree(postResult.andReturn().getResponse().getContentAsString()).get("id").asText();

        // Now, try to get the user by id
        ResultActions getResult =
                mockMvc
                        .perform(get(baseUrl + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        getResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value(validUsername))
                .andExpect(jsonPath("$.email").value(validEmail))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[*].authority", hasItem("ROLE_CLIENT")));
    }

    @Test
    @DisplayName("GET `/users/{id}` with non existing id should return 404")
    void getUserWithNonExistingId() throws Exception {
        String nonExistingId = UUID.randomUUID().toString();

        ResultActions getResult =
                mockMvc
                        .perform(get(baseUrl + "/" + nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        getResult
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value(baseUrl + "/" + nonExistingId))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
