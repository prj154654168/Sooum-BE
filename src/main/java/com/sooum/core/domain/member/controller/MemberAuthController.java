package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.member.dto.AuthDTO;
import com.sooum.core.domain.member.dto.AuthDTO.*;
import com.sooum.core.domain.member.service.MemberInfoService;
import com.sooum.core.domain.rsa.service.RsaService;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberAuthController {

    private final MemberInfoService memberInfoService;
    private final RsaService rsaService;

    @GetMapping("/key")
    public ResponseEntity<?> requestPublicKey() {
        return ResponseEntity.ok(ResponseEntityModel.<AuthDTO.Key>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Request public Key successfully")
                        .build()
                )
                .content(rsaService.findPublicKey())
                .build()
                .add(WebMvcLinkBuilder.linkTo(methodOn(MemberAuthController.class).getClass()).slash("/login").withRel("login")));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login dto) {
        return ResponseEntity.ok(ResponseEntityModel.<LoginResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Login successfully")
                        .build()
                )
                .content(memberInfoService.login(dto))
                .build()
                .add(WebMvcLinkBuilder.linkTo(methodOn(MemberAuthController.class).getClass()).slash("/policies").withRel("policies")));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid AcceptPolicies dto) {
        return ResponseEntity.ok(ResponseEntityModel.<SignUpResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Accept policies successfully")
                        .build()
                )
                .content(memberInfoService.signUp(dto))
                .build()
                .add(WebMvcLinkBuilder.linkTo(methodOn(ProfileController.class).getClass()).withRel("updateProfile")));
    }

    @PostMapping("/token")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(ResponseEntity.ok(ResponseEntityModel.<ReissuedToken>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Sign up successfully")
                        .build()
                )
                .content(memberInfoService.reissueAccessToken(request))
                .build()
                .add(WebMvcLinkBuilder.linkTo(methodOn(MemberAuthController.class).getClass()).slash("/login").withRel("login"))));
    }
}
