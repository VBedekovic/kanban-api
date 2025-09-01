package hr.hivetech.Kanban.API.task;

import jakarta.persistence.*;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriroirty status;
}
