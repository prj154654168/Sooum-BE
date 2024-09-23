package com.sooum.core.domain.member.controller;

import com.sooum.core.domain.member.dto.AuthDTO.Login;
import com.sooum.core.domain.member.dto.AuthDTO.SignUp;
import com.sooum.core.domain.member.service.MemberInfoService;
import com.sooum.core.domain.rsa.service.RsaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberAuthController {

    private final MemberInfoService memberInfoService;
    private final RsaService rsaService;

    @GetMapping("/key")
    public ResponseEntity<?> requestPublicKey() {
        return ResponseEntity.ok(rsaService.save());
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login dto) {
        return ResponseEntity.ok(memberInfoService.login(dto)); // Hateoas
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUp dto) {
        return ResponseEntity.ok(memberInfoService.signUp(dto));
    }

    @PostMapping("/token")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(memberInfoService.reissueAccessToken(request));
    }
}
