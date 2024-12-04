package com.sooum.global.config.jwt;

import com.sooum.api.member.dto.AuthDTO;
import com.sooum.data.member.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static com.sooum.data.member.entity.Role.USER;


@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER = "Bearer ";
    private static final String ID_CLAIM = "id";
    private static final String ROLE_CLAIM = "role";

    public AuthDTO.Token createToken(Long id) {
        return new AuthDTO.Token(
                createAccessToken(id, USER),
                createRefreshToken(id, USER)
        );
    }

    public String createAccessToken(Long id, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(Date.from(
                        LocalDateTime.now()
                                .plusDays(jwtProperties.getAccessTokenExpirationPeriodDay())
                                .atZone(ZoneId.of("Asia/Seoul"))
                                .toInstant()
                ))
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .claim(ID_CLAIM, id)
                .claim(ROLE_CLAIM, role)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long id, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(Date.from(
                        LocalDateTime.now()
                                .plusMonths(jwtProperties.getRefreshTokenExpirationPeriodMonth())
                                .atZone(ZoneId.of("Asia/Seoul"))
                                .toInstant()
                        )
                )
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .claim(ID_CLAIM, id)
                .claim(ROLE_CLAIM, role)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);  // Decode
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Role role = getRole(token).orElse(USER);    // orElse를 타는 경우는 refreshToken일 경우이므로 USER로 설정
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role.getField()));
        return new UsernamePasswordAuthenticationToken(new User(String.valueOf(claims.get(ID_CLAIM, Long.class)), "", authorities), token, authorities);
    }

    public Optional<Long> getId(String token) {
        try {
            return Optional.ofNullable(getClaims(token).get(ID_CLAIM, Long.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isAccessToken(String token) {
        return getClaims(token).getSubject().equals(ACCESS_TOKEN_SUBJECT);
    }

    public Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(TOKEN_HEADER))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Optional<Role> getRole(String token) {
        return Optional.of(Role.getRole(getClaims(token).get(ROLE_CLAIM, String.class)));
    }

    public LocalDateTime getExpiration(String token) {
        return getClaims(token).getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
