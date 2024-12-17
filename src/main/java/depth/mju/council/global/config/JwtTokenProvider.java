package depth.mju.council.global.config;

import depth.mju.council.domain.user.dto.res.JWTAuthResponse;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${secretKey}")
    private String secretKey;

    public static final String BEARER = "Bearer";

    private final Long REFRESH_TOKEN_VALID_TIME = 14 * 1440 * 60 * 1000L;
    private final Long ACCESS_TOKEN_VALID_TIME = 30 * 60 * 1000L;

    private final UserRepository userRepository;

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 생성 메서드
    public JWTAuthResponse generateToken(String username, Authentication authentication) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow();

        Date currentDate = new Date();
        Date accessTokenExpireDate = new Date(currentDate.getTime() + ACCESS_TOKEN_VALID_TIME);
        Date refreshTokenExpireDate = new Date(currentDate.getTime() + REFRESH_TOKEN_VALID_TIME);

        // Access Token 생성
        String accessToken = Jwts.builder()
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(accessTokenExpireDate)
                .subject(username)
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(refreshTokenExpireDate)
                .subject(username)
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();

        // DB에 리프레시 토큰 저장
        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        JWTAuthResponse response = new JWTAuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType(BEARER);
        response.setAccessTokenExpireDate(ACCESS_TOKEN_VALID_TIME);
        return response;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 복호화
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    // 사용자 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", new ArrayList<>());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Refresh");
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(jwtToken);
            return true;
        } catch (SecurityException e) {
            throw new JwtException("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            throw new JwtException("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT 토큰의 구조가 유효하지 않습니다.");
        }
    }
}
