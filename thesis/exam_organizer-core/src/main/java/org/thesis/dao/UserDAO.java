package org.thesis.dao;

import java.sql.SQLException;
import java.util.List;


public interface UserDAO<User> {

    User findUser(User user) throws SQLException;

    List<User> findAllUsers() throws SQLException;

    void insertUser(User user) throws SQLException;

    void updateUserLearningHabits(User user) throws SQLException;

    void updateUser(User user) throws SQLException;

    void deleteUser(User user) throws SQLException;

    void close() throws SQLException;

}
