package com.project.Project.domain.enums;

import lombok.Getter;

/**
 * 단점 키워드 선택 : 없음 주차 대중교통 공원산책 치안 경비실 건물관리 분리수거 환기 방습
 *                단열 반려동물 키우기 벌레 층간소음 엘레베이터 동네소음 언덕 마트/편의점 상가 학교/학원
 */
@Getter
public enum DisadvantageKeywordEnum {
    NONE("없음"), PARKING("주차"),  PUBLIC_TRANSPORTATION("대중교통"),
    PARK_WALK("공원산책"), SECURITY("치안"), SECURITY_OFFICE("경비실"),
    BUILDING_MANAGEMENT("건물관리"), SEPARATE_TRASH("분리수거"), VENTILATION("환기"),
    MOISTURE_PROOF("방습"), INSULATION("단열"), PET_RAISING("반려동물 키우기"),
    INSECT("벌레"), FLOOR_NOISE("층간소음"), ELEVATOR("엘리베이터"),
    TOWN_NOISE("동네소음"), HILL("언덕"), MART_CONVENIENCE_STORE("마트/편의점"),
    STORE("상가"), SCHOOL_ACADEMY("학교/학원");

    private String description;

    DisadvantageKeywordEnum(String description) {
        this.description = description;
    }
}
