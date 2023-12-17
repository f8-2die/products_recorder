package com.testim.productstest.repository;

import com.testim.productstest.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category_id = ?1")
    List<Product> findByCategoryId(Long categoryId);
}
