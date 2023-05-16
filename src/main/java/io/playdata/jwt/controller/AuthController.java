package io.playdata.jwt.controller;

import io.playdata.jwt.model.Account;
import io.playdata.jwt.model.JwtResponse;
import io.playdata.jwt.model.LoginRequest;
import io.playdata.jwt.model.SignupRequest;
import io.playdata.jwt.repository.AccountRepository;
import io.playdata.jwt.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController // RestAPI (웹페이지 대신에 json)
@RequestMapping("/api") // host:port/path => api
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    // AuthenticationManager : Spring Security에서 쓰는 인증 관련 클래스
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt를 사용하기 위해서 지정
    @Autowired
    private JwtUtils jwtUtils; // JWT의 파싱이나 서명을 위한 유틸
    // jjwt를 사용해서 JwtUtils를 구현해줘야함

    @PostMapping("/authenticate") // POST /authenticate (인증)
    // User 인증 메소드 - LoginRequest : DTO / JSON -> 어떠한 Key(속성)들의 묶음
    // LoginRequest -> 로그인 요청 시 그에 맞는 요청 Form의 데이터를 받을 수 있는 클래스
    // loginID (Username), password
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // loginRequest - username, password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( // Token => JwtUtils => JWT
                        loginRequest.getUsername(), loginRequest.getPassword()));
        String jwtToken = jwtUtils.generateJwtToken(loginRequest.getUsername());
        // generate : 생성
        return ResponseEntity.ok(new JwtResponse(jwtToken)); // JwtResponse - DTO
        // ResponseEntity -> Response + Entity(값-DTO, Entity)
        // .ok -> 어떠한 메시지? -> ok 정상적으로 처리되었다 -> 상태코드 200
    }

    @PostMapping("/users") // User를 생성하기 위한 기능
    // POST /users -> User를 의미하는 Body -> Account를 생성할 수 있는 메소드
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        // SignupRequest -> Signup / register / join / create... -> DTO
        if (accountRepository.findByUsername(signupRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        Account account = new Account();
        account.setUsername(signupRequest.getUsername());
        account.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        // Role 추가
        account.setRole(signupRequest.getRole());

        accountRepository.save(account);

        // ok(...) 문자열 => 메시지를 전달
        return ResponseEntity.ok("User registered successfully!");
    }

    // 권한 테스트용 메소드
    @GetMapping("/hello") // authenticate, users는 모든 계정에서 접근 가능
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello, World!");
    }
}
