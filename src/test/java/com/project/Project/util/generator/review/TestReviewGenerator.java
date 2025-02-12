package com.project.Project.util.generator.review;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Project.aws.s3.FileService;
import com.project.Project.config.AmazonConfig;
import com.project.Project.config.WebClientConfig;
import com.project.Project.controller.review.dto.ReviewRequestDto;
import com.project.Project.domain.member.Member;
import com.project.Project.domain.review.Review;
import com.project.Project.repository.building.BuildingCustomRepository;
import com.project.Project.repository.building.BuildingRepository;
import com.project.Project.repository.member.MemberRepository;
import com.project.Project.repository.review.ReviewEventListener;
import com.project.Project.service.building.BuildingGenerator;
import com.project.Project.service.building.BuildingService;
import com.project.Project.service.fileProcess.ReviewImageProcess;
import com.project.Project.service.member.MemberService;
import com.project.Project.service.review.ReviewCategoryService;
import com.project.Project.service.review.ReviewGenerator;
import com.project.Project.service.review.ReviewKeywordService;
import com.project.Project.service.review.ReviewService;
import com.project.Project.unit.repository.RepositoryTestConfig;
import com.project.Project.util.ApplicationContextServe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ReviewGenerator.class, ReviewCategoryService.class, ReviewKeywordService.class, MemberService.class, ReviewService.class, ReviewImageProcess.class, FileService.class, BuildingService.class, BuildingCustomRepository.class, BuildingGenerator.class, ApplicationContextServe.class
        , ReviewEventListener.class,
}))
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RepositoryTestConfig.class, WebClientConfig.class, AmazonConfig.class})
@Commit
public class TestReviewGenerator {

    @Autowired
    BuildingRepository buildingRepository;
    @Autowired
    BuildingService buildingService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MockMvc mockMvc;

    private ReviewCreateConfigurer configurer;


    @BeforeEach
    public void setup() {
        this.configurer = new ReviewCreateConfigurer(this.buildingRepository, this.buildingService);
    }

    private void saveReview(Long targetBuildingId, Long targetMemberId) throws Exception {
        Member member = this.memberService.findById(targetMemberId).orElseGet(() -> this.memberRepository.findAll().get(0));
        ReviewRequestDto.ReviewCreateDto request = this.configurer.setBasics(targetBuildingId).build();
        mockMvc.perform(
                        post("/building/room/review").with(csrf())
                                .flashAttr("request", request)
                ).andExpect(status().isOk())
                .andDo(print());
        System.out.println("AAAAAAA");
    }

    @Test
    public void createReviewDto() throws IOException {
        File file = new File(Paths.get("src/test/java/com/project/Project/generator/review/testCase/case.json").toUri());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Review> reviewList = new ArrayList<>();

        List<TestCase> testCaseList = objectMapper.readValue(file, new TypeReference<>() {
        });
        testCaseList.forEach(x -> System.out.println(x.toString()));
        testCaseList.stream().forEach(testCase -> {
            List<Long> buildingIds = testCase.getBuildingIds().stream().mapToLong((buildingId) -> buildingId.getId()).boxed().collect(Collectors.toList());
            int i = 0;
            for (Long buildingId : buildingIds) {
                try {
                    ReviewRequestDto.ReviewCreateDto request = this.configurer.setBasics(buildingId).setReviewImageList(null).build();
                    String fileName = "testJson" + i + ".json";
                    i++;
                    String filePath = "src/test/java/com/project/Project/generator/review/testCase/" + fileName;
                    File jsonFile = new File(filePath);
                    if (!jsonFile.exists()) {
                        file.createNewFile();
                    }

                    BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                    String json = objectMapper.writeValueAsString(request);
                    writer.newLine();
                    writer.write(json);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void createReviews() throws IOException {
        File file = new File(Paths.get("src/test/java/com/project/Project/generator/review/testCase/case.json").toUri());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Review> reviewList = new ArrayList<>();

        List<TestCase> testCaseList = objectMapper.readValue(file, new TypeReference<>() {
        });
        testCaseList.forEach(x -> System.out.println(x.toString()));
        testCaseList.stream().forEach(testCase -> {
            List<Long> buildingIds = testCase.getBuildingIds().stream().mapToLong((buildingId) -> buildingId.getId()).boxed().collect(Collectors.toList());
            for (Long buildingId : buildingIds) {
                try {
                    saveReview(buildingId, testCase.getMemberId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
