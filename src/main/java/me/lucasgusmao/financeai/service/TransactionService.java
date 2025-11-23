package me.lucasgusmao.financeai.service;

import lombok.RequiredArgsConstructor;
import me.lucasgusmao.financeai.model.entity.Transaction;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.model.enums.CategoryType;
import me.lucasgusmao.financeai.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AuthService authService;

    public Transaction create(Transaction transaction) {
        User currentUser = authService.getCurrentUser();
        transaction.setUser(currentUser);
        return repository.save(transaction);
    }

    public List<Transaction> getAll() {
        User currentUser = authService.getCurrentUser();
        return repository.findAllByUserId(currentUser.getId());
    }

    public Transaction getById(UUID id) {
        User currentUser = authService.getCurrentUser();
        return repository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    public Transaction update(UUID id, Transaction transaction) {
        Transaction foundTransaction = getById(id);
        foundTransaction.setName(transaction.getName());
        foundTransaction.setAmount(transaction.getAmount());
        foundTransaction.setCategory(transaction.getCategory());
        foundTransaction.setNotes(transaction.getNotes());
        return repository.save(foundTransaction);
    }

    public void delete(UUID id) {
        Transaction transaction = getById(id);
        repository.delete(transaction);
    }

    public List<Transaction> getByCategory(UUID categoryId) {
        User currentUser = authService.getCurrentUser();
        return repository.findAllByUserIdAndCategoryId(currentUser.getId(), categoryId);
    }

    public List<Transaction> getByType(CategoryType type) {
        User currentUser = authService.getCurrentUser();
        return repository.findByUserIdAndCategoryType(currentUser.getId(), type);
    }

    public List<Transaction> getByNameLike(UUID id, String name) {
        User currentUser = authService.getCurrentUser();
        return repository.findByUserIdAndNameLikeIgnoreCase(currentUser.getId(), name);
    }

    public List<Transaction> getByAndAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        User currentUser = authService.getCurrentUser();
        return repository.findByUserIdAndAmountBetween(currentUser.getId(), minAmount, maxAmount);
    }
}
