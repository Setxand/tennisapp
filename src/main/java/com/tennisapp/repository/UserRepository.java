package com.tennisapp.repository;


import com.tennisapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByTennisId(String tennisId);

	List<User> findUsersByTennisIdIn(Set<String> keySet);

}
