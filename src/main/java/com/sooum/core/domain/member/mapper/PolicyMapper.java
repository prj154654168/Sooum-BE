package com.sooum.core.domain.member.mapper;

import com.sooum.core.domain.member.dto.AuthDTO.Policy;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PolicyMapper {

    PolicyTerm from(Policy dto, Member member);
}
