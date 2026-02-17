package com.moneyflow.account.domain.service;


import org.springframework.data.jpa.repository.JpaRepository;

import com.moneyflow.account.domain.entity.ActivatableEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 泛用 BaseService，提供 create、單筆/批次軟刪除
 */
public interface BaseService<T extends ActivatableEntity, ID> {

    // 子類必須提供 Repository
    JpaRepository<T, ID> getRepository();

    // ===== 新增單筆 =====
    default T create(T entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setIsActive(true);
        entity.setCreatedAt(now);
        entity.setLastUpdatedAt(now);
        return getRepository().save(entity);
    }

    // ===== 軟刪除單筆 =====
    default T softDelete(ID id) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found"));

        if (entity.getIsActive() != null && entity.getIsActive()) {
            entity.setIsActive(false);
            entity.setLastUpdatedAt(LocalDateTime.now());
        }

        return getRepository().save(entity);
    }

    // ===== 軟刪除批次 =====
    default List<T> softDelete(List<ID> ids) {
        List<T> entities = getRepository().findAllById(ids);
        LocalDateTime now = LocalDateTime.now();

        for (T entity : entities) {
            if (entity.getIsActive() != null && entity.getIsActive()) {
                entity.setIsActive(false);
                entity.setLastUpdatedAt(now);
            }
        }

        return getRepository().saveAll(entities);
    }
}