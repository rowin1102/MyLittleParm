// src/main/java/org/spring/projectjs/JPA/App/Entity/Member.java
package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "member")
public class Member {

    // ✅ DB의 문자열과 정확히 일치하게 원복
    public enum Role { ROLE_USER, ROLE_ADMIN }

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "member_idx")
    private Long memberIdx;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "domain", length = 100)
    private String domain;

    @Column(name = "game_point")
    private Long gamePoint;

    @Column(name = "game_exp")
    private Long gameExp;

    @Column(name = "game_level")
    private Long gameLevel;

    // ✅ DB에 ROLE_USER/ROLE_ADMIN 그대로 저장 & 읽기
    @Enumerated(EnumType.STRING)
    @Column(name = "member_auth", nullable = false, length = 30)
    private Role memberAuth;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "postdate", nullable = false)
    private Date postdate;

    @PrePersist
    void onCreate() {
        if (gamePoint == null) gamePoint = 0L;
        if (gameExp == null) gameExp = 0L;
        if (gameLevel == null) gameLevel = 1L;
        if (memberAuth == null) memberAuth = Role.ROLE_USER; // ✅ 기본값 원복
        if (postdate == null) postdate = new Date();
    }
}
