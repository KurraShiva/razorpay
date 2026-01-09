//package com.ex.razorpay.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AuthenticationResponse {
//    private String token;
//    private String email;
//    private String role;
//    private Long userId;
//    private String message;
//}

package com.ex.razorpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private boolean success;
    private String token;
    private String email;
    private String role;
    private Long userId;
    private String message;
}