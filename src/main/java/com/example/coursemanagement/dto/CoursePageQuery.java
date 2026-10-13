package com.example.coursemanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CoursePageQuery {
    @Min(value = 1, message = "页码必须大于等于 1")
    private int page = 1;

    @Min(value = 1, message = "每页条数必须大于等于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private int size = 10;

    public CoursePageQuery() {
    }

    public CoursePageQuery(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"));
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
