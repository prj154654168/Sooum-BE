package com.sooum.core.domain.member.mapper;

import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.PolicyTerm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import static com.sooum.core.domain.member.dto.AuthDTO.SignUp;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PolicyMapper {

    PolicyTerm from(SignUp dto, Member member);
}
