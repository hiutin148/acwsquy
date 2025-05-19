package com.hiutin.awcsquy.entity; // Hoặc package entity của bạn

import com.hiutin.awcsquy.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000) // Cho phép mô tả dài hơn một chút
    private String description;

    private String slug; // Dùng cho URL thân thiện, ví dụ: "dien-thoai-di-dong"

    private String imageUrl; // URL hình ảnh đại diện cho danh mục

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Cho phép danh mục cha - con (đa cấp)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = false)
    // orphanRemoval=false vì khi xóa danh mục cha, có thể bạn muốn set null cho parent_id của con
    // hoặc không cho xóa nếu có con, tùy logic nghiệp vụ.
    private Set<Category> subCategories = new HashSet<>();

    // Quan hệ với Product: Một Category có nhiều Product
    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    // CascadeType.PERSIST: Khi lưu Category, không tự động lưu Product liên quan qua mối quan hệ này.
    // Thường Product sẽ được quản lý riêng.
    // Nếu xóa Category, bạn cần xử lý logic cho các Product thuộc Category đó (ví dụ: set null, hoặc không cho xóa).
    private Set<Product> products = new HashSet<>();

    // Constructor, getter, setter được Lombok tạo
    // Bạn có thể thêm các phương thức helper nếu cần
}