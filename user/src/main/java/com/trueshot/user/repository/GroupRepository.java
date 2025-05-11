package com.trueshot.user.repository;

import com.trueshot.user.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupRepository extends JpaRepository<Group, String> {

}
