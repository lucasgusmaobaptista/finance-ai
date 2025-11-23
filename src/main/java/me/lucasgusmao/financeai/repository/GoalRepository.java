package me.lucasgusmao.financeai.repository;

import me.lucasgusmao.financeai.model.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {

    List<Goal> findAllByUserId(UUID userId);

    Goal findByIdAndUserId(UUID id, UUID userId);

    Goal findByUserIdAndAchieved(UUID userId, Boolean achieved);
}
