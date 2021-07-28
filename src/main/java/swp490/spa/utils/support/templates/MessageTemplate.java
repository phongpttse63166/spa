package swp490.spa.utils.support.templates;

public class MessageTemplate {
    public static final String BOOKING_STATUS = "BOOKING";
    public static final String BOOKING_TITLE = "Đặt dịch vụ";
    public static final String BOOKING_MESSAGE = "Bạn nhận được yêu cầu đặt lịch mới lúc %s";

    public static final String ASSIGN_STATUS = "ASSIGN";
    public static final String ASSIGN_TITLE = "Phân công";
    public static final String ASSIGN_MESSAGE = "Bạn đã được xếp vào liệu trình của khách hàng %";

    public static final String FINISH_STATUS = "FINISH";
    public static final String FINISH_TITLE = "Dịch vụ hoàn tất";
    public static final String FINISH_CONSULTATION_MESSAGE = "Liệu trình đã được thêm vào lúc %s";
    public static final String FINISH_MESSAGE = "Một bước của liêu trình đã hoàn thành lúc %s, " +
            "xin khách hàng đánh giá chất lượng";
    public static final String FINISH_ALL_MESSAGE = "Liệu trình đã hoàn thành, xin khách hàng " +
            "đánh giá chất lượng";

    public static final String CHANGE_STAFF_STATUS = "CHANGE_STAFF";
    public static final String CHANGE_STAFF_TITLE = "Đổi nhân viên";
    public static final String CHANGE_STAFF_MESSAGE = "Yêu cầu đổi nhân viên đã gửi lúc %s";
    public static final String CHANGE_STAFF_CUSTOMER_FINISH_MESSAGE = "Yêu cầu đổi nhân viên đã hoàn thành lúc %s";
    public static final String CHANGE_STAFF_NEW_STAFF_FINISH_MESSAGE = "Bạn đã được thêm vào liệu trình " +
            "của khách hàng %s";
    public static final String CHANGE_STAFF_OLD_STAFF_FINISH_MESSAGE = "Bạn không còn tham gia vào liệu trình " +
            "của khách hàng %s";

    public static final String REGISTER_DATE_OFF_STATUS = "REGISTER_DATE_OFF";
    public static final String REGISTER_DATE_OFF_TITLE = "Đăng kí ngày nghỉ";
    public static final String REGISTER_DATE_OFF_MESSAGE = "Bạn nhận được yêu cầu xin nghỉ lúc %s";

    public static final String RATING_STATUS = "RATING";
    public static final String RATING_TITLE = "Đánh giá dịch vụ";
    public static final String RATING_MESSAGE = "Bạn nhận được một đánh giá mới lúc %s";
}
