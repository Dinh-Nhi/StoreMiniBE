package com.storemini.controller.admin.user;

import com.storemini.model.user.entity.UserEntity;
import com.storemini.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping("/getAll")
    public ResponseEntity<List<UserEntity>> getAllStoreInfo() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInfoById(@PathVariable Long id) {
        Optional<UserEntity> info = userRepository.findById(id);
        if (info.isPresent()) {
            return ResponseEntity.ok(info.get());
        } else {
            return ResponseEntity.badRequest().body("User with ID " + id + " not found!");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<UserEntity> saveOrUpdate(@RequestBody UserEntity user) {
        if (user.getId() != null) {
            // Nếu có ID → cập nhật
            Optional<UserEntity> existing = userRepository.findById(user.getId());
            if (existing.isPresent()) {
                UserEntity updateEntity = existing.get();
                updateEntity.setFullName(user.getFullName());
                updateEntity.setUsername(user.getUsername());
                updateEntity.setEmail(user.getEmail());
                updateEntity.setAddress(user.getAddress());
                updateEntity.setPhone(user.getPhone());
                // Nếu người dùng có nhập mật khẩu mới thì cập nhật, còn không giữ nguyên
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    updateEntity.setPassword(user.getPassword());
                }
                updateEntity.setRole(user.getRole());
                updateEntity.setStatus(user.getStatus());
                return ResponseEntity.ok(userRepository.save(updateEntity));
            }
        }
        // Nếu không có ID → tạo mới user
        user.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(userRepository.save(user));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStoreInfo(@PathVariable Long id) {
        Optional<UserEntity> existing = userRepository.findById(id);
        if (existing.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("Deleted successfully with ID: " + id);
        } else {
            return ResponseEntity.badRequest().body("User with ID " + id + " not found!");
        }
    }
}
