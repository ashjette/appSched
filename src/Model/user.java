package Model;

/**
 * @author Ashley jette
 */
public class user {
    public int userId;
    public String userName;
    public String userPassword;

    /**
     * user constructor
     */
    public user(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    /**
     * user id getter
     * @return user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * user name getter
     * @return user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * user password getter
     * @return user password
     */
    public String getUserPassword() {
        return userPassword;
    }
}
