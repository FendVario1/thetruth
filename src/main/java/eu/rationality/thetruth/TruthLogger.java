package eu.rationality.thetruth;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class TruthLogger {
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;
    static private WeechatLoggingHandler weechatLog;

    static public void setup() throws IOException {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            if (handler instanceof ConsoleHandler) {
                rootLogger.removeHandler(handler);
            }
        }

        logger.setLevel(Level.CONFIG);
        // TODO rotate logs or use timestamp names?
        fileTxt = new FileHandler("Truth.log");

        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }

    static public void initWeechatLogging(long bufferId) {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        weechatLog = new WeechatLoggingHandler(bufferId);
        weechatLog.setFormatter(formatterTxt);
        logger.addHandler(weechatLog);
    }

    private static class WeechatLoggingHandler extends Handler {
        private static long buffer;

        public WeechatLoggingHandler(long bufferId) {
            buffer = bufferId;
        }

        @Override
        public void publish(LogRecord record) {
            DateFormat simple = new SimpleDateFormat("HH:mm:ss");
            Weechat.getAPIInstance().print(buffer, simple.format(new Date(record.getMillis())) + " at "
                    + record.getSourceClassName() + "." + record.getSourceMethodName());
            Level level = record.getLevel();
            if (level == Level.WARNING || level == Level.SEVERE) {
                Weechat.getAPIInstance().printerr(buffer, record.getLevel() + " " + record.getMessage());
            } else {
                Weechat.getAPIInstance().print(buffer, record.getLevel() + " " + record.getMessage());
            }
            if (record.getThrown() != null) {
                Throwable throwable = record.getThrown();
                Weechat.getAPIInstance().print(buffer, throwable.getMessage());
                StackTraceElement[] stackTrace = throwable.getStackTrace();
                for (StackTraceElement el : stackTrace) {
                    Weechat.getAPIInstance().print(buffer, "\t"+el.toString());
                }
            }
        }

        @Override
        public void flush(){
        }

        @Override
        public void close() {
        }

        // TODO set loglevel?
    }
}
