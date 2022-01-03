package com.github.zhuaidadaya.MCH.log;

public class Logger {
    private Object loggerName = null;

    public Logger() {

    }

    public Logger(Object loggerName){
        setName(loggerName);
    }

    public void info(Object o) {
        if(loggerName != null)
            Log.writeLog(null, o, false, loggerName,"INFO");
    }

    public void warn(Object o) {
        if(loggerName != null)
            Log.writeLog(null, o, true, loggerName,"WARN");
    }

    public void error(Object o) {
        if(loggerName != null)
            Log.writeLog(null, o, true, loggerName,"ERROR");
    }

    public String getName() {
        return loggerName.toString();
    }

    public Logger setName(Object loggerName) {
        this.loggerName = loggerName;

        return this;
    }
}
