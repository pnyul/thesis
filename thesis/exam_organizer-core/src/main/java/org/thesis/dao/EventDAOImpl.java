package org.thesis.dao;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.model.Event;
import org.thesis.model.User;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class that implements CRUD operations. After the database connection is established, it handles individual events for that user.
 */
public class EventDAOImpl implements EventDAO<Event> {

    private static final Logger logger = LogManager.getLogger(EventDAOImpl.class);

    private Connection connection;
    private Properties properties;
    private PreparedStatement findAllEvents;
    private PreparedStatement updateEvent;
    private PreparedStatement insertEvent;
    private PreparedStatement deleteEvent;
    private PreparedStatement deleteAllEvents;

    public EventDAOImpl() throws SQLException {

        this.properties = new Properties();

        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
        }

        this.connection = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("user"), properties.getProperty("password"));

        this.findAllEvents = connection.prepareStatement("SELECT * FROM EVENTS WHERE USERNAME = ?");
        this.updateEvent = connection.prepareStatement("UPDATE EVENTS SET ACTIVE = ? WHERE NAME_OF_EVENT = ? AND USERNAME = ? AND BEGIN_OF_EVENT = ? AND END_OF_EVENT = ?");
        this.insertEvent = connection.prepareStatement("INSERT INTO EVENTS (NAME_OF_EVENT, USERNAME, BEGIN_OF_EVENT, END_OF_EVENT, TYPE, ACTIVE) VALUES (?, ?, ?, ?, ?, ?)");
        this.deleteEvent = connection.prepareStatement("DELETE FROM EVENTS WHERE NAME_OF_EVENT = ? AND USERNAME = ? AND BEGIN_OF_EVENT = ? AND END_OF_EVENT = ?");
        this.deleteAllEvents = connection.prepareStatement(("DELETE FROM EVENTS WHERE USERNAME = ? "));
    }


    @Override
    public List<Event> findAllEvents(String user, Integer... type) throws SQLException {

        if (type.length != 0) {
            this.findAllEvents = connection.prepareStatement("SELECT * FROM EVENTS WHERE USERNAME = ? AND TYPE = ?");
            this.findAllEvents.setInt(2, type[0]);
        }

        this.findAllEvents.setString(1, user);
        ResultSet all = this.findAllEvents.executeQuery();
        List<Event> ret = makeEventsList(all);
        all.close();

        return ret;
    }


    private List<Event> makeEventsList(ResultSet rs) throws SQLException {

        List<Event> ret = new ArrayList<>();
        while (rs.next()) {
            ret.add(makeNewEvent(rs));
        }
        return ret;

    }

    private Event makeNewEvent(ResultSet rs) throws SQLException {

        Event event = new Event();

        event.setId(rs.getInt("id"));
        event.setNameOfEvent(rs.getString("name_of_event"));
        event.setUsername(rs.getString("username"));
        event.setBeginOfEvent(LocalDateTime.parse(rs.getString("begin_of_event"), DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm")));
        event.setEndOfEvent(LocalDateTime.parse(rs.getString("end_of_event"), DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm")));
        event.setType(rs.getInt("type"));
        event.setActive(rs.getBoolean("active"));

        return event;
    }

    @Override
    public void insertEvent(Event event) throws SQLException {

        String begin = event.getBeginOfEvent().toString().replace("-", ".").replace("T", ". ");
        String end = event.getEndOfEvent().toString().replace("-", ".").replace("T", ". ");

        this.insertEvent.setString(1, event.getNameOfEvent());
        this.insertEvent.setString(2, event.getUsername());
        this.insertEvent.setString(3, begin);
        this.insertEvent.setString(4, end);
        this.insertEvent.setInt(5, event.getType());
        this.insertEvent.setBoolean(6, event.isActive());

        this.insertEvent.execute();

    }


    @Override
    public void updateEvent(Event event) throws SQLException {

        String begin = event.getBeginOfEvent().toString().replace("-", ".").replace("T", ". ");
        String end = event.getEndOfEvent().toString().replace("-", ".").replace("T", ". ");

        this.updateEvent.setBoolean(1, event.isActive());
        this.updateEvent.setString(2, event.getNameOfEvent());
        this.updateEvent.setString(3, event.getUsername());
        this.updateEvent.setString(4, begin);
        this.updateEvent.setString(5, end);

        this.updateEvent.execute();

    }


    @Override
    public void deleteEvent(Event event) throws SQLException {

        String begin = event.getBeginOfEvent().toString().replace("-", ".").replace("T", ". ");
        String end = event.getEndOfEvent().toString().replace("-", ".").replace("T", ". ");

        this.deleteEvent.setString(1, event.getNameOfEvent());
        this.deleteEvent.setString(2, event.getUsername());
        this.deleteEvent.setString(3, begin);
        this.deleteEvent.setString(4, end);

        this.deleteEvent.execute();

    }

    @Override
    public void deleteAllEvents(User user) throws SQLException {
        this.deleteAllEvents.setString(1, user.getUsername());
        this.deleteAllEvents.execute();
    }

    @Override
    public void close() throws SQLException {
        this.findAllEvents.close();
        this.insertEvent.close();
        this.deleteEvent.close();
        this.deleteAllEvents.close();
        this.updateEvent.close();
        this.connection.close();
    }
}
