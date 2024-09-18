package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.member.dto.AuthDTO.Login;
import com.sooum.core.domain.member.dto.AuthDTO.SignUp;
import com.sooum.core.domain.member.usecase.MemberUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberAuthController {

    private final MemberUseCase memberUseCase;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login dto) {
        return ResponseEntity.ok(memberUseCase.login(dto)); // Hateoas
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUp dto) {
        return ResponseEntity.ok(memberUseCase.signUp(dto));
    }
}