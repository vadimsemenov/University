package ru.ifmo.ctddev.semenov.todo.dao;

import ru.ifmo.ctddev.semenov.todo.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class TaskInMemoryDao implements TaskDao {
    private final AtomicInteger lastId = new AtomicInteger(0);
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Override
    public int addTask(Task task) {
        int id = lastId.incrementAndGet();
        task.setId(id);
        tasks.add(task);
        return id;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public List<String> getTaskLists() {
        return tasks.stream()
                .map(Task::getTaskList)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void updateTask(int id, boolean complete) {
        if (0 <= id && id < tasks.size()) {
            tasks.get(id).setComplete(complete);
        }
    }

//    public Optional<Task> getProductWithMaxPrice() {
//        return tasks.stream().max(Task.PRICE_COMPARATOR);
//    }

//    public Optional<Task> getProductWithMinPrice() {
//        return tasks.stream().min(Task.PRICE_COMPARATOR);
//    }
}
