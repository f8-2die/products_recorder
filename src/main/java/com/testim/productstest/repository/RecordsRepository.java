package com.testim.productstest.repository;

import com.testim.productstest.model.Records;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordsRepository extends JpaRepository<Records, Long> {
    // Дополнительные методы для работы с записями
}
