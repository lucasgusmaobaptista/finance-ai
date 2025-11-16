package me.lucasgusmao.financeai.service;

import lombok.RequiredArgsConstructor;
import me.lucasgusmao.financeai.model.entity.Transaction;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AuthService authService;

    public Transaction create(Transaction transaction) {
        User currentUser = authService.getCurrentUser();
        transaction.setUser(currentUser);
        repository.save(transaction);
        System.out.println("funcionou:" + transaction);
        return transaction;
    }

    public List<Transaction> getAll() {
        User currentUser = authService.getCurrentUser();
        return repository.findAllByUserId(currentUser.getId());
    }
}
