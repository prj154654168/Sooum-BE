package com.sooum.core.domain.member.mapper;

import com.sooum.core.domain.member.dto.AuthDTO.MemberInfo;
import com.sooum.core.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    @Mapping(target = "nickname", expression = "java( getDefaultNickname() )")
    Member from(MemberInfo dto, String deviceId);

    default String getDefaultNickname() {
        return "Sooum";
    }
}
