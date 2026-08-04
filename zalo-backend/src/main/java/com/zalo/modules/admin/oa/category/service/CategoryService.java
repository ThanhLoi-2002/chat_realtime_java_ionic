package com.zalo.modules.admin.oa.category.service;

import com.zalo.modules.admin.oa.category.dto.request.CategoryRequest;
import com.zalo.modules.admin.oa.category.entity.Category;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.zalo.modules.admin.oa.category.repo.CategoryRepo;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryRepo categoryRepo;

    public List<Category> getAll() {
        return categoryRepo.findAllByOrderByCtDesc();
    }

    public Category create(CategoryRequest req, Long userId) {
        Optional<Category> existCate = categoryRepo.findByNameIgnoreCase(req.getName());

        if(existCate.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nameExist");
        }

        Category e = new Category();
        e.setName(req.getName());
        e.setCode(req.getCode());
        e.setDescription(req.getDescription());
        e.setCu(userId);

        return categoryRepo.save(e);
    }

    public Category update(Long id, CategoryRequest req, Long userId) {
        Optional<Category> existCate = categoryRepo.findById(id);

        if(existCate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
        }

        existCate.get().setName(req.getName());
        existCate.get().setCode(req.getCode());
        existCate.get().setDescription(req.getDescription());
        existCate.get().setEu(userId);

        return categoryRepo.save(existCate.get());
    }

    public Category delete(Long id, Long userId) {
        Optional<Category> existCate = categoryRepo.findById(id);

        if(existCate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
        }

        existCate.get().setStt(-1);
        existCate.get().setEu(userId);

        return categoryRepo.save(existCate.get());
    }
}
