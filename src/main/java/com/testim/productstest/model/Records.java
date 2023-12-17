package com.testim.productstest.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "inventory_records")
public class Records {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    private Date date;

    // Constructors, getters, setters...
    private String action; // Новая колонка для хранения информации о действии (покупка/продажа)

    // Конструкторы, геттеры и сеттеры для остальных полей...

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}