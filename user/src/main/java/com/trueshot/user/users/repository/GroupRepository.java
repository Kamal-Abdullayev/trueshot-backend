package com.trueshot.user.users.repository;

import com.trueshot.user.users.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupRepository extends JpaRepository<Group, String> {
}
