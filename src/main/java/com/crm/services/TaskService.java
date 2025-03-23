package com.crm.services;

import com.crm.entities.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class TaskService extends BaseApiService {

    public TaskService(
            RestTemplate restTemplate,
            @Value("${api.laravel.url}") String apiUrl) {
        super(restTemplate, apiUrl);
    }

    public List<Task> getAllTasks() {
        return fetchData("/tasks", new ParameterizedTypeReference<List<Task>>() {});
    }

    public Task getTaskById(Long id) {
        return fetchSingleData("/tasks/" + id, Task.class);
    }

    public long countCompletedTasks(List<Task> tasks) {
        return tasks.stream().filter(Task::isClosed).count();
    }

    public long countInProgressTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> !t.isClosed() && t.getStatus() != null && "In Progress".equals(t.getStatus().getTitle()))
                .count();
    }

    public long countPendingTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> !t.isClosed() && t.getStatus() != null && "Pending".equals(t.getStatus().getTitle()))
                .count();
    }
}