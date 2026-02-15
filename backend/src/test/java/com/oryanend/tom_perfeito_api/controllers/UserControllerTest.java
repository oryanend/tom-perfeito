package com.oryanend.tom_perfeito_api.controllers;

import static com.oryanend.tom_perfeito_api.factory.UserDTOFactory.*;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oryanend.tom_perfeito_api.dto.UserDTO;
import java.util.UUID;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String userUrl, authRegisterUrl, authLoginUrl;

  private UserDTO validUserDTO;

  private String validUsername, validEmail, validPassword;
  private String invalidUsername, invalidEmail, invalidPassword;
  private String invalidToken;

  @Value("${security.client-id}")
  private String clientId;

  @Value("${security.client-secret}")
  private String clientSecret;

  @BeforeEach
  void setUp() {
    userUrl = "/users";
    authRegisterUrl = "/auth/register";
    authLoginUrl = "/auth/login";

    invalidUsername = "iu";
    invalidPassword = "123";
    invalidEmail = "mail";

    validUsername = "testuser";
    validEmail = "email@test.com";
    validPassword = "testpassword";

    validUserDTO = createUserDTO(validUsername, validEmail, validPassword);

    invalidToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";
  }

  // GET Tests
  @Test
  @DisplayName("GET `/users/me` should return the authenticated user's data")
  void getMeWithValidToken() throws Exception {
    // Insert a valid user and get a token for it
    String validToken = registerUserAndObtainAcessToken(validUserDTO);

    // Now, authenticate as that user and call /users/me
    ResultActions getResult =
        mockMvc.perform(
            get(userUrl + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + validToken)
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
        mockMvc.perform(
            get(userUrl + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

    getResult.andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET `/users/{id}` with existing id should return user")
  void getUserWithExistingId() throws Exception {
    // First, insert a valid user
    UserDTO registeredUser = registerUser(validUserDTO);

    // Get the id of the registered user
    String id = String.valueOf(registeredUser.getId());

    // Now, try to get the user by id
    ResultActions getResult =
        mockMvc.perform(
            get(userUrl + "/" + id)
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
        mockMvc.perform(
            get(userUrl + "/" + nonExistingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

    getResult
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("User not found"))
        .andExpect(jsonPath("$.path").value(userUrl + "/" + nonExistingId))
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  // Methods to help tests

  // Used to receive a valid token for a user by his email and password, also checks if the token is
  // valid and has the correct claims
  private String obtainAcessToken(String email, String password) throws Exception {
    ResultActions tokenResult =
        mockMvc.perform(
            post(authLoginUrl)
                .with(httpBasic(clientId, clientSecret))
                .param("email", email)
                .param("password", password)
                .param("grant_type", "password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

    tokenResult.andExpect(status().isOk());

    return objectMapper
        .readTree(tokenResult.andReturn().getResponse().getContentAsString())
        .get("access_token")
        .asText();
  }

  // Create a valid user by `UserDTO` and return the created user as `UserDTO`
  private UserDTO registerUser(UserDTO dto) throws Exception {
    String jsonBody = objectMapper.writeValueAsString(dto);

    ResultActions createUserResult =
        mockMvc.perform(
            post(authRegisterUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    createUserResult.andExpect(status().isCreated());

    String response = createUserResult.andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(response, UserDTO.class);
  }

  // Use the `registerUser` and `obtainAcessToken` methods to create a user and obtain a valid token
  // for that user
  private String registerUserAndObtainAcessToken(UserDTO dto) throws Exception {
    UserDTO registeredUser = registerUser(dto);
    return obtainAcessToken(registeredUser.getEmail(), dto.getPassword());
  }
}
