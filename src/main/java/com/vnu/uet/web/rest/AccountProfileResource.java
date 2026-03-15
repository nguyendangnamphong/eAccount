package com.vnu.uet.web.rest;

import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.service.AccountManagementService;
import com.vnu.uet.service.UserProfileService;
import com.vnu.uet.service.dto.ProfileDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountProfileResource {

    private final UserProfileService userProfileService;
    private final AccountManagementService accountManagementService;

    public AccountProfileResource(UserProfileService userProfileService, AccountManagementService accountManagementService) {
        this.userProfileService = userProfileService;
        this.accountManagementService = accountManagementService;
    }

    // Temporary mock for current user email since Auth service is not fully integrated yet
    private String getCurrentUserEmail() {
        return "user@vnu.uet"; 
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        String email = getCurrentUserEmail();
        
        UserProfile profile = userProfileService.getUserProfileByEmail(email).orElseGet(() -> {
            // Return a mock profile if not found in DB yet for testing
            UserProfile defaultProfile = new UserProfile();
            defaultProfile.setEmail(email);
            defaultProfile.setFirstName("Nguyễn Văn A");
            defaultProfile.setPhone("0912345678");
            defaultProfile.setGender("MALE");
            defaultProfile.setPosition("Nhân viên");
            defaultProfile.setJob("Kế toán");
            defaultProfile.setDepartment("Phòng BI");
            defaultProfile.setAvatar("https://s3.cloud/eaccount/avatars/user_a.png");
            return defaultProfile;
        });

        Map<String, Object> response = new HashMap<>();
        response.put("email", profile.getEmail());
        response.put("firstName", profile.getFirstName());
        response.put("phone", profile.getPhone());
        response.put("dob", profile.getDob());
        response.put("gender", profile.getGender());
        response.put("position", profile.getPosition());
        response.put("job", profile.getJob());
        response.put("department", profile.getDepartment());
        response.put("avatar", profile.getAvatar());
        response.put("roles", Arrays.asList(-1));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> createProfile(@Valid @RequestBody ProfileDTO dto) {
        try {
            // Reusing account management service for HR creation logic
            String generatedPassword = accountManagementService.createEmployee(dto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", dto.getEmail());
            response.put("generatedPassword", generatedPassword);
            response.put("message", "Tài khoản đã được tạo thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody ProfileDTO dto) {
        String email = getCurrentUserEmail(); // Or read from JWT
        dto.setEmail(email); // Override with token email for security self-update
        
        // Ensure profile exists in DB to be updated
        boolean exists = userProfileService.getUserProfileByEmail(email).isPresent();
        if (!exists) {
            UserProfile newProfile = new UserProfile();
            newProfile.setEmail(email);
            newProfile.setPhone(dto.getPhone() != null ? dto.getPhone() : "0900000000"); 
            userProfileService.saveProfile(newProfile);
        }

        try {
            UserProfile updated = userProfileService.updateProfile(email, dto);
            
            Map<String, Object> data = new HashMap<>();
            data.put("email", updated.getEmail());
            data.put("firstName", updated.getFirstName());
            data.put("phone", updated.getPhone());
            data.put("dob", updated.getDob());
            data.put("gender", updated.getGender());
            data.put("position", updated.getPosition());
            data.put("job", updated.getJob());
            data.put("department", updated.getDepartment());
            data.put("avatar", updated.getAvatar());
            data.put("roles", Arrays.asList(-1));
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Cập nhật thông tin thành công");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
