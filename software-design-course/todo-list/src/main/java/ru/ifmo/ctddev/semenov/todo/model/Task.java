package ru.ifmo.ctddev.semenov.todo.model;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Task {
    private int id;
    private String name;
    private String taskList;
    private boolean complete;

    public Task() {
    }

    public Task(int id, String name, String taskList, boolean complete) {
        this.id = id;
        this.name = name;
        this.taskList = taskList;
        this.complete = complete;
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

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getTaskList() {
        return taskList;
    }

    public void setTaskList(String taskList) {
        this.taskList = taskList;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", taskList='" + taskList + '\'' +
                ", complete=" + complete +
                '}';
    }
}
