package com.storemini.security;

import com.storemini.config.jwt.JwtService;
import com.storemini.model.user.entity.UserEntity;
import com.storemini.model.user.repository.UserRepository;
import com.storemini.payload.request.LoginRequest;
import com.storemini.payload.request.RegisterRequest;
import com.storemini.payload.response.AppResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AppResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUserName()).isPresent()) {
            return AppResponse.error("Tên đăng nhập đã tồn tại");
        }

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Tạo user mới
        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.getUserName());
        newUser.setFullName(request.getFullName());
        newUser.setPassword(encodedPassword);
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setAddress(request.getAddress());
        newUser.setRole("USER");
        newUser.setStatus(1);

        userRepository.save(newUser);
        return new AppResponse(2000, "Đăng ký thành công", newUser);
    }


    public AppResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return AppResponse.error("Sai tên đăng nhập hoặc mật khẩu");
        }

        UserEntity user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        // Trả về response
        return AppResponse.success("Đăng nhập thành công", token);
    }

}

