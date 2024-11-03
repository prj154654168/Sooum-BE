package com.sooum.api.member.mapper;

import com.sooum.api.member.dto.AuthDTO;
import com.sooum.data.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    @Mapping(target = "nickname", expression = "java( getDefaultNickname() )")
    Member from(AuthDTO.MemberInfo dto, String deviceId);

    default String getDefaultNickname() {
        return "Sooum";
    }
}
