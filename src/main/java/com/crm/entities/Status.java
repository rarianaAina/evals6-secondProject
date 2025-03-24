package com.crm.entities;

import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Status {
    private Long id;
    private String title;
    private String color;
    private String sourceType; // Ajout du champ utilisé dans Laravel pour filtrer

    private List<Task> tasks;
    private List<Lead> leads;
    private List<Project> projects;

    // Méthodes équivalentes aux scopes de Laravel
    public static List<Status> filterByType(List<Status> statuses, String type) {
        return statuses.stream()
                .filter(status -> type.equals(status.getSourceType()))
                .collect(Collectors.toList());
    }

    public static List<Status> typeOfTask(List<Status> statuses) {
        return filterByType(statuses, Task.class.getSimpleName());
    }

    public static List<Status> typeOfLead(List<Status> statuses) {
        return filterByType(statuses, Lead.class.getSimpleName());
    }

    public static List<Status> typeOfProject(List<Status> statuses) {
        return filterByType(statuses, Project.class.getSimpleName());
    }
}
