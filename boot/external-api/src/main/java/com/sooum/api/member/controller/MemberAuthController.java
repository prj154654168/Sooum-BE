package com.sooum.api.member.controller;

import com.sooum.api.member.dto.AuthDTO;
import com.sooum.api.member.dto.AuthDTO.*;
import com.sooum.api.member.service.MemberInfoService;
import com.sooum.api.rsa.service.RsaUseCase;
import com.sooum.global.config.jwt.TokenProvider;
import com.sooum.global.responseform.ResponseEntityModel;
import com.sooum.global.responseform.ResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberAuthController {
    private final MemberInfoService memberInfoService;
    private final RsaUseCase rsaUseCase;
    private final TokenProvider tokenProvider;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @GetMapping("/key")
    public ResponseEntity<?> requestPublicKey() {
        return ResponseEntity.ok(ResponseEntityModel.<AuthDTO.Key>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("Request public Key successfully")
                        .build()
                )
                .content(rsaUseCase.findPublicKey())
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
                .add(WebMvcLinkBuilder.linkTo(methodOn(MemberAuthController.class).getClass()).slash("/sign-up").withRel("sign-up")));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUp dto) {
        return ResponseEntity.ok(ResponseEntityModel.<SignUpResponse>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("sign up successfully")
                        .build()
                )
                .content(memberInfoService.signUp(dto))
                .build()
                .add(WebMvcLinkBuilder.linkTo(methodOn(ProfileController.class).getClass()).withRel("updateProfile")));
    }

    @PostMapping("/token")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(ResponseEntityModel.<ReissuedToken>builder()
                .status(ResponseStatus.builder()
                        .httpStatus(HttpStatus.OK)
                        .httpCode(HttpStatus.OK.value())
                        .responseMessage("reissue token successfully")
                        .build()
                )
                .content(memberInfoService.reissueAccessToken(request))
                .build()
                .add(WebMvcLinkBuilder.linkTo(methodOn(MemberAuthController.class).getClass()).slash("/login").withRel("login")));
    }
}
