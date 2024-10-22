package Repository;

import Domain.Studio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudioRepository {

    private Connection connection;

    public StudioRepository(Connection conn) {
        this.connection = conn;
    }

    public void save(Studio studio) throws SQLException {

        String sql = "INSERT INTO studio (Nama_Studio,Kapasitas) VALUES (?,?)";
        PreparedStatement statement = this.connection.prepareStatement(sql);
        statement.setString(1,studio.name);
        statement.setInt(2,studio.capacity);
        statement.executeUpdate();
    }

    public Studio find(int id) throws SQLException {
        String sql = "SELECT Studio_ID,Nama_Studio,Kapasitas FROM studio WHERE Studio_ID = ?";
        PreparedStatement statement = this.connection.prepareStatement(sql);
        statement.setInt(1,id);

        ResultSet res = statement.executeQuery();

        if(res.next()) {
            Studio studio = new Studio();
            studio.id = res.getInt("Studio_ID");
            studio.name = res.getString("Nama_Studio");
            studio.capacity = res.getInt("Kapasitas");

            return studio;
        }else{
            return null;
        }
    }

    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM studio WHERE Studio_ID = ?";
        PreparedStatement statement = this.connection.prepareStatement(sql);
        statement.setInt(1,id);
        statement.executeUpdate();
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM studio";
        PreparedStatement statement = this.connection.prepareStatement(sql);
        statement.executeUpdate();
    }

}
