package kroryi.bus2.dto.lost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LostItemResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String busNumber;
    private String busCompany;
    private LocalDateTime lostTime;
    private Long memberId; // User id만 사용
    private boolean matched;
    private boolean visible;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

