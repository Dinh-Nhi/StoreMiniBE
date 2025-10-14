package com.storemini.model.user.repository;

import com.storemini.model.user.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByNameIgnoreCase(String name);

    @Query("SELECT c FROM CategoryEntity c WHERE c.isShow = true")
    List<CategoryEntity> findIsShowCategories();
}