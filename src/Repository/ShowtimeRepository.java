package Repository;

import Domain.Showtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowtimeRepository {

    private Connection connection;

    public ShowtimeRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Showtime req) throws SQLException {
        String sql = "INSERT INTO showtime (Film_ID, Studio_ID, Jam_Tayang) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, req.filmId);
            statement.setInt(2, req.studioId);
            statement.setString(3, req.showtime);
            statement.executeUpdate();
        }
    }

    public Showtime update(Showtime showtime) throws SQLException {
        String sql = "UPDATE showtime SET Jam_Tayang = ? WHERE Film_ID = ? AND Studio_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, showtime.showtime);
            statement.setInt(2, showtime.filmId);
            statement.setInt(3, showtime.studioId);

            statement.executeUpdate();
            return showtime;
        }
    }

    public Showtime[] getAll() throws SQLException {
        String sql = "SELECT Showtime_ID,Film_ID,Studio_ID,Jam_Tayang FROM showtime";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            ResultSet res = statement.executeQuery();

            res.last();
            int rowCount = res.getRow();
            res.beforeFirst();

            if(rowCount > 0) {
                Showtime[] result = new Showtime[rowCount];
                int i = 0;

                while (res.next()) {
                    Showtime showtime = new Showtime();
                    showtime.id = res.getInt("Showtime_ID");
                    showtime.filmId = res.getInt("Film_ID");
                    showtime.studioId = res.getInt("Studio_ID");
                    showtime.showtime = res.getString("Jam_Tayang");

                    result[i++] = showtime;
                }

                return result;
            }else {
                return new Showtime[0];
            }
        }
    }

    public Showtime[] findByStudioId(int studioId) throws SQLException {
        String sql = "SELECT Showtime_ID,Film_ID,Studio_ID,Jam_Tayang FROM showtime WHERE Studio_ID = ? ORDER BY Jam_Tayang ASC";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, studioId);
            ResultSet res = statement.executeQuery();

            res.last();
            int rowCount = res.getRow();
            res.beforeFirst();

            if(rowCount > 0) {
                Showtime[] result = new Showtime[rowCount];
                int i = 0;

                while (res.next()) {
                    Showtime showtime = new Showtime();
                    showtime.id = res.getInt("Showtime_ID");
                    showtime.filmId = res.getInt("Film_ID");
                    showtime.studioId = res.getInt("Studio_ID");
                    showtime.showtime = res.getString("Jam_Tayang");

                    result[i++] = showtime;
                }

                return result;
            }else {
                return new Showtime[0];
            }
        }
    }

    public Showtime find(int id) throws SQLException {
        String sql = "SELECT Film_ID,Studio_ID,Jam_Tayang FROM showtime WHERE Showtime_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet res = statement.executeQuery()){
                if (res.next()) {
                    Showtime showtime = new Showtime();
                    showtime.filmId = res.getInt("Film_ID");
                    showtime.studioId = res.getInt("Studio_ID");
                    showtime.showtime = res.getString("Jam_Tayang");

                    return showtime;
                }else {
                    return null;
                }
            }
        }
    }

    public void delete(int id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("DELETE FROM showtime WHERE Showtime_ID = ?");
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public void deleteAll() throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("DELETE FROM showtime");
        statement.executeUpdate();
    }
}
