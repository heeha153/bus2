package kroryi.bus2.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kroryi.bus2.dto.user.UserListResponseDTO;
import kroryi.bus2.dto.user.UserRoleChangeResponseDTO;
import kroryi.bus2.entity.apikey.ApiKey;
import kroryi.bus2.entity.user.Role;
import kroryi.bus2.entity.user.User;
import kroryi.bus2.service.admin.AdminUserService;
import kroryi.bus2.service.apikey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자-관리", description = "관리자 전용 사용자 관리 API")
@Controller
@RequestMapping
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final ApiKeyService apiKeyService;

    @ResponseBody
    @Operation(summary = "사용자 목록 조회", description = "전체 사용자 목록 또는 키워드로 검색된 사용자 목록을 반환합니다.")
    @GetMapping("/api/management/user")
    public List<UserListResponseDTO> userList(@RequestParam(required = false) String keyword) {
        List<User> users = (keyword == null || keyword.isBlank())
                ? adminUserService.getAllUsers()
                : adminUserService.searchUsers(keyword);

        return users.stream()
                .map(user -> new UserListResponseDTO(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();
    }

    @ResponseBody
    @PostMapping("/api/management/user/{userId}/role")
    public UserRoleChangeResponseDTO changeUserRole(@PathVariable String userId,
                                                    @RequestParam Role role) {
        adminUserService.changeUserRole(userId, role);
        return new UserRoleChangeResponseDTO(
                userId,
                role,
                userId + "의 권한이 " + role + "(으)로 변경되었습니다."
        );
    }

    @GetMapping("/admin/apikey")
    public String showApiKeyDashboard(Model model) {
        model.addAttribute("recentKeys", apiKeyService.getAllApiKeys());
        return "api/apiKeyDashboard"; // templates/api/apiKeyDashboard.html
    }

    @PostMapping("/admin/apikey/{id}/toggle")
    public String toggleApiKey(@PathVariable Long id) {
        apiKeyService.toggleActiveStatus(id); // 활성/비활성 토글 메서드 호출
        return "redirect:/admin/apikey"; // 다시 대시보드로 리다이렉트
    }
}
