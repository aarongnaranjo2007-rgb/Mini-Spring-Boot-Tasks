package es.daw2a.minitask.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerV2Test {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new TaskControllerV2()).build();
    }

    @Test
    void createV2_shouldReturn201AndLocation() throws Exception {
        mvc.perform(post("/v2/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Tarea V2\"}"))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", "/v2/tasks/1"))
           .andExpect(jsonPath("$.id").value(1))
           .andExpect(jsonPath("$.title").value("Tarea V2"));
    }

    @Test
    void getsExistingTask_v2() throws Exception {
        mvc.perform(post("/v2/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Leer\"}"));

        mvc.perform(get("/v2/tasks/1"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.title").value("Leer"));
    }

    @Test
    void returnsNotFoundForMissingTask_v2() throws Exception {
        mvc.perform(get("/v2/tasks/99"))
           .andExpect(status().isNotFound());
    }
}