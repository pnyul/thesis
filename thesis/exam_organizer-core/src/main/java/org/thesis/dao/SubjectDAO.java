package org.thesis.dao;

import org.thesis.model.User;

import java.sql.SQLException;
import java.util.List;

public interface SubjectDAO<Subject> {

    List<Subject> findAllSubjects(String user) throws SQLException;

    void insertSubject(Subject subject) throws SQLException;

    void updateSubject(Subject subject) throws SQLException;

    void deleteSubject(Subject subject) throws SQLException;

    void deleteAllSubjects(User user) throws SQLException;

    void close() throws SQLException;


}
