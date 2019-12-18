package templatemethod;

public class User {
    private String userId;
    private String name;
    private int age;
    private String phoneNumber;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User(String userId, String name, int age) {
        this.userId = userId;
        this.name = name;
        this.age = age;
    }

    public User(String userId, String name, int age, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
    }
}
