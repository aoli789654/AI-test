package com.example.coursemanagement.exception;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(Long id) {
        super("未找到课程，ID: " + id);
    }
}
