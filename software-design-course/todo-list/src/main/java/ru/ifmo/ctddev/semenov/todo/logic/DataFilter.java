package ru.ifmo.ctddev.semenov.todo.logic;

import ru.ifmo.ctddev.semenov.todo.dao.TaskDao;
import ru.ifmo.ctddev.semenov.todo.model.Task;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public abstract class DataFilter {
    private static Map<String, DataFilter> filters = createFiltersMap();

    static HashMap<String, DataFilter> createFiltersMap() {
        HashMap<String, DataFilter> filters = new HashMap<>();
        filters.put("all", new AllFilter());
//        filters.put("max", new MaxFilter());
//        filters.put("min", new MinFilter());
        return filters;
    }

    public abstract List<Task> filter(TaskDao taskDao);

    private static class AllFilter extends DataFilter {
        public List<Task> filter(TaskDao taskDao) {
            return taskDao.getTasks();
        }
    };

//    private static class MaxFilter extends DataFilter {
//        public List<Task> filter(TaskDao taskDao) {
//            return taskDao.getProductWithMaxPrice()
//                    .map(Collections::singletonList)
//                    .orElse(Collections.emptyList());
//        }
//    };

//    private static class MinFilter extends DataFilter {
//        public List<Task> filter(TaskDao taskDao) {
//            return taskDao.getProductWithMinPrice()
//                    .map(Collections::singletonList)
//                    .orElse(Collections.emptyList());
//        }
//    };

    public static Optional<DataFilter> getFilterByName(String name) {
        return Optional.ofNullable(filters.get(name));
    }
}
