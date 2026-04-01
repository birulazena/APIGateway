package com.github.birulazena.ApiGateway.mapper;

import com.github.birulazena.ApiGateway.dto.request.CreateUserRequestDto;
import com.github.birulazena.ApiGateway.dto.request.RegisterRequestDto;
import com.github.birulazena.ApiGateway.dto.request.UserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserRequestDto toUserRequestDto(CreateUserRequestDto createUserRequestDto);

    RegisterRequestDto toRegisterRequestDto(CreateUserRequestDto createUserRequestDto);
}
