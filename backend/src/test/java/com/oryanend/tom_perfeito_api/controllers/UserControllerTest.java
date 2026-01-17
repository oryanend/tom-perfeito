package com.oryanend.tom_perfeito_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oryanend.tom_perfeito_api.config.PasswordConfig;
import com.oryanend.tom_perfeito_api.dto.UserDTO;
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

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private String secondValidUsername, secondValidEmail, secondValidPassword;
    private String invalidUsername, invalidEmail, invalidPassword;

    @BeforeEach
    void setUp() {
        baseUrl = "/users";

        invalidUsername = "iu";
        invalidPassword = "123";
        invalidEmail = "mail";

        validUsername = "testuser";
        validEmail = "email@test.com";
        validPassword = "testpassword";

        secondValidUsername = "anotheruser";
        secondValidEmail = "anotheremail@test.com";
        secondValidPassword = "anotherpassword";

        validUserDTO = new UserDTO(validUsername, validEmail, validPassword, null, null);

        secondValidUserWithSameUsernameDTO = new UserDTO(validUsername, secondValidEmail, secondValidPassword, null, null);
        secondValidUserWithSameEmailDTO = new UserDTO(secondValidUsername, validEmail, secondValidPassword, null, null);

        nullPasswordUserDTO = new UserDTO(validUsername, validEmail, null, null, null);
        nullEmailUserDTO = new UserDTO(validUsername, null, validPassword, null, null);
        nullUsernameUserDTO = new UserDTO(null, validEmail, validPassword, null, null);

        invalidPasswordUserDTO = new UserDTO(validUsername, validEmail, invalidPassword, null, null);
        invalidEmailUserDTO = new UserDTO(validUsername, invalidEmail, validPassword, null, null);
        invalidUsernameUserDTO = new UserDTO(invalidUsername, validEmail, validPassword, null, null);
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
}
