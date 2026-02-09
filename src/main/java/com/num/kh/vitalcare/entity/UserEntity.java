package com.num.kh.vitalcare.entity;

import com.num.kh.vitalcare.common.enumz.AuthProviderType;
import com.num.kh.vitalcare.common.enumz.GenderType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 120)
  private String email;

  @Column(length = 255)
  private String password;

  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Column private Integer age;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private GenderType gender;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private AuthProviderType provider = AuthProviderType.LOCAL;

  @Column(length = 100)
  private String name;

  @Column(name = "avatar_url", length = 500)
  private String avatarUrl;

  @Column(nullable = false)
  @Builder.Default
  private Boolean enabled = true;

  @Column(name = "account_non_locked", nullable = false)
  @Builder.Default
  private Boolean accountNonLocked = true;

  public boolean isLocal() {
    return AuthProviderType.LOCAL.equals(this.provider);
  }
}
