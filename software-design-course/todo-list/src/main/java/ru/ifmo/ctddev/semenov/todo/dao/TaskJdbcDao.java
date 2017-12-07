package ru.ifmo.ctddev.semenov.todo.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import ru.ifmo.ctddev.semenov.todo.model.Task;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class TaskJdbcDao extends JdbcDaoSupport implements TaskDao {
    public TaskJdbcDao(DataSource dataSource) {
        super();
        setDataSource(dataSource);
    }

    @Override
    public int addTask(Task task) {
        String sql = "INSERT INTO TASKS (NAME, TASKLIST, COMPLETE) VALUES (?, ?, ?)";
        return getJdbcTemplate().update(sql, task.getName(), task.getTaskList(), task.isComplete());
    }

    @Override
    public List<Task> getTasks() {
        String sql = "SELECT * FROM TASKS";
        return getTasksByRequest(sql);
    }

    @Override
    public List<String> getTaskLists() {
        String sql = "SELECT DISTINCT TASKLIST FROM TASKS";
        return getJdbcTemplate().queryForList(sql, String.class);
    }

    @Override
    public void updateTask(int id, boolean complete) {
        String sql = "UPDATE TASKS SET COMPLETE = '" + (complete ? 1 : 0) + "' WHERE ID = " + id;
        getJdbcTemplate().update(sql);
    }

    @Override
    public void removeTask(int id) {
        String sql = "DELETE FROM TASKS WHERE ID = " + id;
        getJdbcTemplate().update(sql);
    }
//    @Override
//    public Optional<Product> getProductWithMaxPrice() {
//        String sql = "SELECT * FROM TASKS ORDER BY PRICE DESC LIMIT 1";
//        return getTasksByRequest(sql).stream().findFirst();
//    }
//
//    @Override
//    public Optional<Product> getProductWithMinPrice() {
//        String sql = "SELECT * FROM TASKS ORDER BY PRICE LIMIT 1";
//        return getTasksByRequest(sql).stream().findFirst();
//
//    }

    private List<Task> getTasksByRequest(String sql) {
        return getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(Task.class));
    }

}
