package ru.ifmo.ctddev.semenov.todo.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ifmo.ctddev.semenov.todo.model.Filter;
import ru.ifmo.ctddev.semenov.todo.model.Task;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class TaskPredicateFactory {
    private static final Predicate<Task> ALL = task -> true;
    private static final Predicate<Task> COMPLETE = Task::isDone;
    private static final Predicate<Task> INCOMPLETE = COMPLETE.negate();

    private static final Map<String, Predicate<Task>> PREDICATES = new HashMap<>();
    static {
        PREDICATES.put("all", ALL);
        PREDICATES.put("complete", COMPLETE);
        PREDICATES.put("incomplete", INCOMPLETE);
    }

    private TaskPredicateFactory() {
    }

    @NotNull
    public static Optional<Predicate<Task>> getPredicate(@NotNull Filter filter) {
        Objects.requireNonNull(filter);
        Predicate<Task> predicate = getStatusPredicateInternal(filter.getStatus());
        if (predicate == null) {
            return Optional.empty();
        }
        if (filter.getList() != null && !filter.getList().isEmpty()) {
            predicate = predicate.and(getListPredicateInternal(filter.getList()));
        }
        return Optional.of(predicate);
    }

    @NotNull
    public static Optional<Predicate<Task>> getListPredicate(@NotNull String list) {
        Objects.requireNonNull(list);
        return Optional.of(getListPredicateInternal(list));
    }

    @NotNull
    public static Optional<Predicate<Task>> getStatusPredicate(@NotNull String status) {
        Objects.requireNonNull(status);
        return Optional.ofNullable(getStatusPredicateInternal(status));
    }

    @NotNull
    private static Predicate<Task> getListPredicateInternal(@NotNull String list) {
        return task -> task.getTaskList().equals(list);
    }

    @Nullable
    private static Predicate<Task> getStatusPredicateInternal(@NotNull String status) {
        return PREDICATES.get(status);
    }

    public static List<String> getStatuses() {
        return new ArrayList<>(PREDICATES.keySet());
    }
}
