package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.database.DatabaseUtils;
import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.RepositoryException;

import javax.sql.DataSource;
import java.sql.*;

public class EventRepository extends XRepository {

    public Event find(long id) {
        try {
            Object[] ret = findResultSet(id, "Event");
            return toThis((ResultSetMetaData) ret[0], (ResultSet) ret[1]);
        } catch (SQLException e) {
            throw new RepositoryException("Can't find Event.", e);
        }
    }

    private Event toThis(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Event event = new Event();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id":
                    event.setId(resultSet.getLong(i));
                    break;
                case "userId":
                    event.setUserId(resultSet.getLong(i));
                    break;
                case "type":
                    event.setTypeEvent(resultSet.getString(i));
                    break;
                case "creationTime":
                    event.setCreationTime(resultSet.getDate(i));
                    break;
                default:
                    // No operations.
            }
        }
        return event;
    }

    public void save(Event event, User user) {
        event.setUserId(user.getId());
        try (PreparedStatement statement = getReadyStatement("INSERT INTO `Event` (`userId`, `type`, `creationTime`) VALUES (?, ?, NOW())", new Object[]{user.getId(), event.getTypeEventString()}, true)) {
            if (statement.executeUpdate() != 1) {
                throw new RepositoryException("Can't save User.");
            } else {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getLong(1));
                    event.setCreationTime(find(event.getId()).getCreationTime());
                } else {
                    throw new RepositoryException("Can't save Event [no autogenerated fields].");
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Can't save Event.", e);
        }
    }
}
