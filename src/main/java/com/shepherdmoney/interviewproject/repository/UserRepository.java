package com.shepherdmoney.interviewproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shepherdmoney.interviewproject.model.User;

/**
 * Crud Repository to store User classes
 */
@Repository("UserRepo")
public interface UserRepository extends JpaRepository<User, Integer> {

    public List<User> findByName(String name);
    public User findById(int userId);
    public void deleteById(int userId);

}
