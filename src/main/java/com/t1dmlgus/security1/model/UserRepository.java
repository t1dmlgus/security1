package com.t1dmlgus.security1.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


    //findBy규칙 -> Username 문법
    // select * from user where username = ?
    public User findByUsername(String username);           // jpa Query 메소드


}
