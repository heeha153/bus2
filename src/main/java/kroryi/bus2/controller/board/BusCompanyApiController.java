package kroryi.bus2.controller.board;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kroryi.bus2.dto.board.BusCompanyDTO;
import kroryi.bus2.entity.BusCompany;
import kroryi.bus2.service.board.BusCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "버스-회사-정보", description = "")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class BusCompanyApiController {
    private final BusCompanyService busCompanyService;

    @GetMapping
    @Operation(summary = "버스 회사 정보 조회", description = "버스 회사 정보를 조회합니다")
    public List<BusCompany> getFares(@RequestParam(required = false) String name) {
        if (name == null) {
            return busCompanyService.findAll();
        } else {
            return busCompanyService.findByName(name);
        }
    }

    @PostMapping
    @Operation(summary = "버사 회사 정보 등록", description = "버스 회사 정보를 등록합니다")
    public ResponseEntity<BusCompany> createFare(@RequestBody BusCompanyDTO dto) {
        return ResponseEntity.ok(busCompanyService.save(dto));
    }

    @PatchMapping("/{id}/routes")
    @Operation(summary = "노선 추가", description = "해당 회사에 새로운 노선을 추가합니다.")
    public ResponseEntity<BusCompany> addRoutesToCompany(
            @PathVariable int id,
            @RequestBody List<String> newRoutes
    ) {
        return ResponseEntity.ok(busCompanyService.addRoutes(id, newRoutes));
    }

    @PutMapping("/{id}")
    @Operation(summary = "버스 회사 정보 수정", description = "버스 회사 정보를 수정합니다")
    public ResponseEntity<BusCompany> updateFare(@PathVariable int id, @RequestBody BusCompanyDTO dto) {
        return ResponseEntity.ok(busCompanyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @Operation(summary = "버스 회사 정보 삭제", description = "버스 회사 정보를 삭제합니다")
    public ResponseEntity<String> deleteFare(@PathVariable int id) {
        busCompanyService.deleteById(id);
        return ResponseEntity.ok("삭제 완료");
    }
}
