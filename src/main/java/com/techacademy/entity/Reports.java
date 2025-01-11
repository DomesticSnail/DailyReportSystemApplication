package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
public class Reports {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 日報日付
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    // タイトル
    @Column(length = 255, nullable = false)
    private String title;

    // 内容
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 従業員
    @ManyToOne
    @JoinColumn(name = "employee_code", nullable = false)
    private Employee employee;

    // 削除フラグ (論理削除)
    @Column(name = "delete_flg", columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlag;

    // 登録日時
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}