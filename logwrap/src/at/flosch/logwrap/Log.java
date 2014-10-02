package at.flosch.logwrap;

import android.os.Environment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A wrapper for LogCat that provides an easy way to disable all logging at once.
 * @author Florian Schrofner
 */
public class Log {

    final static String TAG = "LogWrap";

    private final static String mLine   = "================================================================== \n";

    //enum to specify the log-level
    public enum LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR
    }

    private static boolean mEnabled = false;
    private static String mFilePath = "";
    private static long mSession = -1;
    private static LogLevel mDefaultLevel = LogLevel.VERBOSE;

    /**
     * Enables logging, must be called before any logging can take place.
     */
    public static void enable(){
        mSession = System.currentTimeMillis();
        mEnabled = true;
    }

    /**
     * Disables logging again, can be used to block certain logs in the code.
     * Don't forget to re-enable logging, when you need output again.
     */
    public static void disable(){
        mSession = -1;
        mEnabled = false;
    }

    /**
     * Whether logging is enabled inside the logger.
     * Can be used to ignore certain code parts for release versions
     * or the other way round: just call certain methods when using a release version.
     * @return true = logging enabled, false = logging disabled
     */
    public static boolean isDebuggable(){
        return mEnabled;
    }

    /**
     * Sets the default log level that will be used when not specified explicitly.
     * @param _level the level you want to use as default.
     */
    public static void setDefaultLogLevel(LogLevel _level){
        mDefaultLevel = _level;
    }

    /**
     * Sets the default file path of your log files.
     * When you don't change this, the following default path will be used:
     * Environment.getExternalStorageDirectory().toString() + "/logs/"
     * @param _path the path where you want to store your log files.
     */
    public static void setFilePath(String _path){
        mFilePath = _path;
    }

    /**
     * Logs using the default log level.
     * @param _tag the tag you want to use for the log.
     * @param _message the message for the log.
     */
    public static void log(String _tag, String _message){
        log(mDefaultLevel, _tag, _message);
    }

    /**
     * Output the given log to the verbose level.
     * @param _tag the tag you want to use for the log.
     * @param _message the message for the log.
     */
    public static void v(String _tag, String _message){
        log(LogLevel.VERBOSE, _tag, _message);
    }

    /**
     * Output the given log to the debug level.
     * @param _tag the tag you want to use for the log.
     * @param _message the message for the log.
     */
    public static void d(String _tag, String _message){
        log(LogLevel.DEBUG, _tag, _message);
    }

    /**
     * Output the given log to the info level.
     * @param _tag the tag you want to use for the log.
     * @param _message the message for the log.
     */
    public static void i(String _tag, String _message){
        log(LogLevel.INFO, _tag, _message);
    }

    /**
     * Output the given log to the warn level.
     * @param _tag the tag you want to use for the log.
     * @param _message the message for the log.
     */
    public static void w(String _tag, String _message){
        log(LogLevel.WARN, _tag, _message);
    }

    /**
     * Output the given log to the error level.
     * @param _tag the tag you want to use for the log.
     * @param _message the message for the log.
     */
    public static void e(String _tag, String _message){
        log(LogLevel.ERROR, _tag, _message);
    }

    /**
     * Output the message to a a file inside the specified file path of this logger.
     * Every log call to the same filename will be separated by the given tag and the time it was called.
     * Only logs that were made during the same session and have the same filename specified will end up
     * in the same file.
     * @param _filename the name of the file in the specified file path of the logger, which should be used for logging
     * @param _tag the tags to separate multiple logs in the same file (can be used to distinguish requests and responses)
     * @param _message the actual message that should be logged to the file
     */
    public static void f(String _filename, String _tag, String _message){
        logToFile(_filename, _tag, _message);
    }

    /**
     * Outputs the given message to a file with the specified file name inside the logging path of the logger.
     * Logs to the same file during the same session will be separated by the time they where logged inside the file.
     * @see #f(String, String, String)
     * @param _filename the name of the file in the specified file path of the logger, which should be used for logging
     * @param _message the message for the log.
     */
    public static void f(String _filename, String _message){
        logToFile(_filename, _message);
    }

    /**
     * Logs using the specified log level.
     * @param _level the level you want your log to have.
     * @param _tag the tag you want to use for the log.
     * @param _message the message for the log.
     */
    public static void log(LogLevel _level, String _tag, String _message){
        if(mEnabled){
            switch (_level){
                case VERBOSE:
                    android.util.Log.v(_tag, _message);
                    break;

                case DEBUG:
                    android.util.Log.d(_tag, _message);
                    break;

                case INFO:
                    android.util.Log.i(_tag, _message);
                    break;

                case WARN:
                    android.util.Log.w(_tag, _message);
                    break;

                case ERROR:
                    android.util.Log.e(_tag, _message);
                    break;
            }

        }
    }

    /**
     * Logs the specified content to the given filename inside the default file path.
     * @param _fileName the name of the file.
     * @param _header the header for this log (to separate requests and responses for example).
     * @param _content the content you want to write.
     */
    public static void logToFile(String _fileName, String _header, String _content){
        if(mEnabled){
            String filePath = mFilePath;

            if(filePath == null || filePath.equals("")){
                filePath = Environment.getExternalStorageDirectory().toString() + "/logs/";
            }

            //adding the real filename
            String fileName;

            if(mSession >= 0){
                fileName = _fileName + "_" + mSession + ".log";
            } else {
                fileName = _fileName + "_NO_SESSION.log";
            }


            try {
                File file = new File(filePath, fileName);

                file.getParentFile().mkdirs();

                FileWriter writer = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                //formatting the current time into a human readable form
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String currentDate = sdf.format(date);

                bufferedWriter.append(_header + " at " + currentDate + "\n");
                bufferedWriter.append(mLine);
                bufferedWriter.append(_content + "\n");
                bufferedWriter.append(mLine);
                bufferedWriter.close();
                writer.close();
                android.util.Log.v(TAG, "logged file to: " + file.toString());

            } catch (IOException e) {
                android.util.Log.e(TAG, "error when writing file output!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Log content to the specified file, separating every log only by the time it was logged.
     * @param _fileName the name of the file.
     * @param _content the content you want to write.
     */
    public static void logToFile(String _fileName, String _content){
        logToFile(_fileName, _fileName, _content);
    }

    /**
     * Log the content of the given object, separated by the given header.
     * @param _fileName the name of the file.
     * @param _header the header for this log (to separate requests and responses for example).
     * @param _content the object containing the data to write, toString will be invoked on the object.
     */
    public static void logToFile(String _fileName, String _header, Object _content){
        if(_content != null){
            logToFile(_fileName, _header, _content.toString());
        } else {
            logToFile(_fileName, _header, "null");
        }
    }

    /**
     * Log content to the specified file, uses the toString method of the given object.
     * @param _fileName the name of the file.
     * @param _content the object containing the data to write, toString will be invoked on the object.
     */
    public static void logToFile(String _fileName, Object _content){
        if(_content != null){
            logToFile(_fileName, _fileName, _content.toString());
        } else {
            logToFile(_fileName, _fileName, "null");
        }
    }

}
