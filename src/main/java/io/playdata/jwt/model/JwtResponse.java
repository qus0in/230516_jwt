package io.playdata.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // AllArgsConstructor : 모든 가지고 있는 속성(멤버변수)을 포함한 생성자
public class JwtResponse {
    private String jwtToken; // jwtToken <- 응답
}
