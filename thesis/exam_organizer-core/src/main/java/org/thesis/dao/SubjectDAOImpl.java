package org.thesis.dao;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.model.Subject;
import org.thesis.model.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class that implements CRUD operations. After the database connection is established, it handles individual subjects for that user.
 */
public class SubjectDAOImpl implements SubjectDAO<Subject> {

    private static final Logger logger = LogManager.getLogger(SubjectDAOImpl.class);

    private Connection connection;
    private Properties properties;
    private PreparedStatement findAllSubjects;
    private PreparedStatement updateSubject;
    private PreparedStatement insertSubject;
    private PreparedStatement deleteSubject;
    private PreparedStatement deleteAllSubjects;

    public SubjectDAOImpl() throws SQLException {

        this.properties = new Properties();

        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
        }

        this.connection = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("user"), properties.getProperty("password"));

        this.findAllSubjects = connection.prepareStatement("SELECT * FROM SUBJECTS WHERE USERNAME = ?");
        this.updateSubject = connection.prepareStatement("UPDATE SUBJECTS SET CREDIT = ?, EXAMS_LEFT = ?, FURTHER_REG = ?, HOURS_NEEDED_FOR_LEARNING = ?, PREREQUIREMENT = ?, SUCCESS = ?, SUPP_EXAM_ALLOWED = ? WHERE USERNAME = ? AND SUBJECT = ?");
        this.insertSubject = connection.prepareStatement("INSERT INTO SUBJECTS (SUBJECT, USERNAME, CREDIT, EXAMS_LEFT, FURTHER_REG, HOURS_NEEDED_FOR_LEARNING, PREREQUIREMENT, SUCCESS, SUPP_EXAM_ALLOWED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        this.deleteSubject = connection.prepareStatement("DELETE FROM SUBJECTS WHERE SUBJECT = ? AND USERNAME = ?");
        this.deleteAllSubjects = connection.prepareStatement("DELETE FROM SUBJECTS WHERE USERNAME = ?");
    }


    @Override
    public List<Subject> findAllSubjects(String user) throws SQLException {
        this.findAllSubjects.setString(1, user);
        ResultSet all = this.findAllSubjects.executeQuery();
        List<Subject> ret = makeSubjectsList(all);
        all.close();
        return ret;
    }

    private List<Subject> makeSubjectsList(ResultSet rs) throws SQLException {
        List<Subject> ret = new ArrayList<>();
        while (rs.next()) {
            ret.add(makeNewUser(rs));
        }
        return ret;
    }

    private Subject makeNewUser(ResultSet rs) throws SQLException {

        Subject subject = new Subject();

        subject.setId(rs.getInt("id"));
        subject.setSubject(rs.getString("subject"));
        subject.setUser(rs.getString("username"));
        subject.setCredit(rs.getInt("credit"));
        subject.setExamsLeft(rs.getInt("exams_left"));
        subject.setFurtherRegistrationPossible(rs.getBoolean("further_reg"));
        subject.setTimeNeededForLearning(rs.getInt("hours_needed_for_learning"));
        subject.setPreRequirement(rs.getBoolean("prerequirement"));
        subject.setSuccess(rs.getBoolean("success"));
        subject.setSupplementaryExamAllowed(rs.getBoolean("supp_exam_allowed"));

        return subject;
    }


    @Override
    public void insertSubject(Subject subject) throws SQLException {
        this.insertSubject.setString(1, subject.getSubject());
        this.insertSubject.setString(2, subject.getUser());
        this.insertSubject.setInt(3, subject.getCredit());
        this.insertSubject.setInt(4, subject.getExamsLeft());
        this.insertSubject.setBoolean(5, subject.isFurtherRegistrationPossible());
        this.insertSubject.setInt(6, subject.getTimeNeededForLearning());
        this.insertSubject.setBoolean(7, subject.isPreRequirement());
        this.insertSubject.setBoolean(8, subject.isSuccess());
        this.insertSubject.setBoolean(9, subject.getSupplementaryExamAllowed());
        this.insertSubject.execute();
    }


    @Override
    public void updateSubject(Subject subject) throws SQLException {
        this.updateSubject.setInt(1, subject.getCredit());
        this.updateSubject.setInt(2, subject.getExamsLeft());
        this.updateSubject.setBoolean(3, subject.isFurtherRegistrationPossible());
        this.updateSubject.setInt(4, subject.getTimeNeededForLearning());
        this.updateSubject.setBoolean(5, subject.isPreRequirement());
        this.updateSubject.setBoolean(6, subject.isSuccess());
        this.updateSubject.setBoolean(7, subject.getSupplementaryExamAllowed());
        this.updateSubject.setString(8, subject.getUser());
        this.updateSubject.setString(9, subject.getSubject());
        this.updateSubject.execute();
    }

    @Override
    public void deleteSubject(Subject subject) throws SQLException {
        this.deleteSubject.setString(1, subject.getSubject());
        this.deleteSubject.setString(2, subject.getUser());
        this.deleteSubject.execute();
    }

    @Override
    public void deleteAllSubjects(User user) throws SQLException {
        this.deleteAllSubjects.setString(1, user.getUsername());
        this.deleteAllSubjects.execute();
    }


    @Override
    public void close() throws SQLException {
        this.findAllSubjects.close();
        this.insertSubject.close();
        this.updateSubject.close();
        this.deleteSubject.close();
        this.connection.close();
    }

}
