package com.project.Project.validator;

import com.project.Project.exception.ErrorCode;
import com.project.Project.repository.building.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@Component
@RequiredArgsConstructor
public class BuildingExistValidator implements ConstraintValidator<ExistBuilding, Long> {

    private final BuildingRepository buildingRepository;

    @Override
    public void initialize(ExistBuilding constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long buildingId, ConstraintValidatorContext context) {
        if(!buildingRepository.existsById(buildingId)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorCode.BUILDING_NOT_FOUND.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
