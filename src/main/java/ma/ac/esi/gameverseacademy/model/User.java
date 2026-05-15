package ma.ac.esi.gameverseacademy.model;

public class User {

    private int id;
    private String login;
    private String password;
    private String role;
    private String username;
    private String avatar;

    // DEFAULT CONSTRUCTOR
    public User() {
    }

    // FULL CONSTRUCTOR
    public User(int id,
                String login,
                String password,
                String role,
                String username,
                String avatar) {

        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    // GETTERS

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    // SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}