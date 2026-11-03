package es.daw2a.minitask.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerV1Test {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new TaskControllerV1()).build();
    }

    @Test
    void ping_shouldReturnPong() throws Exception {
        mvc.perform(get("/v1/ping"))
           .andExpect(status().isOk())
           .andExpect(content().string("pong"));
    }

    @Test
    void list_shouldInitiallyBeEmpty() throws Exception {
        mvc.perform(get("/v1/tasks"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void create_shouldReturn201AndTask() throws Exception {
        mvc.perform(post("/v1/tasks")
                .contentType("application/json")
                .content("{\"title\":\"Aprender Spring Boot\"}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(1))
           .andExpect(jsonPath("$.title").value("Aprender Spring Boot"))
           .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void getById_shouldReturnTaskWhenExists() throws Exception {
        mvc.perform(post("/v1/tasks")
            .contentType("application/json")
            .content("{\"title\":\"Tarea para consultar\"}"))
       .andExpect(status().isCreated());

        mvc.perform(get("/v1/tasks/1"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.id").value(1))
       .andExpect(jsonPath("$.title").value("Tarea para consultar"))
       .andExpect(jsonPath("$.done").value(false));
  }

  @Test
  void getById_shouldReturn404WhenNotFound() throws Exception {
    mvc.perform(get("/v1/tasks/999"))
       .andExpect(status().isNotFound());
  }
}