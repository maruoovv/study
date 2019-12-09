package templatemethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract public class AbstractRepository {
    protected Connection connection;

    public List<User> getUsers(String userName) {
        List<User> users = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();

            users = fetchResult(rs);
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return users;
        }
    }

    abstract List<User> fetchResult(ResultSet rs);
}
