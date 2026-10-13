package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CoursePageQuery;
import com.example.coursemanagement.dto.PageResult;
import com.example.coursemanagement.dto.Result;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<Result<PageResult<Course>>> findAll(
            @Valid @ModelAttribute CoursePageQuery query) {
        Page<Course> coursePage = courseService.findAll(query.toPageable());
        PageResult<Course> pageResult = new PageResult<>(
                coursePage.getContent(),
                coursePage.getTotalElements(),
                coursePage.getTotalPages(),
                query.getPage(),
                query.getSize());
        return ResponseEntity.ok(Result.success(200, "查询成功", pageResult));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<Course>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(Result.success(200, "查询成功", courseService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Result<Course>> create(@Valid @RequestBody Course course) {
        Course createdCourse = courseService.create(course);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(201, "创建成功", createdCourse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<Course>> update(@PathVariable Long id,
                                                @Valid @RequestBody Course course) {
        return ResponseEntity.ok(Result.success(200, "更新成功", courseService.update(id, course)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.ok(Result.success(200, "删除成功", (Void) null));
    }
}
