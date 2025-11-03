package com.storemini.model.user.repository;


import com.storemini.model.user.entity.MediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<MediaEntity, Long> {
    Optional<MediaEntity> findByFileKey(String fileKey);
    Optional<MediaEntity> findByFileKeyAndMainTrue(String fileKey);
    List<MediaEntity> findAllByFileKey(String fileKey);
}
