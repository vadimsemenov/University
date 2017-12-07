package ru.ifmo.ctddev.semenov.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ifmo.ctddev.semenov.todo.dao.TaskDao;
import ru.ifmo.ctddev.semenov.todo.logic.TaskPredicateFactory;
import ru.ifmo.ctddev.semenov.todo.model.Filter;
import ru.ifmo.ctddev.semenov.todo.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
@Controller
public class TaskController {
    @Autowired
    private TaskDao taskDao;

    @RequestMapping(value = "/add-task", method = RequestMethod.POST)
    public String addTask(@ModelAttribute("task") Task task) {
        taskDao.addTask(task);
        return "redirect:/get-tasks";
    }

    @RequestMapping(value = "/get-tasks", method = RequestMethod.GET)
    public String getTasks(ModelMap map) {
        prepareModelMap(map, taskDao.getTasks());
        return "index";
    }

    @RequestMapping(value = "/filter-tasks", method = RequestMethod.GET)
    public String getTasks(@RequestParam String status, @RequestParam String list, ModelMap map) {
        Optional<Predicate<Task>> maybePredicate = TaskPredicateFactory.getPredicate(new Filter(status, list));
        if (!maybePredicate.isPresent()) {
            return "404";
        }
        List<Task> filteredTasks = taskDao.getTasks().stream()
                .filter(maybePredicate.get())
                .collect(Collectors.toList());
        prepareModelMap(map, filteredTasks);
        return "index";
    }

    private void prepareModelMap(ModelMap map, List<Task> filteredTasks) {
        prepareModelMap(map, filteredTasks, taskDao.getTaskLists(), TaskPredicateFactory.getStatuses());
    }

    private void prepareModelMap(ModelMap map, List<Task> tasks, List<String> lists, List<String> statuses) {
        map.addAttribute("tasks", tasks);
        map.addAttribute("lists", lists);
        map.addAttribute("statuses", statuses);
        map.addAttribute("task", new Task());
        map.addAttribute("filter", new Filter());
    }
}
