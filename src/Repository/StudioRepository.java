package Repository;

import Domain.Studio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudioRepository {

    private Connection connection;

    public StudioRepository(Connection conn) {
        this.connection = conn;
    }

    public Studio save(Studio studio) throws SQLException {
        String sql = "INSERT INTO studio (Studio_ID,Nama_Studio, Kapasitas) VALUES (? , ?, ?)";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1,studio.id);
            statement.setString(2, studio.name);
            statement.setInt(3, studio.capacity);
            statement.executeUpdate();

            return studio;
        }
    }

    public Studio update(Studio studio) throws SQLException {
        String sql = "UPDATE studio SET Nama_Studio = ?, Kapasitas = ? WHERE Studio_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, studio.name);
            statement.setInt(2, studio.capacity);
            statement.setInt(3, studio.id);

            statement.executeUpdate();
            return studio;
        }
    }

    public Studio find(int id) throws SQLException {
        String sql = "SELECT Studio_ID, Nama_Studio, Kapasitas FROM studio WHERE Studio_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    Studio studio = new Studio();
                    studio.id = res.getInt("Studio_ID");
                    studio.name = res.getString("Nama_Studio");
                    studio.capacity = res.getInt("Kapasitas");

                    return studio;
                } else {
                    return null;
                }
            }
        }
    }

    public Studio[] findAll() throws SQLException {
        String sql = "SELECT Studio_ID, Nama_Studio, Kapasitas FROM studio";

        try (PreparedStatement statement = this.connection.prepareStatement(sql);
             ResultSet res = statement.executeQuery()) {

            List <Studio> studios = new ArrayList<Studio>();

            while (res.next()) {
                Studio studio = new Studio();
                studio.id = res.getInt("Studio_ID");
                studio.name = res.getString("Nama_Studio");
                studio.capacity = res.getInt("Kapasitas");

                studios.add(studio);

            }
            return studios.toArray(new Studio[0]);
        }
    }

    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM studio WHERE Studio_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
    public Studio findById(int id) throws SQLException {
        String sql = "SELECT Studio_ID, Nama_Studio, Kapasitas FROM studio WHERE Studio_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    Studio studio = new Studio();
                    studio.id = res.getInt("Studio_ID");
                    studio.name = res.getString("Nama_Studio");
                    studio.capacity = res.getInt("Kapasitas");
                    return studio;
                } else {
                    return null; // Return null if not found
                }
            }
        }
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM studio";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}