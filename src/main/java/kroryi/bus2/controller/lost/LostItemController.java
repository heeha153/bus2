package kroryi.bus2.controller.lost;


import kroryi.bus2.dto.lost.LostItemAdminResponseDTO;
import kroryi.bus2.dto.lost.LostItemListResponseDTO;
import kroryi.bus2.dto.lost.LostItemRequestDTO;
import kroryi.bus2.dto.lost.LostItemResponseDTO;
import kroryi.bus2.entity.lost.LostItem;
import kroryi.bus2.service.lost.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost")
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;

    @PostMapping
    public ResponseEntity<LostItem> reportLostItem(@RequestBody LostItemRequestDTO dto) {
        LostItem saved = lostItemService.saveLostItem(dto);
        return ResponseEntity.ok(saved);
    }

    // 🔸 일반 회원용 (숨겨지지 않은 것만 조회)
    @GetMapping("/visible")
    public ResponseEntity<List<LostItemListResponseDTO>> getVisibleLostItems() {
        List<LostItemListResponseDTO> result = lostItemService.getAllLostItemsVisibleOnly()
                .stream()
                .map(item -> LostItemListResponseDTO.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .busNumber(item.getBusNumber())
                        .lostTime(item.getLostTime())
                        .matched(item.isMatched())
                        .build())
                .toList();
        return ResponseEntity.ok(result);
    }

    // 🔸 관리자용 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<LostItemListResponseDTO>> getAllLostItemsIncludingHidden() {
        List<LostItemListResponseDTO> result = lostItemService.getAllLostItemsIncludingHidden()
                .stream()
                .map(item -> LostItemListResponseDTO.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .busNumber(item.getBusNumber())
                        .lostTime(item.getLostTime())
                        .matched(item.isMatched())
                        .build())
                .toList();
        return ResponseEntity.ok(result);
    }
    // 🔸 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<LostItemResponseDTO> getLostItemById(@PathVariable Long id) {
        LostItemResponseDTO dto = lostItemService.getLostItemById(id);
        return ResponseEntity.ok(dto);
    }
    @GetMapping
    public ResponseEntity<List<LostItemListResponseDTO>> getAllLostItems() {
        List<LostItemListResponseDTO> results = lostItemService.getAllLostItems();
        return ResponseEntity.ok(results);
    }
}

