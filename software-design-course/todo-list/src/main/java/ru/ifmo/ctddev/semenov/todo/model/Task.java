package ru.ifmo.ctddev.semenov.todo.model;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Task {
    private int id;
    private String name;
    private String taskList;
    private boolean done;

    public Task() {
    }

    public Task(int id, String name, String taskList, boolean done) {
        this.id = id;
        this.name = name;
        this.taskList = taskList;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTaskList() {
        return taskList;
    }

    public void setTaskList(String taskList) {
        this.taskList = taskList;
    }
}
