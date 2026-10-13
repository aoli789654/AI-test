package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.exception.CourseNotFoundException;
import com.example.coursemanagement.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    @Override
    public Course create(Course course) {
        course.setId(null);
        return courseRepository.save(course);
    }

    @Override
    public Course update(Long id, Course course) {
        Course existingCourse = findById(id);
        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setCredit(course.getCredit());
        existingCourse.setMaxEnrollment(course.getMaxEnrollment());
        return courseRepository.save(existingCourse);
    }

    @Override
    public void delete(Long id) {
        courseRepository.delete(findById(id));
    }
}
