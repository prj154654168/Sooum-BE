package com.sooum.api.member.mapper;

import com.sooum.api.member.dto.AuthDTO;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.PolicyTerm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PolicyMapper {

    PolicyTerm from(AuthDTO.Policy dto, Member member);
}
