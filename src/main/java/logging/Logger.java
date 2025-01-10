package logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Created by LCC.
 */
public class Logger {
    private Level loggerLevel;
    private boolean useLogColorEmphasis;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_MAGENTA = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BRIGHT_BLACK = "\u001B[90m";
    public static final String ANSI_BRIGHT_RED = "\u001B[91m";
    public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[94m";
    public static final String ANSI_BRIGHT_MAGENTA = "\u001B[95m";
    public static final String ANSI_BRIGHT_CYAN = "\u001B[96m";
    public static final String ANSI_BRIGHT_WHITE = "\u001B[97m";

    /**
     * Logging 작업을 위한 객체 생성.
     * @param loggerLevel : Logger 에 설정할 logging 레벨 기준.
     * @param useLogColorEmphasis : 출력되는 로그에 색깔을 입혀 강조 효과를 줄 것인지 여부.
     */
    public Logger(String loggerLevel, boolean useLogColorEmphasis) {
        if (loggerLevel == null) {
            loggerLevel = Level.INFO.getName();
        }
        this.loggerLevel = Level.parse(loggerLevel);
        this.useLogColorEmphasis = useLogColorEmphasis;
    }

    /**
     * Logger 에 설정된 loggerLevel 의 이름을 반환.
     * @return : 현재 Logger 객체에 설정된 loggerLevel 의 이름.
     */
    public String getLoggerLevelName() {
        return loggerLevel.getName();
    }

    /**
     * Logger 에 설정된 useLogColorEmphasis 의 값을 반환.
     * @return : 현재 Logger 객체에 설정된 useLogColorEmphasis 의 값.
     */
    public boolean getUseLogColorEmphasis() {
        return useLogColorEmphasis;
    }

    /**
     * Logging 레벨 수준을 비교하여 원하는 색깔의 로그를 남김.
     * @param level : Logger 에 설정된 loggerLevel 과 비교하여 로그를 출력할지 판단하는 기준이 되는 logging 레벨 수준.
     * @param message : 로그로 남길 메세지.
     */
    public void log(Level level, String message) {
        final String defaultPrefix = getTime();
        StringBuilder logMessageBuilder = new StringBuilder();

        if (level.intValue() >= loggerLevel.intValue()) {
            if (useLogColorEmphasis) {
                if (level.equals(Level.WARNING)) {
                    logMessageBuilder.append(ANSI_BRIGHT_RED);
                } else if (level.equals(Level.INFO)) {
                    logMessageBuilder.append(ANSI_YELLOW);
                } else if (level.equals(Level.FINE)) {
                    logMessageBuilder.append(ANSI_BRIGHT_BLACK);
                }
            }

            logMessageBuilder.append(defaultPrefix);
            logMessageBuilder.append(message);

            if (useLogColorEmphasis) {
                logMessageBuilder.append(ANSI_RESET);
            }

            System.out.println(logMessageBuilder.toString());
        }
    }

    /**
     * 주어진 format 에 맞게 현재 시간을 String 형태로 반환.
     * @return : 주어진 format 에 맞게 표현 된 현재 시간.
     */
    private String getTime() {
        // 현재 시간 계산.
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[yyyy년 MM월 dd일 HH시 mm분 ss초] ");
        return simpleDateFormat.format(new Date(currentTimeMillis));
    }
}