package me.lucasgusmao.financeai.repository;

import me.lucasgusmao.financeai.model.entity.Category;
import me.lucasgusmao.financeai.model.entity.Transaction;
import me.lucasgusmao.financeai.model.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByUserId(UUID userId);

    List<Transaction> findByUserIdAndCategoryType(UUID userId, CategoryType type);

    List<Transaction> findByUserIdAndNameLikeIgnoreCase(UUID userId, String name);

    List<Transaction> findByUserIdAndCategoryTypeAndAmountBetween(UUID userId, CategoryType type, BigDecimal minAmount, BigDecimal maxAmount);
}
