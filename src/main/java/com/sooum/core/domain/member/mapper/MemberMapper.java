package com.sooum.core.domain.member.mapper;

import com.sooum.core.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import static com.sooum.core.domain.member.dto.AuthDTO.SignUp;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    @Mapping(target = "nickname", expression = "java( getDefaultNickname() )")
    Member from(SignUp dto);

    default String getDefaultNickname() {
        return "Sooum";
    }
}
