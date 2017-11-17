package ru.ifmo.ctddev.semenov.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ifmo.ctddev.semenov.todo.dao.TaskDao;
import ru.ifmo.ctddev.semenov.todo.dao.TaskInMemoryDao;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
@Configuration
public class InMemoryDaoContextConfiguration {
    @Bean
    public TaskDao taskDao() {
        return new TaskInMemoryDao();
    }
}
