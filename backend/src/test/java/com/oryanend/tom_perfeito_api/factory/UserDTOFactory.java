package com.oryanend.tom_perfeito_api.factory;

import com.oryanend.tom_perfeito_api.dto.UserDTO;
import net.datafaker.Faker;

@SuppressWarnings("removal")
public class UserDTOFactory {
    private static final Faker faker = new Faker();

    // With all fields
    public static UserDTO createUserDTO(String username, String email, String password){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        return userDTO;
    }

    // With password setup
    public static UserDTO createUserDTOWithPassword(String password){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(faker.internet().username());
        userDTO.setEmail(faker.internet().emailAddress());
        userDTO.setPassword(password);
        return userDTO;
    }

    // With email setup
    public static UserDTO createUserDTOWithEmail(String email){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(faker.internet().username());
        userDTO.setEmail(email);
        userDTO.setPassword(faker.internet().password(7,12));
        return userDTO;
    }

    // With username setup
    public static UserDTO createUserDTOWithUsername(String username){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setEmail(faker.internet().emailAddress());
        userDTO.setPassword(faker.internet().password(7,12));
        return userDTO;
    }

    // Without any field
    public static UserDTO createUserDTOTemplate(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(faker.internet().username());
        userDTO.setEmail(faker.internet().emailAddress());
        userDTO.setPassword(faker.internet().password(7,12));
        return userDTO;
    }
}
