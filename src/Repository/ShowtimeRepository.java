package Repository;

import Domain.Showtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ShowtimeRepository {

    private Connection connection;

    public ShowtimeRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Showtime req) throws SQLException {
        String sql = "INSERT INTO showtime ( Film_ID , Studio_ID , Jam_Tayang) VALUES (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,req.filmId);
        statement.setInt(2,req.studioId);
        statement.setTime(3,req.showtime);
        statement.executeUpdate();
    }

    public Showtime update (Showtime showtime) throws SQLException {

        PreparedStatement statement = this.connection.prepareStatement("UPDATE showtime SET Jam_Tayang = ? WHERE Film_ID = ? AND Studio_ID = ?");

        return showtime;
    }

}
