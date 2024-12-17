package depth.mju.council.domain.user.controller;

import depth.mju.council.domain.user.dto.req.LoginReq;
import depth.mju.council.domain.user.dto.req.RegisterReq;
import depth.mju.council.domain.user.dto.res.JWTAuthResponse;
import depth.mju.council.domain.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/health_check")
    public String status() {
        return "It's Working in User Service";
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> login(
            @Parameter(description = "Schemas의 LoginReq를 참고해주세요.", required = true) @RequestBody @Valid LoginReq loginReq) {
        JWTAuthResponse token = userService.login(loginReq);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Parameter(description = "Schemas의 RegisterReq를 참고해주세요.", required = true) @RequestBody @Valid RegisterReq registerReq) {
        String response = userService.register(registerReq);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<JWTAuthResponse> reissueToken(
            @RequestHeader("RefreshToken") String refreshToken) {
        return ResponseEntity.ok(userService.reissueToken(refreshToken));
    }

}