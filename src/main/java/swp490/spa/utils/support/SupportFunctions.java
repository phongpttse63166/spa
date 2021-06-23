package swp490.spa.utils.support;

import org.springframework.beans.factory.annotation.Autowired;
import swp490.spa.entities.BookingDetailStep;
import swp490.spa.entities.User;
import swp490.spa.services.BookingDetailStepService;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

public class SupportFunctions {
    @Autowired
    public BookingDetailStepService bookingDetailStepService;

    public SupportFunctions() {
    }
    public SupportFunctions(BookingDetailStepService bookingDetailStepService) {
        this.bookingDetailStepService = bookingDetailStepService;
    }

    public void setBookingDetailStepService(BookingDetailStepService bookingDetailStepService) {
        this.bookingDetailStepService = bookingDetailStepService;
    }

    public List<String> getBookTime(Integer totalTime, List<User> userList,
                                    String dateBooking, String isStaff) {
        List<BookingDetailStep> bookingDetailStepList = new ArrayList<>();
        Map<Integer, List<BookingDetailStep>> map = new HashMap<>();
        bookingDetailStepList = bookingDetailStepService.getAllByCurrentDateBooking(Date.valueOf(dateBooking));
        List<String> timeResult = new ArrayList<>();
        Time timeAdd = null;
        int loop = -1;
        if(bookingDetailStepList.size()!=0){
            for (User user : userList) {
                List<BookingDetailStep> listAddIntoMap = new ArrayList<>();
                for (BookingDetailStep bookingDetailStep : bookingDetailStepList) {
                    if(isStaff.equalsIgnoreCase("true")){
                        if(Objects.nonNull(bookingDetailStep.getStaff())){
                            if (bookingDetailStep.getStaff().getId().equals(user.getId())) {
                                listAddIntoMap.add(bookingDetailStep);
                            }
                        }
                    } else {
                        if(Objects.nonNull(bookingDetailStep.getConsultant())){
                            if (bookingDetailStep.getConsultant().getId().equals(user.getId())) {
                                listAddIntoMap.add(bookingDetailStep);
                            }
                        }
                    }
                }
                if(listAddIntoMap.size()==0){
                    listAddIntoMap = bookingDetailStepList;
                }
                map.put(user.getId(), listAddIntoMap);
            }
            for (Map.Entry<Integer, List<BookingDetailStep>> entry : map.entrySet()) {
                String bookTimeFinal = "";
                if (entry.getValue().size() < 2) {
                    Time startTime = entry.getValue().get(0).getStartTime();
                    Time endTime = entry.getValue().get(0).getEndTime();
                    if (entry.getValue().get(0).getEndTime().compareTo(Time.valueOf(Constant.TIME_START_RELAX)) > 0) {
                        loop = calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX),
                                Time.valueOf(Constant.TIME_START_DATE), totalTime);
                        for (int i = 0; i <= loop; i++) {
                            if (i == 0) {
                                timeAdd = Time.valueOf(Constant.TIME_START_DATE);
                            } else {
                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                            }
                            bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                        }
                        if (calculateTimeDuration(startTime, Time.valueOf(Constant.TIME_END_RELAX), totalTime) != -1) {
                            loop = calculateTimeDuration(startTime, Time.valueOf(Constant.TIME_END_RELAX), totalTime);
                            for (int i = 0; i <= loop; i++) {
                                if (i == 0) {
                                    timeAdd = Time.valueOf(Constant.TIME_END_RELAX);
                                } else {
                                    timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                }
                                bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                            }
                        }
                        if (calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE), endTime, totalTime) != -1) {
                            loop = calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE), endTime, totalTime);
                            for (int i = 0; i <= loop; i++) {
                                if (i == 0) {
                                    int min = endTime.getMinutes();
                                    if((min-5)%10 == 0){
                                        timeAdd = Time.valueOf(endTime.toLocalTime().plusMinutes(5));
                                    } else {
                                        timeAdd = endTime;
                                    }
                                } else {
                                    timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                }
                                if (i == loop) {
                                    bookTimeFinal = bookTimeFinal + timeAdd.toString();
                                } else {
                                    bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                }
                            }
                        }
                        timeResult = Arrays.asList(bookTimeFinal.split("-"));
                    } else {
                        if (calculateTimeDuration(startTime, Time.valueOf(Constant.TIME_START_DATE), totalTime) != -1) {
                            loop = calculateTimeDuration(startTime, Time.valueOf(Constant.TIME_START_DATE), totalTime);
                            for (int i = 0; i <= loop; i++) {
                                if (i == 0) {
                                    timeAdd = Time.valueOf(Constant.TIME_START_DATE);
                                } else {
                                    timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                }
                                bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                            }
                        }
                        if (calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX), endTime, totalTime) != -1) {
                            loop = calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX), endTime, totalTime);
                            for (int i = 0; i <= loop; i++) {
                                if (i == 0) {
                                    int min = endTime.getMinutes();
                                    if((min-5)%10 == 0){
                                        timeAdd = Time.valueOf(endTime.toLocalTime().plusMinutes(5));
                                    } else {
                                        timeAdd = endTime;
                                    }
                                } else {
                                    timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                }
                                bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                            }
                        }
                        loop = calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE),
                                Time.valueOf(Constant.TIME_END_RELAX), totalTime);
                        for (int i = 0; i <= loop; i++) {
                            if (i == 0) {
                                timeAdd = Time.valueOf(Constant.TIME_END_RELAX);
                            } else {
                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                            }
                            if (i == loop) {
                                bookTimeFinal = bookTimeFinal + timeAdd.toString();
                            } else {
                                bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                            }
                        }
                        timeResult = Arrays.asList(bookTimeFinal.split("-"));
                    }
                } else {
                    List<BookingDetailStep> listBooking = entry.getValue();
                    for (int i = 0; i < listBooking.size() - 1; i++) {
                        if (i + 1 != listBooking.size()) {
                            int j = i + 1;
                            BookingDetailStep bookingFirst = listBooking.get(i);
                            BookingDetailStep bookingSecond = listBooking.get(j);
                            if (Time.valueOf(Constant.TIME_START_RELAX).compareTo(bookingFirst.getEndTime()) > 0
                                    && Time.valueOf(Constant.TIME_START_RELAX).compareTo(bookingSecond.getEndTime()) > 0) {
                                if (i == 0) {
                                    if (calculateTimeDuration(bookingFirst.getStartTime(),
                                            Time.valueOf(Constant.TIME_START_DATE), totalTime) != -1) {
                                        loop = calculateTimeDuration(bookingFirst.getStartTime(),
                                                Time.valueOf(Constant.TIME_START_DATE), totalTime);
                                        for (int k = 0; k <= loop; k++) {
                                            if (k == 0) {
                                                timeAdd = Time.valueOf(Constant.TIME_START_DATE);
                                            } else {
                                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                            }
                                            bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                        }
                                    }
                                }
                                if (calculateTimeDuration(bookingSecond.getStartTime(), bookingFirst.getEndTime(), totalTime) != -1) {
                                    loop = calculateTimeDuration(bookingSecond.getStartTime(), bookingFirst.getEndTime(), totalTime);
                                    for (int k = 0; k <= loop; k++) {
                                        if (k == 0) {
                                            int min = bookingFirst.getEndTime().getMinutes();
                                            if((min-5)%10 == 0){
                                                timeAdd = Time.valueOf(bookingFirst.getEndTime().toLocalTime().plusMinutes(5));
                                            } else {
                                                timeAdd = bookingFirst.getEndTime();
                                            }
                                        } else {
                                            timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                        }
                                        bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                    }
                                }
                                if (j == listBooking.size() - 1) {
                                    if (calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX),
                                            bookingSecond.getEndTime(), totalTime) != -1) {
                                        loop = calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX),
                                                bookingSecond.getEndTime(), totalTime);
                                        for (int k = 0; k <= loop; k++) {
                                            if (k == 0) {
                                                int min = bookingSecond.getEndTime().getMinutes();
                                                if((min-5)%10 == 0){
                                                    timeAdd = Time.valueOf(bookingSecond.getEndTime().toLocalTime().plusMinutes(5));
                                                } else {
                                                    timeAdd = bookingSecond.getEndTime();
                                                }
                                            } else {
                                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                            }
                                            bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                        }
                                        loop = calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE),
                                                Time.valueOf(Constant.TIME_END_RELAX), totalTime);
                                        for (int l = 0; l <= loop; l++) {
                                            if (l == 0) {
                                                timeAdd = Time.valueOf(Constant.TIME_END_RELAX);
                                            } else {
                                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                            }
                                            if (l == loop) {
                                                bookTimeFinal = bookTimeFinal + timeAdd.toString();
                                            } else {
                                                bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                            }
                                        }
                                    }
                                }
                            } else if (Time.valueOf(Constant.TIME_START_RELAX).compareTo(bookingFirst.getEndTime()) > 0
                                    && Time.valueOf(Constant.TIME_START_RELAX).compareTo(bookingSecond.getEndTime()) < 0) {
                                if (i == 0) {
                                    if (calculateTimeDuration(bookingFirst.getStartTime(),
                                            Time.valueOf(Constant.TIME_START_DATE), totalTime) != -1) {
                                        loop = calculateTimeDuration(bookingFirst.getStartTime(),
                                                Time.valueOf(Constant.TIME_START_DATE), totalTime);
                                        for (int k = 0; k <= loop; k++) {
                                            if (k == 0) {
                                                timeAdd = Time.valueOf(Constant.TIME_START_DATE);
                                            } else {
                                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                            }
                                            bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                        }
                                    }
                                }
                                if (calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX),
                                        bookingFirst.getEndTime(), totalTime) != -1) {
                                    loop = calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX),
                                            bookingFirst.getEndTime(), totalTime);
                                    for (int k = 0; k <= loop; k++) {
                                        if (k == 0) {
                                            int min = bookingFirst.getEndTime().getMinutes();
                                            if((min-5)%10 == 0){
                                                timeAdd = Time.valueOf(bookingFirst.getEndTime().toLocalTime().plusMinutes(5));
                                            } else {
                                                timeAdd = bookingFirst.getEndTime();
                                            }
                                        } else {
                                            timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                        }
                                        bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                    }
                                }
                                if (calculateTimeDuration(bookingSecond.getStartTime(),
                                        Time.valueOf(Constant.TIME_END_RELAX), totalTime) != -1) {
                                    loop = calculateTimeDuration(bookingSecond.getStartTime(),
                                            Time.valueOf(Constant.TIME_END_RELAX), totalTime);
                                    for (int k = 0; k <= loop; k++) {
                                        if (k == 0) {
                                            timeAdd = Time.valueOf(Constant.TIME_END_RELAX);
                                        } else {
                                            timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                        }
                                        bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                    }
                                }
                                if (j == listBooking.size() - 1) {
                                    if (calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE),
                                            bookingSecond.getEndTime(), totalTime) != -1) {
                                        loop = calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE),
                                                bookingSecond.getEndTime(), totalTime);
                                        for (int k = 0; k <= loop; k++) {
                                            if (k == 0) {
                                                int min = bookingFirst.getEndTime().getMinutes();
                                                if((min-5)%10 == 0){
                                                    timeAdd = Time.valueOf(bookingFirst.getEndTime().toLocalTime().plusMinutes(5));
                                                } else {
                                                    timeAdd = bookingFirst.getEndTime();
                                                }
                                            } else {
                                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                            }
                                            if (k == loop) {
                                                bookTimeFinal = bookTimeFinal + timeAdd.toString();
                                            } else {
                                                bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (i == 0) {
                                    loop = calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX),
                                            Time.valueOf(Constant.TIME_START_DATE), totalTime);
                                    for (int k = 0; k <= loop; k++) {
                                        if (k == 0) {
                                            timeAdd = Time.valueOf(Constant.TIME_START_DATE);
                                        } else {
                                            timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                        }
                                        bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                    }
                                    if (calculateTimeDuration(bookingFirst.getStartTime(),
                                            Time.valueOf(Constant.TIME_END_RELAX), totalTime) != -1) {
                                        loop = calculateTimeDuration(bookingFirst.getStartTime(),
                                                Time.valueOf(Constant.TIME_END_RELAX), totalTime);
                                        for (int k = 0; k <= loop; k++) {
                                            if (k == 0) {
                                                timeAdd = Time.valueOf(Constant.TIME_END_RELAX);
                                            } else {
                                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                            }
                                            bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                        }
                                    }
                                }
                                if (calculateTimeDuration(bookingSecond.getStartTime(), bookingFirst.getEndTime(), totalTime) != -1) {
                                    loop = calculateTimeDuration(bookingSecond.getStartTime(), bookingFirst.getEndTime(), totalTime);
                                    for (int k = 0; k <= loop; k++) {
                                        if (k == 0) {
                                            int min = bookingFirst.getEndTime().getMinutes();
                                            if((min-5)%10 == 0){
                                                timeAdd = Time.valueOf(bookingFirst.getEndTime().toLocalTime().plusMinutes(5));
                                            } else {
                                                timeAdd = bookingFirst.getEndTime();
                                            }
                                        } else {
                                            timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                        }
                                        bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                    }
                                }
                                if (j == listBooking.size() - 1) {
                                    if (calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE),
                                            bookingSecond.getEndTime(), totalTime) != -1) {
                                        loop = calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE),
                                                bookingSecond.getEndTime(), totalTime);
                                        for (int k = 0; k <= loop; k++) {
                                            if (k == 0) {
                                                int min = bookingFirst.getEndTime().getMinutes();
                                                if((min-5)%10 == 0){
                                                    timeAdd = Time.valueOf(bookingFirst.getEndTime().toLocalTime().plusMinutes(5));
                                                } else {
                                                    timeAdd = bookingFirst.getEndTime();
                                                }
                                            } else {
                                                timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                                            }
                                            bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (timeResult.size() == 0) {
                        timeResult = Arrays.asList(bookTimeFinal.split("-"));
                    } else {
                        List<String> checkTimeList = Arrays.asList(bookTimeFinal.split("-"));
                        List<String> addTimeList = new ArrayList<>();
                        for (String time : checkTimeList) {
                            if (!checkTimeExisted(time, timeResult)) {
                                addTimeList.add(time);
                            }
                        }
                        addTimeList.addAll(timeResult);
                        Collections.sort(addTimeList);
                        timeResult = addTimeList;
                    }
                }
            }
        } else {
            String bookTimeFinal = "";
            loop = calculateTimeDuration(Time.valueOf(Constant.TIME_START_RELAX),
                    Time.valueOf(Constant.TIME_START_DATE), totalTime);
            for (int i = 0; i <= loop; i++) {
                if (i == 0) {
                    timeAdd = Time.valueOf(Constant.TIME_START_DATE);
                } else {
                    timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                }
                bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
            }
            loop = calculateTimeDuration(Time.valueOf(Constant.TIME_END_DATE),
                    Time.valueOf(Constant.TIME_END_RELAX), totalTime);
            for (int i = 0; i <= loop; i++) {
                if (i == 0) {
                    timeAdd = Time.valueOf(Constant.TIME_END_RELAX);
                } else {
                    timeAdd = Time.valueOf(timeAdd.toLocalTime().plusMinutes(10));
                }
                if (i == loop) {
                    bookTimeFinal = bookTimeFinal + timeAdd.toString();
                } else {
                    bookTimeFinal = bookTimeFinal + timeAdd.toString() + "-";
                }
            }
            timeResult = Arrays.asList(bookTimeFinal.split("-"));
        }
        return timeResult;
    }

    private boolean checkTimeExisted(String time, List<String> timeResult) {
        for (int i = 0; i < timeResult.size(); i++) {
            if (timeResult.get(i).equalsIgnoreCase(time)) {
                return true;
            }
        }
        return false;
    }

    private int calculateTimeDuration(Time timeHigher, Time timeLower, int totalMin) {
        int mins = (int) (timeHigher.getTime() - timeLower.getTime()) / 60000;
        if (mins - totalMin >= 0) {
            int result = (int) (mins - totalMin) / Constant.TIME_BETWEEN_TWO_BOOKING;
            return result;
        }
        return -1;
    }
}