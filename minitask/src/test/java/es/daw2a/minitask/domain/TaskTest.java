package es.daw2a.minitask.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void newTaskShouldNotBeCompleted() {
        var task = new Task("Comprar leche");
        assertThat(task.isDone()).isFalse();
    }
}