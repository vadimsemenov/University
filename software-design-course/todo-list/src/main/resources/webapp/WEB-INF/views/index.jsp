<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="td" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<head>
    <title>TODO List</title>
</head>
<body>

<h3>Select tasks</h3>
<%--@elvariable id="filter" type="ru.ifmo.ctddev.semenov.todo.model.Filter"--%>
<form:form modelAttribute="filter" method="GET" action="/filter-tasks">
    <form:select path="status">
        <form:options items="${statuses}" />
        <%--<form:option value="all">all</form:option>--%>
        <%--<form:option value="max">max</form:option>--%>
        <%--<form:option value="min">min</form:option>--%>
    </form:select>
    <form:select path="list">
        <form:option value="" />
        <form:options items="${lists}" />
    </form:select>
    <input type="submit" value="filter">
</form:form>

<table cellspacing="5" cellpadding="10">
    <c:forEach var="t" items="${tasks}">
    <tr>
        <td>${t.getId()}</td>
        <td>${t.getName()}</td>
        <td>${t.getTaskList()}</td>
        <td>${t.isComplete()}</td>
        <td>
            <%--@elvariable id="task" type="ru.ifmo.ctddev.semenov.todo.model.Task"--%>
            <form:form modelAttribute="task" method="POST" action="/change-status">
                <form:input path="id" value="${t.id}" type="hidden" />
                <form:input path="complete" value="${t.complete}" type="hidden" />
                <input type="submit" value="${t.complete ? "reset" : "complete"}">
            </form:form>
        </td>
        <td>
            <form:form modelAttribute="task" method="POST" action="/remove-task">
                <form:input path="id" value="${t.id}" type="hidden" />
                <input type="submit" value="remove">
            </form:form>
        </td>
    </tr>
    </c:forEach>
</table>

<h3>Add new tasks</h3>
<%--@elvariable id="task" type="ru.ifmo.ctddev.semenov.todo.model.Task"--%>
<form:form modelAttribute="task" method="POST" action="/add-task">
    <table>
    <tr>
        <td><form:label path="name">Name:</form:label></td>
        <td><form:input path="name"/></td>
    </tr>
    <tr>
        <td><form:label path="taskList">Task list:</form:label></td>
        <td><form:input path="taskList"/></td>
    </tr>
    <tr>
        <td><form:label path="complete">Complete:</form:label></td>
        <td><form:input path="complete"/></td>
    </tr>
        <%--<tr>--%>
            <%--<td><form:label path="price">Price:</form:label></td>--%>
            <%--<td><form:input path="price"/></td>--%>
        <%--</tr>--%>
    </table>

    <input type="submit" value="add">
</form:form>
</body>
</html>
