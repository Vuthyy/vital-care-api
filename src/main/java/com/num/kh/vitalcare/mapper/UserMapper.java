package com.num.kh.vitalcare.mapper;

import com.num.kh.vitalcare.dto.request.RegisterRequestDto;
import com.num.kh.vitalcare.dto.request.UserUpdateRequestDto;
import com.num.kh.vitalcare.dto.response.UserResponseDto;
import com.num.kh.vitalcare.entity.UserEntity;
import org.mapstruct.*;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  UserResponseDto to(UserEntity userEntity);

  UserEntity toEntity(RegisterRequestDto requestDto);

  void updateEntityFromDto(UserUpdateRequestDto requestDto, @MappingTarget UserEntity user);
}
