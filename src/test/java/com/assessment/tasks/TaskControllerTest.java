package com.assessment.tasks;

import com.assessment.tasks.dto.CreateTaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_success() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test task");
        request.setPriority(1);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test task"))
                .andExpect(jsonPath("$.priority").value(1))
                .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    void createTask_idempotency_samePayload() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Idempotent task");
        request.setPriority(2);

        String key = "idem-key-" + System.currentTimeMillis();

        MvcResult first = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", key)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult second = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", key)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        assert first.getResponse().getContentAsString()
                .equals(second.getResponse().getContentAsString());
    }

    @Test
    void createTask_idempotency_conflict() throws Exception {
        String key = "conflict-key-" + System.currentTimeMillis();

        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("first task");
        request1.setPriority(1);

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("different task");
        request2.setPriority(2);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", key)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", key)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("conflict"));
    }
}