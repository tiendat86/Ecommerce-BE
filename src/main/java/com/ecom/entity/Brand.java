package com.ecom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Table
@Entity
@Getter
@Setter
public class Brand extends BaseEntity {
    @Column(name = "name", unique = true)
    private String name;
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private Collection<Product> products;
}
