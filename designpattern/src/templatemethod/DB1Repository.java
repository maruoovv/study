package templatemethod;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB1Repository extends AbstractRepository {
    private Connection connection;

    public DB1Repository() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:db1", "user", "user");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    List<User> fetchResult(ResultSet rs) {
        List<User> users = new ArrayList<>();
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String userId = rs.getString("id");
                int age = rs.getInt("age");

                users.add(new User(name, userId, age));
            }
        } catch(SQLException e) {

        }
        return users;
    }
}
