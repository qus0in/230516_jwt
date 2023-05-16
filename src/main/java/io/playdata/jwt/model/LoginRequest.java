package io.playdata.jwt.model;

import lombok.Data;

// Entity?
// https://techblog.woowahan.com/2647/
@Data // DTO - 로그인 요청 시에 단순히 요청을 대응하기 위한 Form으로 존재하는 클래스(객체)
public class LoginRequest {
    private String username;
    private String password;
}
