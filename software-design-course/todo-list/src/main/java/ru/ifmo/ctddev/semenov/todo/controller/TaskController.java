package ru.ifmo.ctddev.semenov.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ifmo.ctddev.semenov.todo.dao.TaskDao;
import ru.ifmo.ctddev.semenov.todo.logic.DataFilter;
import ru.ifmo.ctddev.semenov.todo.model.Filter;
import ru.ifmo.ctddev.semenov.todo.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
@Controller
public class TaskController {
    @Autowired
    private TaskDao taskDao;

    @RequestMapping(value = "/add-task", method = RequestMethod.POST)
    public String addQuestion(@ModelAttribute("task") Task task) {
        taskDao.addTask(task);
        return "redirect:/get-tasks";
    }

    @RequestMapping(value = "/get-tasks", method = RequestMethod.GET)
    public String getTasks(ModelMap map) {
        prepareModelMap(map, taskDao.getTasks());
        return "index";
    }

    @RequestMapping(value = "/filter-tasks", method = RequestMethod.GET)
    public String getTasks(@RequestParam String filter, ModelMap map) {
        Optional<DataFilter> dataFilter = DataFilter.getFilterByName(filter);
        dataFilter.ifPresent(dataFilter1 -> prepareModelMap(map, dataFilter1.filter(taskDao)));

        return "index";
    }

    private void prepareModelMap(ModelMap map, List<Task> tasks) {
        map.addAttribute("tasks", tasks);
        map.addAttribute("task", new Task());
        map.addAttribute("filter", new Filter());
    }
}
