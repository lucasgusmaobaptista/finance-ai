package me.lucasgusmao.financeai.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.lucasgusmao.financeai.model.entity.Goal;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository repository;
    private final AuthService authService;

    public Goal create(Goal goal) {
        User user = authService.getCurrentUser();
        goal.setUser(user);
        goal.setAchieved(false);

        return repository.save(goal);
    }

    public Goal update(UUID id, Goal goal) {
        User user = authService.getCurrentUser();
        Goal foundGoal = repository.findByIdAndUserId(id, user.getId());
        foundGoal.setName(goal.getName());
        foundGoal.setDescription(goal.getDescription());
        foundGoal.setGoalAmount(goal.getGoalAmount());
        foundGoal.setCurrentAmount(goal.getCurrentAmount());
        foundGoal.setStartDate(goal.getStartDate());
        foundGoal.setEndDate(goal.getEndDate());
        return repository.save(foundGoal);
    }

    public Goal markAsAchieved(UUID id) {
        User user = authService.getCurrentUser();
        Goal foundGoal = repository.findByIdAndUserId(id, user.getId());
        BigDecimal goalAmount = foundGoal.getGoalAmount();
        BigDecimal currentAmount = foundGoal.getCurrentAmount();
        if (goalAmount.equals(currentAmount)) {
            foundGoal.setAchieved(true);
        }
        return repository.save(foundGoal);
    }

    public void delete(UUID id) {
        User user = authService.getCurrentUser();
        Goal foundGoal = repository.findByIdAndUserId(id, user.getId());
        repository.delete(foundGoal);
    }

    public List<Goal> getAll() {
        User user = authService.getCurrentUser();
        return repository.findAllByUserId(user.getId());
    }
}
