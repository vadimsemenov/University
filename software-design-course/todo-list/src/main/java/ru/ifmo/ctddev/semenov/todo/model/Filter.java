package ru.ifmo.ctddev.semenov.todo.model;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Filter {
    private String filter;

    public Filter() {}
    public Filter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
