package org.udesc.todo.model;

import com.google.gson.annotations.SerializedName;

public class ToDoModel {
    @SerializedName("id")
    private int id;

    @SerializedName("status")
    private int status;

    @SerializedName("task")
    private String task;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
