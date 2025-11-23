package me.lucasgusmao.financeai.repository;

import me.lucasgusmao.financeai.model.entity.Category;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.model.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByUserId(UUID userId);

    List<Category> findByUserIdAndType(UUID userId, CategoryType type);

    Optional<Category> findByUserIdAndNameLikeIgnoreCase(UUID userId, String name);

    boolean existsByUserIdAndName(UUID userId, String name);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
}
