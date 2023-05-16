package io.playdata.jwt.service;

import io.playdata.jwt.model.Account;
import io.playdata.jwt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService -> 인터페이스를 구현하기 위한 구현체
// Spring / Spring Boot
// Service => Interface / ServiceImpl => Class
// Service를 Interface로 구현해서 이미 필요한 기능들은 다 정의해놓고,
// 실제 구현은 Impl이 붙은 클래스로 나눠서 진행함을 통해서 상호 의존성이나 복잡성을 줄이는 방향.
// https://wildeveloperetrain.tistory.com/49
// -> 상사, 팀장님 -> 서비스 인터페이스를 만들어서 여러분한테 내려보냅니다
// -> 실제 그 명세에 맞춰서 Impl.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // UserDetailsService <- Spring Security가 권장하는 User 정보 서비스의 구조

    @Autowired
    private AccountRepository accountRepository;

    // UserDetails <- 유저의 필수 정보를 갖고 있는 클래스(타입)
    // throws UsernameNotFoundException : 이 메소드는 이러한 예외가 날 수 있습니다
    // (호출해서 쓰는 상위 메소드, 클래스에서 이걸 처리하는 것을 결정)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username); // JPA 메소드 생성규칙
        if (account == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        // User = Spring Security에서 쓰는 User.
        return User // (!= user(Account))
                .withUsername(username) // Username을 지정 (account)
                .password(account.getPassword()) // 암호도 지정
//                .roles("USER") // 역할
                .roles(account.getRole()) // 역할 <- Custom 역할
                .build(); // 클래스 기반한 객체를 생성
    }
}

