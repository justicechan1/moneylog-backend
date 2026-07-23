package org.example.moneylog.domain.user.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.example.moneylog.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //사용자 고유 ID

    @Column(nullable = false, unique = true, length = 100)
    private String email;  // 로그인 ID

    @Column(nullable = false)
    private String password;  //비밀번호, BCrypt 해시 저장(평문 금지!)

    @Column(nullable = false, length = 50)
    private String nickname;  //닉네임

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    // 수정은 Setter가 아닌 의도가 드러나는 메서드로
    public void update(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
