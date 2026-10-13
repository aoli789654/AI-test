package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    Page<Course> findAll(Pageable pageable);

    Course findById(Long id);

    Course create(Course course);

    Course update(Long id, Course course);

    void delete(Long id);
}
