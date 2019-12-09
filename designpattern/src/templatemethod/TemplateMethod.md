### 템플릿 메소드 패턴

메소드에서 알고리즘의 골격을 정의한다.  
알고리즘의 여러 단계 중 일부는 서브클래스에서 구현할 수 있다.  
알고리즘의 구조는 그대로 유지하면서 서브클래스에서 특정 단계를 재정의 할 수 있다.
템플릿 메소드 패턴을 사용하면 코드중복을 크게 줄일 수 있고, 쉽게 자식 객체를 추가 및 확장 할 수 있다.  
하지만 추상 메소드가 많아질 수록 관리가 어려워지고, 하위 클래스가 상위 추상 클래스의 메소드를 호출 하는 식으로 
로직이 구성된다면, 혼란을 야기할 수 있다.


간단한 예제를 통해 템플릿 메소드 패턴을 알아보자.

DB에서 결과를 조회하는 간단한 클래스가 있다.  
이 클래스는 이름을 입력받고, 이름과 일치하는 유저의 정보를 반환해준다.

```java
public class User {
    private String userId;
    private String name;
    private String empNo;
    private String phoneNumber;
}

public class DB1Repository {
    private Connection connection;

    public DB1Repository() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:db1", "user", "user");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<User> getUser(String userName) {
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String userId = rs.getString("id");
                String empNo = rs.getString("empno");

                users.add(new User(name, userId, empNo));
            }

            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return users;
        }
    }
}
```
초기에는 데이터베이스 하나를 쓰고 있었고, 유저 정보에는 이름, 아이디, 나이만 저장했다.  
얼마가 지난 후, 유저 정보에 핸드폰 번호를 추가해야 하는 상황이 왔는데, 핸드폰 번호를 추가한 정보는 
내부 사정상 데이터베이스를 분리해야 하는 상황이 왔다.  
새로운 데이터베이스에서 정보를 가져오는 클래스를 만들어보자.

```java

public class DB2Repository {
    private Connection connection;

    public DB2Repository() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1523:db2", "user", "user");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<User> getUser(String userName) {
        List<User> users = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String userId = rs.getString("id");
                int age = rs.getInt("age");
                String phoneNumber = rs.getString("phoneNumber");

                users.add(new User(name, userId, age, phoneNumber));
            }

            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return users;
        }
    }
}
```

앞서 구현했던 DB1Repository 와 새로 만든 DB2Repository 를 비교해보자.  
상당히 많은 양의 코드가 중복됨을 알 수 있다. 중복된 코드를 추출하여 템플릿 메소드 패턴을 적용해보자.

```java
abstract public class AbstractRepository {
    protected Connection connection;

    public List<User> getUsers(String userName) {
        List<User> users = new ArrayList<>();
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

public class DB2Repository extends AbstractRepository {
    private Connection connection;

    public DB2Repository() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1523:db2", "user", "user");
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
```

중복된 부분을 상위의 추상 클래스로 추출하고,  
각 서브 클래스에서 알고리즘의 일부를 구현하도록 하였다.  
위의 예제는 간단하여 굳이 템플릿 메소드 패턴이 아니라 스트래티지 패턴으로도 구현이 가능하지만,  
알고리즘이 복잡하고 공통된 부분이 많다면 템플릿 메소드 패턴을 적용하는 것이 유지보수나 확장에 유리하다.