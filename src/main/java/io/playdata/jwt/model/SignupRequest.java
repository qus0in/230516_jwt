package io.playdata.jwt.model;

import lombok.Data;

// Entity?
// https://techblog.woowahan.com/2647/
@Data // DTO - 가입 요청 시에 요청을 대응하기 위한 Form으로 존재하는 클래스(객체)
public class SignupRequest { // 테이블을 만들 일이 없다!
    private String username;
    private String password;
    private String role;
}
