package me.lucasgusmao.financeai.service;

import lombok.RequiredArgsConstructor;
import me.lucasgusmao.financeai.exceptions.custom.AlreadyExistsException;
import me.lucasgusmao.financeai.model.entity.Category;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final AuthService authService;

    public Category create(Category category) {
        User currentUser = authService.getCurrentUser();
        category.setUser(currentUser);

        if (repository.existsByUserIdAndName(category.getUser().getId(), category.getName())) {
            throw new AlreadyExistsException("Essa categoria já existe!");
        }
        repository.save(category);
        System.out.println("funcionou"+ category);
        return category;
    }

    public List<Category> getAll() {
        User currentUser = authService.getCurrentUser();

        return repository.findAllByUserId(currentUser.getId());
    }

    public Category getById(UUID id) {
        User currentUser = authService.getCurrentUser();

        return repository.findByIdAndUserId(currentUser.getId(), id);
    }

}
