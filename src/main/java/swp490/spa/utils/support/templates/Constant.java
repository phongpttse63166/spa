package swp490.spa.utils.support.templates;

public class Constant {
    // Page's setting
    public static final int PAGE_DEFAULT = 0;
    public static final int SIZE_DEFAULT = 20;
    public static final int SIZE_MAX = 50;
    public static final String SEARCH_NO_CONTENT = "";
    // Spa's working
    public static final String TIME_START_DATE = "08:30:00";
    public static final String TIME_END_DATE = "21:00:00";
    public static final String TIME_START_RELAX = "12:00:00";
    public static final String TIME_END_RELAX = "13:00:00";
    // Setting Booking
    public static final String DATE_DEFAULT = "2000-02-02";
    public static final String TIME_DEFAULT = "00:00:00";
    public static final int TIME_BETWEEN_TWO_BOOKING = 15;
    public static final int DURATION_OF_CONSULTATION = 30;
    // Firebase's setting
    public static final String STORAGE_URL = "https://storage.googleapis.com/testuploadimgae.appspot.com/%s";
    public static final String FILE_JSON = "testuploadimgae-firebase-adminsdk-tpl07-8b469b8252.json";
    public static final String STORAGE_BUCKET = "testuploadimgae.appspot.com";
    public static final String DATABASE_URL = "https://testuploadimgae-default-rtdb.asia-southeast1.firebasedatabase.app/";
    // Setting send OTP & Password
    public static final String RECEIVER_EMPLOYEE_ID = "1942478711700648091";
    public static final String RECEIVER_CUSTOMER_ID = "4079618529664678643";
    public static final String ACCOUNT_SID = "AC09e33d1014c54d9dbc2f302ca6bc3ff1";
    public static final String AUTH_TOKEN = "68486189aea6416e9d9b5ac66cef11eb";
    public static final String PHONE_SERVICE = "+14062047088";
    public static final String ZALO_URL = "https://graph.zalo.me/v2.0/me";
    public static final String ZALO_OAUTH_URL = "https://oauth.zaloapp.com/v3";
    public final static String APP_ID = "1383309215977390719";
    public final static String APP_SECRET = "h3NeAfGT2FmZ63ATALDu";
    public final static String CODE = "p3QTtRotGGEw9QVfYCDK5F1acTdCxbWjkcJbtiEv6Gk23z7cjfy-1eWEtjBDc1KOzHBQxOEjSmxMJR3Gd_n-FQHjdDsHYqOFu2Mtq-YOVoQT1CJh_Fi8Fw9DnzNIoG0Rc7U0huIr7dowADUUriqt2UjpjPQxkHrhYItbYBNSFqNQHFI2yOyxT9mTy_sre6D6e4RKyjVDDnBdClxvjxHAJUWO_iE0YXjJiZxE-AVPMKgDNSpn-EDRIQTqbfkSwLjiwMxOku6A57UYRuxBqwBuBjHo8eMbxDzPgbqBgkhFXnEz7dhVofbGWUbSfbJkZH1OiQ_lfBeSDpU--PsBmtfDC-sdbxl4Icadwil2kir6U6AKqDN-MIO4swK5aUjeCG";
    public static final String DEFAULT_PASSWORD = "123@123a";
    // Properties
    public static final Integer TOTAL_TIME_DEFAULT = 0;
    public static final int PASSWORD_LENGTH = 12;
    public static final String PROFILE = "Profile";
    public static final String SERVICE = "Service";
    public static final String CATEGORY = "Category";
    public static final String SPA_PACKAGE_SERVICE = "Spa package-service";
    public static final String TREATMENT_SERVICE = "Treatment-service";
    public static final String USER_LOCATION = "User-location";
    public static final String BOOKING = "Booking";
    public static final String BOOKING_DETAIL = "Booking detail";
    public static final String BOOKING_DETAIL_STEP = "Booking detail step";
    public static final String DATE_OFF = "Date off";
    public static final String SPA_PACKAGE = "Spa package";
    public static final String SPA_TREATMENT = "Spa treatment";
    public static final String PASSWORD = "Password";
    public static final String CONSULTATION_CONTENT = "Consultation content";
    public static final String USER = "User";
    public static final String CUSTOMER = "Customer";
    public static final String STAFF = "Staff";
    public static final String CONSULTANT = "Consultant";
    public static final String MANAGER = "Manager";
    public static final String ADMIN = "Admin";
    public static final String SPA = "Spa";
    public static final String RATING = "Rating";
    public static final String NOTIFICATION = "Notification";
    public static final String EMPLOYEE = "Nhân viên";
    public static final String IMAGE = "Hình ảnh";
    public static final String SERVICES = "Danh sách Service";
    public static final String WORKING_STAFF = "lịch làm việc của Staff";
    public static final String WORKING_CONSULTANT = "lịch làm việc của Consultant";
    public static final String LIST_STAFF_FREE = "Danh sách Staff rảnh";
    public static final String LIST_CONSULTANT_FREE = "Danh sách Consultant rảnh";
    public static final String TIME_LIST = "Danh sách thời gian";
    public static final String BOOKING_DETAIL_TREATMENT = "Treatment vào Booking Detail";
    public static final String LIST_CONSULTANT_CHATTING = "Danh sách Consultant cho Chat";
    public static final String CHANGE_STAFF_STATUS_REASON = "CHANGE_STAFF";
    public static final String SKIP_STATUS_REASON = "SKIP";
    public static final String CANCEL_STATUS_REASON = "CANCEL";
    public static final String ZONE_ID = "Asia/Ho_Chi_Minh";
    public static final String TIME_NEXT_STEP = "thời gian cho step kế tiếp";

    // TYPE Notification
    public static final String TREATMENT_FINISH_TYPE = "TREATMENT_FINISH";
    public static final String STEP_FINISH_TYPE = "STEP_FINISH";
    public static final String REMIND_TYPE = "REMIND";
    public static final String CHANGE_STAFF_TYPE = "CHANGE_STAFF";
    public static final String CHANGE_BOOKING_TIME_TYPE = "CHANGE_BOOKING_TIME";
    public static final String ASSIGN_TYPE = "ASSIGN";
    public static final String SKIP_TYPE = "SKIP";
    public static final String CANCEL_TYPE = "CANCEL";
    public static final String APPROVE_DATE_OFF_TYPE = "APPROVE_DATE_OFF";
    public static final String CANCEL_DATE_OFF_TYPE = "CANCEL_DATE_OFF";

    // Month - Month's first date - Month's last date
    public static final Integer JANUARY = 1;
    public static final String FIRST_DATE_JANUARY = "-01-01";
    public static final String LAST_DATE_JANUARY = "-01-31";
    public static final Integer FEBRUARY = 2;
    public static final String FIRST_DATE_FEBRUARY = "-02-01";
    public static final String LAST_DATE_FEBRUARY = "-02-28";
    public static final String LAST_DATE_FEBRUARY_LEAP = "-02-29";
    public static final Integer MARCH = 3;
    public static final String FIRST_DATE_MARCH = "-03-01";
    public static final String LAST_DATE_MARCH = "-03-31";
    public static final Integer APRIL = 4;
    public static final String FIRST_DATE_APRIL = "-04-01";
    public static final String LAST_DATE_APRIL = "-04-30";
    public static final Integer MAY = 5;
    public static final String FIRST_DATE_MAY = "-05-01";
    public static final String LAST_DATE_MAY = "-05-31";
    public static final Integer JUNE = 6;
    public static final String FIRST_DATE_JUNE = "-06-01";
    public static final String LAST_DATE_JUNE = "-06-30";
    public static final Integer JULY = 7;
    public static final String FIRST_DATE_JULY = "-07-01";
    public static final String LAST_DATE_JULY = "-07-31";
    public static final Integer AUGUST = 8;
    public static final String FIRST_DATE_AUGUST = "-08-01";
    public static final String LAST_DATE_AUGUST = "-08-31";
    public static final Integer SEPTEMBER = 9;
    public static final String FIRST_DATE_SEPTEMBER = "-09-01";
    public static final String LAST_DATE_SEPTEMBER = "-09-30";
    public static final Integer OCTOBER = 10;
    public static final String FIRST_DATE_OCTOBER = "-10-01";
    public static final String LAST_DATE_OCTOBER = "-10-31";
    public static final Integer NOVEMBER = 11;
    public static final String FIRST_DATE_NOVEMBER = "-11-01";
    public static final String LAST_DATE_NOVEMBER = "-11-30";
    public static final Integer DECEMBER = 12;
    public static final String FIRST_DATE_DECEMBER = "-12-01";
    public static final String LAST_DATE_DECEMBER = "-12-31";

    // Level_Rating
    public static final Integer RATE_5 = 5;
    public static final Integer RATE_4 = 4;
    public static final Integer RATE_3 = 3;
    public static final Integer RATE_2 = 2;
    public static final Integer RATE_1 = 1;
}
