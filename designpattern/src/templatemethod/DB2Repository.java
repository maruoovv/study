package templatemethod;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB2Repository extends AbstractRepository {
    private Connection connection;

    public DB2Repository() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:db2", "user", "user");
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
                String phoneNumber = rs.getString("phoneNumber");

                users.add(new User(name, userId, age, phoneNumber));
            }
        } catch(SQLException e) {

        }
        return users;
    }
}
