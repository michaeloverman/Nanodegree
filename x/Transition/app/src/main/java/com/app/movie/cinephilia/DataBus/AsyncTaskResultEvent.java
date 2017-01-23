package com.app.movie.cinephilia.DataBus;

/**
 * Created by GAURAV on 23-01-2016.
 */
public class AsyncTaskResultEvent {
    private boolean result;
    private String className;

    public AsyncTaskResultEvent(boolean result, String className) {
        this.className = className;
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }

    public String getName(){
        return className;
    }
}
