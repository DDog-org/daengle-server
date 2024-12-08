package ddog.notification.application.port;

public interface UserStatusPersist {
    boolean isUserLoggedIn(Long userId);
    void setUserLogIn(Long userId);
    void setUserLogOut(Long userId);
}
