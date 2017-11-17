package ru.akirakozov.sd.refactoring.http;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpBuilder {
    private final StringBuilder builder = new StringBuilder();

    private State state = State.OPENED;

    public static HttpBuilder createOpen() {
        HttpBuilder builder = new HttpBuilder();
        builder.open();
        return builder;
    }

    private HttpBuilder() {
    }

    private void open() {
        checkState(State.OPENED);
        builder.append("<html><body>");
        state = State.ACTIVE;
    }

    public HttpBuilder appendHeader(String h1) {
        checkState(State.ACTIVE);
        builder.append("<h1>")
                .append(h1)
                .append("</h1>");
        return this;
    }

    public HttpBuilder appendLine(String line) {
        checkState(State.ACTIVE);
        builder.append(line).append("</br>");
        return this;
    }

    public HttpBuilder close() {
        if (state == State.CLOSED) {
            return this;
        }
        checkState(State.ACTIVE);
        builder.append("</body></html>");
        state = State.CLOSED;
        return this;
    }

    public void writeTo(HttpServletResponse response) {
        close();
        try {
            response.getWriter().println(builder.toString());

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkState(State expected) {
        if (state != expected) {
            throw new IllegalStateException(state + ", " + expected + " expected");
        }
    }

    private enum State {
        OPENED, ACTIVE, CLOSED
    }
}
