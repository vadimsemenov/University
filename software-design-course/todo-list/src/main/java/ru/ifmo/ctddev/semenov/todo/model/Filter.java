package ru.ifmo.ctddev.semenov.todo.model;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Filter {
    private String status;
    private String list;

    public Filter() {
    }

    public Filter(String status) {
        this.status = status;
    }

    public Filter(String status, String list) {
        this.status = status;
        this.list = list;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }
}
