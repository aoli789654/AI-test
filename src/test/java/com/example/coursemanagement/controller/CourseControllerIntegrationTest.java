package com.example.coursemanagement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void clearCourses() {
        courseRepository.deleteAll();
    }

    @Test
    void courseCanBeCreatedReadUpdatedAndDeletedThroughUnifiedResponses() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseName":"数据结构","credit":3.5,"maxEnrollment":60}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.courseName").value("数据结构"))
                .andExpect(jsonPath("$.data.credit").value(3.5))
                .andExpect(jsonPath("$.data.maxEnrollment").value(60))
                .andReturn();

        JsonNode createdBody = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long courseId = createdBody.path("data").path("id").asLong();
        assertThat(courseId).isPositive();

        mockMvc.perform(get("/api/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.courseName").value("数据结构"));

        mockMvc.perform(put("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseName":"高级数据结构","credit":4,"maxEnrollment":80}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.courseName").value("高级数据结构"))
                .andExpect(jsonPath("$.data.maxEnrollment").value(80));

        mockMvc.perform(delete("/api/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").doesNotExist());

        mockMvc.perform(get("/api/courses/{id}", courseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void rejectsCreditThatExceedsDatabasePrecisionWithUnifiedResponse() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseName":"超大课程","credit":1000.0,"maxEnrollment":60}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.credit").exists());
    }

    @Test
    void rejectsCourseNameLongerThanColumnLimitWithUnifiedResponse() throws Exception {
        String longName = "x".repeat(101);
        String requestBody = "{\"courseName\":\"" + longName
                + "\",\"credit\":3.5,\"maxEnrollment\":60}";

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.courseName").exists());
    }

    @Test
    void rejectsBlankCourseNameAndCreditsOutsideOneToFiveWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseName":"   ","credit":3.5,"maxEnrollment":60}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("参数校验失败"))
                .andExpect(jsonPath("$.data.courseName").value("课程名称不能为空"));

        for (String credit : List.of("0.5", "5.1")) {
            mockMvc.perform(post("/api/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"courseName":"课程","credit":CREDIT,"maxEnrollment":60}
                                    """.replace("CREDIT", credit)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.data.credit").value("课程学分必须在 1 到 5 之间"));
        }
    }

    @Test
    void acceptsCreditValuesAtBothInclusiveBoundaries() throws Exception {
        for (String credit : List.of("1.0", "5.0")) {
            mockMvc.perform(post("/api/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"courseName":"边界课程","credit":CREDIT,"maxEnrollment":60}
                                    """.replace("CREDIT", credit)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.credit").value(Double.valueOf(credit)));
        }
    }

    @Test
    void courseListSupportsPagingAndReturnsTotalCount() throws Exception {
        courseRepository.saveAll(List.of(
                new Course("课程一", new BigDecimal("2.0"), 30),
                new Course("课程二", new BigDecimal("3.0"), 40),
                new Course("课程三", new BigDecimal("4.0"), 50)));

        mockMvc.perform(get("/api/courses").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(2))
                .andExpect(jsonPath("$.data.records.length()").value(2));
    }

    @Test
    void rejectsOutOfRangePageParametersWithFieldErrorDetails() throws Exception {
        mockMvc.perform(get("/api/courses").param("page", "0").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.page").value("页码必须大于等于 1"))
                .andExpect(jsonPath("$.data.size").value("每页条数不能超过 100"));
    }

}
