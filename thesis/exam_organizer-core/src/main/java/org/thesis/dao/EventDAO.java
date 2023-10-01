package org.thesis.dao;

import org.thesis.model.User;

import java.sql.SQLException;
import java.util.List;

public interface EventDAO<Event> {

    List<Event> findAllEvents(String user, Integer... type) throws SQLException;

    void insertEvent(Event event) throws SQLException;

    void updateEvent(Event event) throws SQLException;

    void deleteEvent(Event event) throws SQLException;

    void deleteAllEvents(User user) throws SQLException;

    void close() throws SQLException;

}
