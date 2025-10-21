package com.storemini.model.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_key", length = 50)
    private String fileKey;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "root", length = 255)
    private String root;

    @Column(name = "main")
    private Boolean main;

    @Column(name = "user_action", length = 30, nullable = false)
    private String userAction = "";

    @Column(name = "action_date", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime actionDate = LocalDateTime.now();
}
