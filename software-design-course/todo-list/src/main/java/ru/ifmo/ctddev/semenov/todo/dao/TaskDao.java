package ru.ifmo.ctddev.semenov.todo.dao;

import ru.ifmo.ctddev.semenov.todo.model.Task;

import java.util.List;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public interface TaskDao {
    int addTask(Task task);

    List<Task> getTasks();
    List<String> getTaskLists();

    void updateTask(int id, boolean complete);
    void removeTask(int id);

//    Optional<Product> getProductWithMaxPrice();
//    Optional<Product> getProductWithMinPrice();
}
