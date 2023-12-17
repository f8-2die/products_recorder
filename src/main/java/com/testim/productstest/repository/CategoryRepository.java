package com.testim.productstest.repository;

import com.testim.productstest.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // здесь можно добавить кастомные методы для работы с категориями, если нужно
}
