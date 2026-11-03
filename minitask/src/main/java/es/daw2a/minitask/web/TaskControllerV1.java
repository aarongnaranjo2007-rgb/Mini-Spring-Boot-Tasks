package es.daw2a.minitask.web;

import es.daw2a.minitask.domain.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v1")
public class TaskControllerV1 {
    private final List<Task> inMemory = new ArrayList<>();
    private final AtomicLong seq = new AtomicLong(0);

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/tasks")
    public List<Task> list() {
        return inMemory;
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody Task task) {
        long id = seq.incrementAndGet();
        task.setId(id);
        inMemory.add(task);
        return task;
    }
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") Long id) {
        return inMemory.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}