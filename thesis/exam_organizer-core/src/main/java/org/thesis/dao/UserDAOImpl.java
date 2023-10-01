package org.thesis.dao;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.model.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class that implements CRUD operations. After the database connection is established, it manages the users.
 */
public class UserDAOImpl implements UserDAO<User> {

    private static final Logger logger = LogManager.getLogger(UserDAOImpl.class);

    private Connection connection;
    private Properties properties;
    private PreparedStatement findAllUsers;
    private PreparedStatement findUser;
    private PreparedStatement updateUserLearningHabits;
    private PreparedStatement updateUser;
    private PreparedStatement insertUser;
    private PreparedStatement deleteUser;

    public UserDAOImpl() throws SQLException {

        this.properties = new Properties();

        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
        }

        this.connection = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("user"), properties.getProperty("password"));

        this.findAllUsers = connection.prepareStatement("SELECT * FROM USERS");
        this.findUser = connection.prepareStatement("SELECT * FROM USERS WHERE USERNAME = ?");
        this.updateUser = connection.prepareStatement("UPDATE USERS SET PASSWORD = ?, MAX_EXAM = ?, MAX_EXAM_PER_SEMESTER = ?, BEGIN_OF_EXAM_PERIOD = ?, END_OF_EXAM_PERIOD = ?, BEGIN_OF_SUPP_EXAM_PERIOD = ?, END_OF_SUPP_EXAM_PERIOD = ?, DURATION_OF_DAILY_ACTIVITIES = ? WHERE USERNAME = ?");
        this.updateUserLearningHabits = connection.prepareStatement("UPDATE USERS SET LEARNING_HOURS_PER_DAY = ?, SATURDAY = ?, SUNDAY = ? WHERE USERNAME = ?");
        this.insertUser = connection.prepareStatement("INSERT INTO USERS (USERNAME, PASSWORD, MAX_EXAM, MAX_EXAM_PER_SEMESTER, BEGIN_OF_EXAM_PERIOD, END_OF_EXAM_PERIOD, BEGIN_OF_SUPP_EXAM_PERIOD, END_OF_SUPP_EXAM_PERIOD, DURATION_OF_DAILY_ACTIVITIES, LEARNING_HOURS_PER_DAY, SATURDAY, SUNDAY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        this.deleteUser = connection.prepareStatement("DELETE FROM USERS WHERE USERNAME = ?");

    }


    @Override
    public List<User> findAllUsers() throws SQLException {
        ResultSet all = this.findAllUsers.executeQuery();
        List<User> ret = makeUsersList(all);
        all.close();
        return ret;
    }


    @Override
    public User findUser(User user) throws SQLException {


        this.findUser.setString(1, user.getUsername());


        ResultSet all = this.findUser.executeQuery();
        List<User> ret = makeUsersList(all);
        all.close();
        return ret.get(0);
    }


    private List<User> makeUsersList(ResultSet rs) throws SQLException {
        List<User> ret = new ArrayList<>();
        while (rs.next()) {
            ret.add(makeNewUser(rs));
        }
        return ret;
    }

    private User makeNewUser(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setMaxPossibleExam(rs.getInt("max_exam"));
        user.setMaxPossibleExamPerSemester(rs.getInt("max_exam_per_semester"));
        user.setBeginOfExamPeriod(rs.getDate("begin_of_exam_period").toLocalDate());
        user.setEndOfExamPeriod(rs.getDate("end_of_exam_period").toLocalDate());
        user.setBeginOfSupplementaryExamPeriod(rs.getDate("begin_of_supp_exam_period").toLocalDate());
        user.setEndOfSupplementaryExamPeriod(rs.getDate("end_of_supp_exam_period").toLocalDate());
        user.setDurationOfDailyActivities(rs.getInt("duration_of_daily_activities"));
        user.setLearningTimePerDay(rs.getInt("learning_hours_per_day"));
        user.setRestDayOnSaturday(rs.getBoolean("saturday"));
        user.setRestDayOnSunday(rs.getBoolean("sunday"));

        return user;
    }

    @Override
    public void insertUser(User user) throws SQLException {
        this.insertUser.setString(1, user.getUsername());
        this.insertUser.setString(2, user.getPassword());
        this.insertUser.setInt(3, user.getMaxPossibleExam());
        this.insertUser.setInt(4, user.getMaxPossibleExamPerSemester());
        this.insertUser.setString(5, user.getBeginOfExamPeriod().toString());
        this.insertUser.setString(6, user.getEndOfExamPeriod().toString());
        this.insertUser.setString(7, user.getBeginOfSupplementaryExamPeriod().toString());
        this.insertUser.setString(8, user.getEndOfSupplementaryExamPeriod().toString());
        this.insertUser.setInt(9, user.getDurationOfDailyActivities());
        this.insertUser.setInt(10, user.getLearningTimePerDay());
        this.insertUser.setBoolean(11, user.isRestDayOnSaturday());
        this.insertUser.setBoolean(12, user.isRestDayOnSunday());
        this.insertUser.execute();
    }

    @Override
    public void updateUser(User user) throws SQLException {
        this.updateUser.setString(1, user.getPassword());
        this.updateUser.setInt(2, user.getMaxPossibleExam());
        this.updateUser.setInt(3, user.getMaxPossibleExamPerSemester());
        this.updateUser.setString(4, user.getBeginOfExamPeriod().toString());
        this.updateUser.setString(5, user.getEndOfExamPeriod().toString());
        this.updateUser.setString(6, user.getBeginOfSupplementaryExamPeriod().toString());
        this.updateUser.setString(7, user.getEndOfSupplementaryExamPeriod().toString());
        this.updateUser.setInt(8, user.getDurationOfDailyActivities());
        this.updateUser.setString(9, user.getUsername());
        this.updateUser.execute();
    }


    @Override
    public void updateUserLearningHabits(User user) throws SQLException {
        this.updateUserLearningHabits.setInt(1, user.getLearningTimePerDay());
        this.updateUserLearningHabits.setBoolean(2, user.isRestDayOnSaturday());
        this.updateUserLearningHabits.setBoolean(3, user.isRestDayOnSunday());
        this.updateUserLearningHabits.setString(4, user.getUsername());
        this.updateUserLearningHabits.execute();
    }


    @Override
    public void deleteUser(User user) throws SQLException {
        this.deleteUser.setString(1, user.getUsername());
        this.deleteUser.execute();
    }

    @Override
    public void close() throws SQLException {
        this.findUser.close();
        this.findAllUsers.close();
        this.insertUser.close();
        this.updateUser.close();
        this.updateUserLearningHabits.close();
        this.deleteUser.close();
        this.connection.close();
    }

}
