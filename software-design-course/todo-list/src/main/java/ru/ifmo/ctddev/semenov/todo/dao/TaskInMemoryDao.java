package ru.ifmo.ctddev.semenov.todo.dao;

import ru.ifmo.ctddev.semenov.todo.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class TaskInMemoryDao implements TaskDao {
    private final AtomicInteger lastId = new AtomicInteger(0);
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    public int addTask(Task task) {
        int id = lastId.incrementAndGet();
        task.setId(id);
        tasks.add(task);
        return id;
    }

    public List<Task> getTasks() {
        return new ArrayList(tasks);
    }

//    public Optional<Task> getProductWithMaxPrice() {
//        return tasks.stream().max(Task.PRICE_COMPARATOR);
//    }

//    public Optional<Task> getProductWithMinPrice() {
//        return tasks.stream().min(Task.PRICE_COMPARATOR);
//    }
}
