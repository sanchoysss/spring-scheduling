package com.almi.springscheduling.user.impl;

import com.almi.springscheduling.user.User;
import com.almi.springscheduling.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class UserDaoImpl implements UserDao {

    private DataSource postgresDataSource;

    private static final String SQL_QUERY_CREATE_USER = "insert into users(firstName, lastName, age, last_modified_ts) values (?, ?, ?, ?)";
    private static final String SQL_QUERY_GET_USERS = "select id, firstName, lastName, age from users";
    private static final String SQL_QUERY_DELETE_OLD_USERS = "delete from users where last_modified_ts < ?";

    public UserDaoImpl(JdbcTemplate jdbcTemplate, DataSource postgresDataSource) {
        this.postgresDataSource = postgresDataSource;
    }

    @Override
    public long createUser(User user) {
        try (Connection connection = postgresDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_QUERY_CREATE_USER, Statement.RETURN_GENERATED_KEYS)
        ) {

            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setByte(3, user.getAge());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            long key = -1;
            if (rs.next()) {
                key = rs.getLong(1);
                user.setId(key);
                log.info("Created user {}", user);
            }
            rs.close();
            return key;
        } catch (SQLException e) {
            log.error("createUser() error: {}", e.getMessage());
            return -1;
        }
    }

    @Override
    public List<User> getUsers() {
        try (Connection connection = postgresDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_QUERY_GET_USERS);
             ResultSet rs = statement.executeQuery()
        ) {
            List<User> users = new ArrayList<>(rs.getFetchSize());
            while (rs.next()) {
                User user = User.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("firstName"))
                        .lastName(rs.getString("lastName"))
                        .age(rs.getByte("long"))
                        .build();
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            log.error("getUsers() error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteOldUsers(LocalDateTime lastModifiedDateTime) {
        try (Connection connection = postgresDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_QUERY_DELETE_OLD_USERS)
        ) {
            statement.setTimestamp(1, Timestamp.valueOf(lastModifiedDateTime));
            int number = statement.executeUpdate();
            log.info("Users deleted {}", number);
        } catch (SQLException e) {
            log.error("deleteOldUsers() error: {}", e.getMessage());
        }
    }
}
