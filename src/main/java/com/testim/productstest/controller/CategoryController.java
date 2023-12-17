package com.testim.productstest.controller;

import com.testim.productstest.model.Category;
import com.testim.productstest.model.Product;
import com.testim.productstest.repository.CategoryRepository;
import com.testim.productstest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
//сделал стартовую страницу homepage для вывода категорий
@RequestMapping("/home")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository; // Предположим, у вас есть репозиторий Product

    @Autowired
    public CategoryController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String getAllCategories(Model model) {
        List<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("categories", categoryList);

        // Создание объекта Category для использования в форме добавления категории
        Category category = new Category();
        model.addAttribute("category", category);

        return "category_list"; // Обновленное имя HTML-файла для списка категорий
    }


    @GetMapping("/{categoryId}/files")
    public String getProductsByCategory(@PathVariable Long categoryId, Model model) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            List<Product> products = productRepository.findByCategoryId(categoryId); // Метод findByCategoryId нужно реализовать в вашем ProductRepository
            model.addAttribute("category", category);
            model.addAttribute("products", products);
            return "category_products"; // Имя вашего HTML-файла для списка товаров в этой категории
        } else {
            return "redirect:/categories/list";
        }
    }
    @GetMapping("/category/image/{categoryId}")
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            byte[] image = category.getImage(); // Получаем байты изображения из объекта Category
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Измените MediaType в зависимости от типа изображения
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/newcategory")
    public String addNewCategory(@ModelAttribute("category") Category category,
                                 @RequestParam("imageFile") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] imageBytes = file.getBytes();
                category.setImage(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибки чтения изображения, если необходимо
            }
        }

        categoryRepository.save(category);
        return "redirect:/home";
    }

    // ... (остальной код контроллера)
    // Метод для отображения формы редактирования категории по ID
    @GetMapping("/edit/category/{categoryId}")
    public String showEditCategoryForm(@PathVariable Long categoryId, Model model) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
        model.addAttribute("category", category);
        return "edit-category"; // Имя вашего HTML-файла для редактирования категории
    }
    // Метод для обновления категории
    // Метод для обновления категории с новым изображением
    @PostMapping("/updateCategory")
    public String updateCategoryWithImage(@ModelAttribute("category") Category updatedCategory,
                                          @RequestParam("newImageFile") MultipartFile newImageFile) {
        Category category = categoryRepository.findById(updatedCategory.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + updatedCategory.getId()));

        category.setName(updatedCategory.getName());
        category.setDescription(updatedCategory.getDescription());
        // Другие поля категории, которые требуется обновить

        if (!newImageFile.isEmpty()) {
            try {
                byte[] newImageBytes = newImageFile.getBytes();
                category.setImage(newImageBytes); // Обновление изображения категории
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибки чтения изображения, если необходимо
            }
        }

        categoryRepository.save(category);
        return "redirect:/home";
    }

    @DeleteMapping("/delete/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
            return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}


