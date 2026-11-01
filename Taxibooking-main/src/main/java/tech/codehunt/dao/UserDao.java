package tech.codehunt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.codehunt.model.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    // Naya method login ke liye add kiya gaya hai
    User findByEmail(String email);
}