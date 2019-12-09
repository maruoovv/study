package templatemethod;

import java.sql.*;

public class TemplateMethod {

    Connection connection;
    public static void main(String[] args) {


    }

    public void test(String id) {
        try {
            connection = DriverManager.getConnection("test", "test", "test");
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE id = ?");
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String userId = rs.getString("id");

            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fetchResult(ResultSet rs) {
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String userId = rs.getString("id");

            }
        } catch(Exception e) {

        }
    }
}
