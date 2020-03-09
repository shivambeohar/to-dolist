package com.globallogic.to_dolist.models;

public class Task{

    //Keys for saving the data to the fireBase

    public static String NEW_TASK = "new task";
    public static String REMINDER = "reminder";
    public static String DESCRIPTION = "taskDescription";
    public static String DATE = "task_date";
    public static String PROGRESS = "progress_status";
    public static String TIME = "task_time";



    private String mNewTask;
    private String mDescription;
    private String mDate;
    private String mTime;
    private String mProgressStatus;
    private String mReminder;

    public Task(String newTask, String description, String date, String time,String progressStatus) {
        mNewTask = newTask;
        mDescription = description;
        mDate = date;
        mTime = time;
        mProgressStatus = progressStatus;
    }

    public String getProgressStatus() {
        return mProgressStatus;
    }

    public void setProgressStatus(String progressStatus) {
        mProgressStatus = progressStatus;
    }

    public String getNewTask() {
        return mNewTask;
    }

    public void setNewTask(String newTask) {
        mNewTask = newTask;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}