package ddog.domain.notification.port;

public interface UserStatusManage {
    boolean isUserLoggedIn(Long userId);
    void setUserLogIn(Long userId);
    void setUserLogOut(Long userId);
}
