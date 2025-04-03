package org.vinio.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.vinio.dto.UserDTO;

public class UserInfoResponse {
    JwtResponse jwtResponse;
    UserDTO userDTO;
}
