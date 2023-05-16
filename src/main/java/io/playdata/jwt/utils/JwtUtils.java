package io.playdata.jwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component // Spring Container 등록
public class JwtUtils { // Jwt 생성과 검증에 쓰이는 기능들을 묶어둔 클래스

    @Value("${jwt.secret}") // @Value -> application.properties
    private String secret; // secretKey -> Jwt를 생성할 때 기준으로 쓰일 비밀키를 지정

    @Value("${jwt.expiration}") // 만료 시간 (초)
    private int expiration;

    public String generateJwtToken(String username) { // 토큰을 생성
        Date now = new Date(); // 현재 시간
        Date expiryDate = new Date(now.getTime() + expiration * 1000); // 1일 후 만료
        // 밀리초 -> 천분의 1초 -> 86400 * 1000? 밀리초.

        return Jwts.builder()
                .setSubject(username) // 회원이름
                .setIssuedAt(now) // 발행시간
                .setExpiration(expiryDate) // 만료시간
                .signWith(SignatureAlgorithm.HS512, secret) // 서명 -> 위변조를 막기 위해 변조
                // HS512 -> 서명알고리즘. + secret 암호키
                .compact(); // 정리.
    }

    // 토큰 -> username (토큰에서 정보 뽑아내기)
    public String getUsernameFromJwtToken(String token) {
        Claims claims = Jwts.parser() // JWT를 분석할 수 있게 처리
                .setSigningKey(secret) // 서명 - 암호키
                .parseClaimsJws(token) // 서명 전의 상태로 다시 변경
                .getBody();

        return claims.getSubject(); // setSubject - username을 getSubject를 통해서 받아옴
    }

    // 토큰을 검증. 비밀키를 통해서 이 Jwt 토큰이 정상적인가? 형식을 맞췄는가?
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true; // 우리가 발행한 토큰
        } catch (Exception e) {
            return false; // 위변조된 토큰 (서명이 일치하지 않는)
        }
    }
}