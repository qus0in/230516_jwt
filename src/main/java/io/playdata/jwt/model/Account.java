package io.playdata.jwt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // JPA - Entity (Table 만들겠다)
@Data // Lombok
public class Account { // 계정 (User?) 1. MySQL User 예약어 에러 2. Spring Security User.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    private String role;
}
