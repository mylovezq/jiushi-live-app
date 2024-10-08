package top.mylove7.live.api.vo;

/**
 * @Author jiushi
 *
 * @Description
 */
public class UserLoginVO {

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLoginVO{" +
                "userId=" + userId +
                '}';
    }
}
