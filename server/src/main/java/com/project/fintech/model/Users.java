package com.project.fintech.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public Users(String snsType, String snsToken) {
      this.snsType = returnSnsType(snsType);
      this.snsToken = snsToken;
    }

    @Column(name = "Userid")
    String userid;
    @Column(name = "UserName")
    String username;

    public enum SnsType {
      GL, //구글
      NA, //네이버
      KA; //카카오
  }
  public SnsType returnSnsType(String snsType){
    if(snsType == SnsType.GL.name()){
      return SnsType.GL;
    }else if(snsType == SnsType.NA.name()){
      return SnsType.NA;
    }else if(snsType == SnsType.KA.name()){
      return SnsType.KA;
    }else{
      return null;
    }
  }
    @Column(name = "snsType")
    @Enumerated(EnumType.STRING)
    private SnsType snsType;
    @Column(name ="snsToken")
    String snsToken;

    @Column(name = "email")
    String email;
  

    @Column(name = "ImageUrl")
    private String imageUrl;

    @Column(name = "BirthDate")
    Date birthDate;

    @Column(name = "ThemeMode")
    boolean themeMode;

    @Column(name = "Version")
    String version;

    // 리프레시 토큰 저장
    @Column(name = "refreshToken")
    String refreshToken;

    @Column(name ="InUserID")
    Integer InUserID;
    @Column(name = "InDateTime")
    Date InDateTime;

    @Column(name ="ModiUserID")
    Integer ModiUserID;
    @Column(name = "ModiDateTime")
    Date ModiDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public Users update(String name, String email, String imageUrl) {
        this.username = name;
        this.email = email;
        this.imageUrl = imageUrl;
        return this;
    }
    public Users(String userid, String snsType, String snsToken, String email, String username, String imageUrl, String refreshToken) {
        this.userid = userid;
        this.snsType = returnSnsType(snsType);
        this.snsToken = snsToken;
        this.email = email;
        this.username = username;
        this.imageUrl = imageUrl;
        this.birthDate = null;
        this.themeMode = false;
        this.version = null;
        this.role = Role.ROLE_USER;
        this.refreshToken = refreshToken;
        // 리프레시토큰
    }
}
