package io.playdata.jwt.repository;

import io.playdata.jwt.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // findByUsername <- Spring Security = Account / User 연결해줄 때
    Account findByUsername(String username);
}

