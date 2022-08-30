package com.almi.springscheduling.user;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDao {

    long createUser(User user);

    List<User> getUsers();

    void deleteOldUsers(LocalDateTime lastModifiedDateTime);
}
