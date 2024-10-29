package com.itbangmodkradankanbanapi.Jwt.filters;


import com.itbangmodkradankanbanapi.database2.DTO.JwtRequestUser;
import com.itbangmodkradankanbanapi.database2.DTO.JwtResponse;
import com.itbangmodkradankanbanapi.database2.DTO.JwtResponseWithoutRefreshToken;
import com.itbangmodkradankanbanapi.database2.entities.User;
import com.itbangmodkradankanbanapi.database2.repositories.UserRepo;
import com.itbangmodkradankanbanapi.database2.service.JwtTokenUtil;
import com.itbangmodkradankanbanapi.database2.service.JwtUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = {"http://ip23kp3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepo userRepo;
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid JwtRequestUser jwtRequestUser) {
        User user = userRepo.findByUsername(jwtRequestUser.getUserName());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
        System.out.println(authenticationToken);
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            if (!authentication.isAuthenticated()) {
                throw new UsernameNotFoundException("user or password is incorrect");
            }
            String token = jwtTokenUtil.generateToken(user,"token");
            String refreshToken = jwtTokenUtil.generateToken(user,"refresh_token");
            return ResponseEntity.ok(new JwtResponse(token,refreshToken));
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED , "username and password is incorrect");
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Object> token( @RequestHeader(value = "Authorization", required = false) String token) {
        try{
            String trimToken = token.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(trimToken);
            User user = userRepo.findByUsername(username);
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(trimToken, userDetails)) {
            String accessToken = jwtTokenUtil.generateToken(user,"token");
            return ResponseEntity.ok(new JwtResponseWithoutRefreshToken(accessToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("expire or invalid refresh token");
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token has expired or invalid");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }


    @GetMapping("/validate-token")
        public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String requestTokenHeader) {
        Claims claims = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "JWT Token does not begin with Bearer String");
        }
        return ResponseEntity.ok(claims);
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ping");
    }
}
