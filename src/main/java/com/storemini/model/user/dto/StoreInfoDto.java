package com.storemini.model.user.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreInfoDto {
    private Long id;

    private String code;

    @Column(name = "parent_code")
    private String parentCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_key")
    private String fileKey;

    private Integer sort = 1;

    private Integer status;

    @Column(length = 255)
    private String link;

    @Column(name = "user_action", nullable = false)
    private String userAction = "";

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @Column(name = "media_id")
    private Long mediaId;
}