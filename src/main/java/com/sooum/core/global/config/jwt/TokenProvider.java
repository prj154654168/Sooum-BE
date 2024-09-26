package com.sooum.core.global.config.jwt;

import com.sooum.core.domain.member.dto.AuthDTO.Token;
import com.sooum.core.domain.member.entity.Role;
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
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static com.sooum.core.domain.member.entity.Role.USER;
import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String REFRESH_TOKEN_HEADER = "Authorization-refresh";
    private static final String BEARER = "Bearer ";
    private static final String ID_CLAIM = "id";
    private static final String ROLE_CLAIM = "role";

    public Token createToken(Long id) {
        return new Token(
                createAccessToken(id, USER),
                createRefreshToken(id)
        );
    }

    public String createAccessToken(Long id, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getAccessTokenExpirationPeriod()))
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .claim(ID_CLAIM, id)
                .claim(ROLE_CLAIM, role)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long id) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getAccessTokenExpirationPeriod()))
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .claim(ID_CLAIM, id)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);  // Decode
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Object detailedValidateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT token has expired."); // 유효기간 만료
            return e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token: " + e.getMessage());   // 지원하지 않는 형식 또는 구조
            return e;
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token: " + e.getMessage()); // 지원하지 않는 형식 또는 손상
            return e;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature: " + e.getMessage()); // 유효하지 않은 서명
            return e;
        } catch (JwtException e) {
            log.info("Invalid JWT token: " + e.getMessage()); // 예상하지 못한 유효성 검사 실패
            return e;
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

    public Optional<String> getAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(ACCESS_TOKEN_HEADER))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> getRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(REFRESH_TOKEN_HEADER))
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

    public LocalTime getExpiration(String token) {
        return LocalDateTime.ofInstant(getClaims(token).getExpiration().toInstant(),
                ZoneId.systemDefault()).toLocalTime();
    }
}
