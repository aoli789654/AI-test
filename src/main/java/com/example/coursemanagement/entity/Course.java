package com.example.coursemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称不能超过 100 个字符")
    @Column(nullable = false, length = 100)
    private String courseName;

    @NotNull(message = "课程学分不能为空")
    @DecimalMin(value = "1.0", message = "课程学分必须在 1 到 5 之间")
    @DecimalMax(value = "5.0", message = "课程学分必须在 1 到 5 之间")
    @Digits(integer = 3, fraction = 2, message = "课程学分最多三位整数且最多两位小数")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal credit;

    @NotNull(message = "最大选课人数不能为空")
    @Positive(message = "最大选课人数必须大于 0")
    @Column(nullable = false)
    private Integer maxEnrollment;

    public Course() {
    }

    public Course(String courseName, BigDecimal credit, Integer maxEnrollment) {
        this.courseName = courseName;
        this.credit = credit;
        this.maxEnrollment = maxEnrollment;
    }

    public Course(Long id, String courseName, BigDecimal credit, Integer maxEnrollment) {
        this.id = id;
        this.courseName = courseName;
        this.credit = credit;
        this.maxEnrollment = maxEnrollment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public Integer getMaxEnrollment() {
        return maxEnrollment;
    }

    public void setMaxEnrollment(Integer maxEnrollment) {
        this.maxEnrollment = maxEnrollment;
    }
}
