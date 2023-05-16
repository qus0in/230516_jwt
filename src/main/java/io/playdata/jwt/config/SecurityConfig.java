package io.playdata.jwt.config;

//import io.playdata.jwt.filter.JwtAuthenticationFilter;
import io.playdata.jwt.filter.JwtAuthenticationFilter;
import io.playdata.jwt.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth.userDetailsService(userDetailsService) -> 간단해짐?
        // 별도로 AccountService를 구현한게 아니라, Spring Security의 userDetailsService를
        // 인터페이스 삼아서 구현했기 때문에 그냥 넣어주면 됨.
        auth.userDetailsService(userDetailsService
        ).passwordEncoder(passwordEncoder()); // Bcrypt -> DB 쪽에 털려도 원래 비밀번호를 알 수 없는 설정.
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // Spring REST API를 쓸 때 외부에서 접속했을 때 차단하는 옵션을 꺼줌
                .authorizeRequests() // 어떠한 메소드들(경로)들을 권한을 지정해줄지
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                // HttpMethod.POST 외의 나머지 메소드들은 허용하지 않음 (인증이 필요한 상태)
                .anyRequest().authenticated()
                // sessionManagement - SessionCreationPolicy.STATELESS (토큰을 써주겠다는 의미)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // http 요청을 받았을 때 인증 header 담긴 jwt를 filter를 통해서 검증 혹은 사용.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
//    @Autowired
//    private AuthenticationManager authenticationManager;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}