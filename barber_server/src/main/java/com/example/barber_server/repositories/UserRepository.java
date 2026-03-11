package com.example.barber_server.repositories;
import com.example.barber_server.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findByUsername(String userName);
    Page<User> findAllByUserType(String role, Pageable pageable);
    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);
}
