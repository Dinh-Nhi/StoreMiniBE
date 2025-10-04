package com.storemini.model.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    @Column(name = "parent_code")
    private String parentCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_key")
    private String fileKey;

    private Integer sort = 1;

    private Integer status = 0;

    @Column(length = 255)
    private String link;

    @Column(name = "user_action", nullable = false)
    private String userAction = "";

    @Column(name = "action_date")
    private LocalDateTime actionDate;
}