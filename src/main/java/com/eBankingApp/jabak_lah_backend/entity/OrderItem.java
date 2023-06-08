package com.eBankingApp.jabak_lah_backend.entity;

import com.eBankingApp.jabak_lah_backend.model.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    private String itemName;
    private int quantity;
    private double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private CustomerOrder order;

    private Category category;
    private String productNumber;

    public double calculateTotalPrice() {
        return quantity * price;
    }

    // Other fields and relationships as needed
}
