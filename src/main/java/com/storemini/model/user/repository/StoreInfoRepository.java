package com.storemini.model.user.repository;

import com.storemini.model.user.dto.StoreInfoDto;
import com.storemini.model.user.entity.StoreInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreInfoRepository extends JpaRepository<StoreInfoEntity, Long> {

    @Query("""
                SELECT new com.storemini.model.user.dto.StoreInfoDto(
                    s.id,
                    s.code,
                    s.parentCode,
                    s.name,
                    s.fileKey,
                    s.sort,
                    s.status,
                    s.link,
                    s.userAction,
                    s.actionDate,
                    m.id as media_id
                )
                FROM StoreInfoEntity s
                LEFT JOIN MediaEntity m ON s.fileKey = m.fileKey
                WHERE s.id = :id
            """)
    Optional<StoreInfoDto> findByIdWithMedia(@Param("id") Long id);


}



