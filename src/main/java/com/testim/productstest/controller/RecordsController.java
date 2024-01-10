package com.testim.productstest.controller;

import com.testim.productstest.model.Category;
import com.testim.productstest.model.Product;
import com.testim.productstest.model.Records;
import com.testim.productstest.repository.CategoryRepository;
import com.testim.productstest.repository.ProductRepository;
import com.testim.productstest.repository.RecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/home")
public class RecordsController {

    private final RecordsRepository recordsRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public RecordsController(RecordsRepository recordsRepository, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.recordsRepository = recordsRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/records")
    public String showRecordsPage(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("record", new Records());
        return "records_page";
    }

    @GetMapping("/getProductsByCategory/{categoryId}")
    @ResponseBody
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @PostMapping("/saveRecord")
    public String saveRecord(@ModelAttribute("record") Records record,
                             @RequestParam("productId") Long productId,
                             @RequestParam("action") String action,
                             @RequestParam("quantity") int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));

        if ("purchase".equals(action)) {
            product.setQuantity(product.getQuantity() + quantity);
        } else if ("sale".equals(action)) {
            if (product.getQuantity() >= quantity) {
                product.setQuantity(product.getQuantity() - quantity);
            } else {
                // Обработка ситуации, когда товара недостаточно для продажи
            }
        }

        productRepository.save(product);

        record.setProduct(product);
        record.setAction(action);
        record.setQuantity(quantity);
        record.setDate(new Date());

        recordsRepository.save(record);

        return "redirect:/home/records";
    }

    @GetMapping("/salesHistory")
    public String showSalesHistory(Model model) {
        List<Records> salesHistory = recordsRepository.findAll(); // Получаем все записи истории продаж
        model.addAttribute("records", salesHistory);
        return "sales_history"; // Имя HTML-файла для истории продаж
    }

}
