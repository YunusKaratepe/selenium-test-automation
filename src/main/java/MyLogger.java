import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MyLogger {

    private static Logger logger;

    public MyLogger() {
        logger = LogManager.getLogger(MyLogger.class);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void fatal(String msg) {
        logger.fatal(msg);
    }

}
