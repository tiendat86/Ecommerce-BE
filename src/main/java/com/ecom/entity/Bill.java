package com.ecom.entity;

import com.ecom.enumuration.EBillStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bill")
    private List<ProductInCart> products;
    private double totalPrice;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EBillStatus status;
}
