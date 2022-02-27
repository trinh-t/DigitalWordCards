package com.digitalwordcards.data.repositories;

import com.digitalwordcards.data.Card;
import com.digitalwordcards.data.Role;
import com.digitalwordcards.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByClazzAndAndRole(String clazz, Role role);
    Optional<User> findUserByEmail(String email);

    List<User> findAllByViewedCardsIsContaining(Card card);

}
