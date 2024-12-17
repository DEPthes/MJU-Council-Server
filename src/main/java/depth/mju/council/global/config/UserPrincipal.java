package depth.mju.council.global.config;

import depth.mju.council.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final String name;

    public static UserPrincipal from(UserEntity user) {
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEncryptedPwd(),
                user.getEmail(),
                user.getName()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 기본적으로 빈 권한 목록을 반환, 필요시 구현 가능
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true = 잠기지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true = 활성화됨)
    }
}
