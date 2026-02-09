package com.oryanend.tom_perfeito_api.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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
import static com.oryanend.tom_perfeito_api.factory.UserDTOFactory.createUserDTOWithEmail;
import static com.oryanend.tom_perfeito_api.factory.UserDTOFactory.createUserDTOWithPassword;
import static com.oryanend.tom_perfeito_api.factory.UserDTOFactory.createUserDTOWithUsername;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordConfig passwordConfig;

    private String authUrl, authRegisterUrl, authLoginUrl;
    private String validUsername, validEmail, validPassword;
    private String invalidUsername, invalidEmail, invalidPassword;
    private String invalidToken;
    private UserDTO validUserDTO, secondValidUserWithSameUsernameDTO, secondValidUserWithSameEmailDTO;
    private UserDTO nullPasswordUserDTO, nullEmailUserDTO, nullUsernameUserDTO;
    private UserDTO invalidUsernameUserDTO, invalidEmailUserDTO, invalidPasswordUserDTO;


    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @Value("${security.jwt.duration}")
    private Integer tokenExpiresIn;

    @BeforeEach
    void setUp() {
        authUrl = "/auth/login";
        authRegisterUrl = "/auth/register";
        authLoginUrl = "/auth/login";

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

    // Post `/auth/login` tests

    @Test
    @DisplayName("POST `/auth/login` with valid email and password should return token")
    void validTokenTest() throws Exception {
        // First, register the user
        registerUser(validUserDTO);

        // Now, try to obtain a token
        ResultActions validTokenResult = obtainAcessToken(validUserDTO.getEmail(), validUserDTO.getPassword());

        validTokenResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").isNumber())
                .andExpect(jsonPath("$.access_token").isString())
        ;

        // Token validation
        String responseString = validTokenResult.andReturn().getResponse().getContentAsString();
        String accessToken = JsonPath.read(responseString, "$.access_token");

        DecodedJWT decodedJWT = JWT.decode(accessToken);

        assertEquals(clientId, decodedJWT.getSubject());
        assertEquals(decodedJWT.getExpiresAt().getTime() - decodedJWT.getIssuedAt().getTime(), tokenExpiresIn * 1000);
        assertEquals(validUserDTO.getEmail(), decodedJWT.getClaim("username").asString());
    }

    @Test
    @DisplayName("POST `/auth/login` with invalid password should return 400")
    void invalidPasswordTest() throws Exception {
        ResultActions tokenResult = obtainAcessToken(validUserDTO.getEmail(), "wrongpassword");

        tokenResult
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Invalid Credentials"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Email or Password invalid"))
                .andExpect(jsonPath("$.path").value(authUrl))
        ;
    }

    @Test
    @DisplayName("POST `/auth/login` with invalid username should return 400")
    void invalidUsernameTest() throws Exception {
        ResultActions tokenResult = obtainAcessToken("invalidEmailUsername@test.com", validUserDTO.getPassword());

        tokenResult
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Invalid Credentials"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Email or Password invalid"))
                .andExpect(jsonPath("$.path").value(authUrl))
        ;
    }

    // POST `/auth/register` tests

    @Test
    @DisplayName("POST `/auth/register` with valid data should return success")
    void insertUser() throws Exception {
        // First, register the user
        UserDTO userDTO = registerUser(validUserDTO);

        // Check if the returned id is a valid UUID
        String id = userDTO.getId().toString();
        assertDoesNotThrow(() -> UUID.fromString(id));

        // Check if the password is encoded correctly
        assertTrue(passwordConfig.passwordEncoder().matches(validPassword, userDTO.getPassword()));

        // Check if createdAt and updatedAt are valid Instants
        String createdAtStr = userDTO.getCreatedAt().toString();
        String updatedAtStr = userDTO.getUpdatedAt().toString();
        assertDoesNotThrow(() -> Instant.parse(createdAtStr));
        assertDoesNotThrow(() -> Instant.parse(updatedAtStr));
    }

    @Test
    @DisplayName("POST `/auth/register` without password should return unprocessable entity")
    void insertWithoutPassword() throws Exception {
        ResultActions result = insertUser(nullPasswordUserDTO);

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("Password cannot be null"));
    }

    @Test
    @DisplayName("POST `/auth/register` without email should return unprocessable entity")
    void insertWithoutEmail() throws Exception {
        ResultActions result = insertUser(nullEmailUserDTO);

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("Email cannot be null"));
    }

    @Test
    @DisplayName("POST `/auth/register` without username should return unprocessable entity")
    void insertWithoutUsername() throws Exception {
        ResultActions result = insertUser(nullUsernameUserDTO);

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("username"))
                .andExpect(jsonPath("$.errors[0].message").value("Username cannot be null"));
    }

    @Test
    @DisplayName("POST `/auth/register` with a invalid password should return unprocessable entity")
    void insertWithInvalidPassword() throws Exception {
        ResultActions result = insertUser(invalidPasswordUserDTO);

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("Password must have 5 characters at least"));
    }

    @Test
    @DisplayName("POST `/auth/register` with a invalid password should return unprocessable entity")
    void insertWithInvalidEmail() throws Exception {
        ResultActions result = insertUser(invalidEmailUserDTO);

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.errors[*].fieldName").value(hasItem("email")))
                .andExpect(jsonPath("$.errors[*].message").value(hasItem("Email must be between 5 and 254 characters")))
                .andExpect(jsonPath("$.errors[*].message").value(hasItem("Email should be valid")));
    }

    @Test
    @DisplayName("POST `/auth/register` with a invalid username should return unprocessable entity")
    void insertWithInvalidUsername() throws Exception {
        ResultActions result = insertUser(invalidUsernameUserDTO);

        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Exception"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.errors[0].fieldName").value("username"))
                .andExpect(jsonPath("$.errors[0].message").value("Username must be between 3 and 40 characters"));
    }

    @Test
    @DisplayName("POST `/auth/register` with an already taken email should return bad request")
    void insertWithAlreadyTakenEmail() throws Exception {
        ResultActions result = insertUser(validUserDTO);

        result.andExpect(status().isCreated());

        // Try to insert the same user again
        ResultActions secondResult = insertUser(secondValidUserWithSameEmailDTO);

        secondResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Resource already exists"))
                .andExpect(jsonPath("$.message").value("Email already in use, try another one."))
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("POST `/auth/register` with an already taken username should return bad request")
    void insertWithAlreadyTakenUsername() throws Exception {
        ResultActions result = insertUser(validUserDTO);

        result.andExpect(status().isCreated());

        // Try to insert the same user again
        ResultActions secondResult = insertUser(secondValidUserWithSameUsernameDTO);

        secondResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Resource already exists"))
                .andExpect(jsonPath("$.message").value("Username already in use, try another one."))
                .andExpect(jsonPath("$.path").value(authRegisterUrl))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // Methods to help tests

    // Used to receive a valid token for a user by his email and password
    private ResultActions obtainAcessToken(String email, String password) throws Exception {
        return mockMvc
                .perform(post(authLoginUrl).with(httpBasic(clientId, clientSecret))
                        .param("email", email)
                        .param("password", password)
                        .param("grant_type", "password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
    }

    // Insert a user by `UserDTO` and return the ResultActions
    private ResultActions insertUser(UserDTO dto) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(dto);

        return mockMvc
                .perform(post(authRegisterUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON));
    }

    // Create a valid user by `UserDTO` and return the created user as `UserDTO`
    private UserDTO registerUser(UserDTO dto) throws Exception {
        ResultActions createUserResult = insertUser(dto);

        createUserResult
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(validUsername))
                .andExpect(jsonPath("$.email").value(validEmail))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[*].authority", hasItem("ROLE_CLIENT")))
        ;

        String response = createUserResult.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, UserDTO.class);
    }
}