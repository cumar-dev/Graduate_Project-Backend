package com.graduate.repository;

import com.graduate.model.Role;
import com.graduate.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByStudentId(String studentId);

    List<User> findByRole(Role role);

    long countByRole(Role role);
}
