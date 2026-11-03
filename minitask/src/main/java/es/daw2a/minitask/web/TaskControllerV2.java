package es.daw2a.minitask.web;

import es.daw2a.minitask.domain.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v2")
public class TaskControllerV2 {
    private final List<Task> inMemory = new ArrayList<>();
    private final AtomicLong seq = new AtomicLong(0);

    @GetMapping("/tasks")
    public List<Task> list() {
        return inMemory;
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> create(@RequestBody Task task) {
        long id = seq.incrementAndGet();
        task.setId(id);
        inMemory.add(task);
        return ResponseEntity.created(URI.create("/v2/tasks/" + id)).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") Long id) {
        return inMemory.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}