package com.oryanend.tom_perfeito_api.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.oryanend.tom_perfeito_api.dto.UserDTO;
import com.oryanend.tom_perfeito_api.factory.UserDTOFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OAuth2Test {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private UserDTO validUserDTO;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @Value("${security.jwt.duration}")
    private Integer tokenExpiresIn;

    @BeforeEach
    void setUp() {
        baseUrl = "/oauth2/token";

        validUserDTO = UserDTOFactory.createUserDTOTemplate();
    }

    @Test
    @DisplayName("POST `/oauth2/token` with valid email and password should return token")
    void validTokenTest() throws Exception {
        // Create new user first
        String jsonBody = objectMapper.writeValueAsString(validUserDTO);

        ResultActions result =
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());

        // Try to get token with the same user
        ResultActions tokenResult =
                mockMvc
                        .perform(post(baseUrl).with(httpBasic(clientId, clientSecret))
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

        // Token validation
        String responseString = tokenResult.andReturn().getResponse().getContentAsString();
        String accessToken = JsonPath.read(responseString, "$.access_token");

        DecodedJWT decodedJWT = JWT.decode(accessToken);

        assertEquals(clientId, decodedJWT.getSubject());
        assertEquals(decodedJWT.getExpiresAt().getTime() - decodedJWT.getIssuedAt().getTime(), tokenExpiresIn * 1000);
        assertEquals(validUserDTO.getEmail(), decodedJWT.getClaim("username").asString());
    }

    @Test
    @DisplayName("POST `/oauth2/token` with invalid password should return 400")
    void invalidPasswordTest() throws Exception {
        ResultActions tokenResult =
                mockMvc
                        .perform(post(baseUrl)
                                .with(httpBasic(clientId, clientSecret))
                                .param("username", validUserDTO.getEmail())
                                .param("password", "wrongpassword")
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid credentials"))
        ;
    }

    @Test
    @DisplayName("POST `/oauth2/token` with invalid username should return 400")
    void invalidUsernameTest() throws Exception {
        ResultActions tokenResult =
                mockMvc
                        .perform(post(baseUrl)
                                .with(httpBasic(clientId, clientSecret))
                                .param("username", "invalidEmailUsername@test.com")
                                .param("password", validUserDTO.getPassword())
                                .param("grant_type", "password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON));

        tokenResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid credentials"))
        ;
    }
}
