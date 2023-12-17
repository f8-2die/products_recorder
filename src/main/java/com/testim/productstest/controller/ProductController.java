package com.testim.productstest.controller;
import com.testim.productstest.model.Product;
import com.testim.productstest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // Изменение импорта
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/home")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            byte[] image = product.getImage(); // Получаем байты изображения из объекта Product
            // Отправляем изображение в ответе HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Измените MediaType в зависимости от типа изображения
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Метод для отображения всех товаров
    @GetMapping("/allproducts")
    public String getAllProductsAsHtml(Model model) {
        List<Product> productList = productRepository.findAll();

        model.addAttribute("products", productList);
        // Создание объекта Product для использования в форме добавления товара
        Product product = new Product();

        // Добавление объекта product в модель для передачи в представление
        model.addAttribute("product", product);

        // Возвращение имени представления (products.html)
        return "products";

    }

    // Метод для отображения формы создания нового товара
    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "products"; // Шаблон HTML для создания нового товара
    }


    // Ваш метод для добавления нового продукта с изображением
    @PostMapping("/new")
    public String addNewProduct(@ModelAttribute("product") Product product,
                                @RequestParam("imageFile") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] imageBytes = file.getBytes();
                product.setImage(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибки чтения изображения, если необходимо
            }
        }

        productRepository.save(product);
        return "redirect:/home/allproducts";
    }




    // Метод для обработки запроса на удаление товара по ID
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        // Логика удаления товара из базы данных
        productRepository.deleteById(id);
        return "redirect:/home/allproducts"; // Перенаправление на страницу со списком товаров
    }

    // Метод для отображения формы редактирования товара
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "edit-product :: editForm"; // используем фрагмент шаблона для передачи только формы редактирования
    }



    // Метод для обработки запроса на редактирование товара
// Метод для обработки запроса на редактирование товара
    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              @ModelAttribute("product") Product productDetails,
                              @RequestParam(value = "newImageFile", required = false) MultipartFile newImageFile) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        // Обновление данных товара
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setCategory_id(productDetails.getCategory_id());
        product.setQuantity(productDetails.getQuantity());

        if (newImageFile != null && !newImageFile.isEmpty()) {
            try {
                byte[] newImageBytes = newImageFile.getBytes();
                product.setImage(newImageBytes); // Установка нового изображения
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибки чтения изображения, если необходимо
            }
        }

        // Сохранение обновленных данных товара в базе данных
        productRepository.save(product);

        return "redirect:/home/allproducts"; // Перенаправление на страницу со списком товаров
    }

}