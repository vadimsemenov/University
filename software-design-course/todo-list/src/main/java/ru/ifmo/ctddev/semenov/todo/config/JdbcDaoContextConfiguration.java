package ru.ifmo.ctddev.semenov.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.ifmo.ctddev.semenov.todo.dao.TaskJdbcDao;

import javax.sql.DataSource;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class JdbcDaoContextConfiguration {
    @Bean
    public TaskJdbcDao taskJdbcDao(DataSource dataSource) {
        return new TaskJdbcDao(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:tasks.db");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
}
