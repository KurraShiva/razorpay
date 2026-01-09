//package com.ex.razorpay.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "users")
//@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//public class User {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(nullable = false)
//    private String name;
//    
//    @Column(nullable = false, unique = true)
//    private String email;
//    
//    @Column(nullable = false)
//    private String phone;
//    
//    private String address;
//    
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Order> orders = new ArrayList<>();
//    
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//}


//
//package com.ex.razorpay.entity;
//
//import com.ex.razorpay.enumm.Role;
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.Data;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//@Builder
//@Entity
//@Table(name = "users")
//@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//public class User implements UserDetails {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(nullable = false)
//    private String name;
//    
//    @Column(nullable = false, unique = true)
//    private String email;
//    
//    @Column(nullable = false)
//    private String password;
//    
//    @Column(nullable = false)
//    private String phone;
//    
//    private String address;
//    
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Role role;
//    
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Order> orders = new ArrayList<>();
//    
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//    
//    // UserDetails methods
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
//    }
//    
//    @Override
//    public String getUsername() {
//        return email;
//    }
//    
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//    
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//    
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//    
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}

package com.ex.razorpay.entity;

import com.ex.razorpay.enumm.Role;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor  // REQUIRED: No-args constructor for JPA/Hibernate
@AllArgsConstructor // REQUIRED: All-args constructor for Builder
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String phone;
    
    private String address;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_USER;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}