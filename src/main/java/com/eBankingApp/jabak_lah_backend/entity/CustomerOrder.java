package com.eBankingApp.jabak_lah_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "customer_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @OneToOne(mappedBy = "order")
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private double totalAmount;

    public Double calculateTotalAmount() {
        double totalAmount = 0.0;
        if (items != null) {
            for (OrderItem orderItem : items) {
                totalAmount += orderItem.calculateTotalPrice();
            }
        }
        if (delivery != null) {
            totalAmount += delivery.getDeliverPrice();
        }
        return totalAmount;
    }

    // Other fields and relationships as needed
}
