package kroryi.bus2.controller.bus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kroryi.bus2.dto.Route.RouteIdAndNoDTO;
import kroryi.bus2.dto.busStop.*;

import kroryi.bus2.service.busStop.AddBusStopService;
import kroryi.bus2.service.busStop.BusStopDataService;
import kroryi.bus2.service.busStop.DeleteBusStopService;
import kroryi.bus2.service.busStop.UpdateBusStopService;
import kroryi.bus2.dto.busStop.BusStopListDTO;
import kroryi.bus2.dto.busStop.BusStopUpdateDTO;
import kroryi.bus2.entity.busStop.BusStop;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "버스-정류장-정보", description = "")
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
@Log4j2
public class BusStopDataController {

    private final AddBusStopService addBusStopService;
    private final UpdateBusStopService updateBusStopService;
    private final BusStopDataService busStopDataService;
    private final DeleteBusStopService deleteBusStopService;

    // 페이징과 검색이 적용된 전체 정류장 리스트 컨트롤러
    @Operation(summary = "전체 정류장 리스트", description = "페이징과 검색이 적용된 전체 정류장 리스트 컨트롤러")
    @GetMapping("/AllBusStop")
    public ResponseEntity<Page<BusStopListDTO>> getBusStops(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BusStopListDTO> result = busStopDataService.getBusStopsWithPaging(keyword, page, size);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "정류장 추가", description = "새로운 정류장(BusStop)을 추가합니다. 좌표(xpos, ypos), 정류장 ID(bsId), 이름(bsNm)를 포함합니다.")
    @PostMapping("/addBusStop")
    public ResponseEntity<BusStop> addStop(@RequestBody BusStopDetailResponseDTO dto) {
        BusStop created = addBusStopService.createBusStop(dto);
        return ResponseEntity.ok(created);
    }

    // 정류장 ID로 조회
    @Operation(summary = "정류장 상세 정보 + 위치,주소 정보 + 정류장에 오는 노선 조회", description = "정류장 ID(bsId)를 기준으로 상세 정보 및 위치 정보(시/구/동 포함) + 정류장에 오는 노선을 조회합니다.")
    @GetMapping("/busStop")
    public ResponseEntity<BusStopFullDetailDTO> getFullDetail(@RequestParam String bsId) {
        BusStopFullDetailDTO detail = busStopDataService.getFullBusStopDetail(bsId);
        return ResponseEntity.ok(detail);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "정류장 정보 수정", description = "정류장 ID(bsId)에 해당하는 정류장의 이름, 좌표 등을 수정합니다.")
    @PutMapping("/updateStop/{bsId}")
    public ResponseEntity<String> updateBusStop(
            @PathVariable String bsId,
            @RequestBody BusStopUpdateDTO dto) {
        updateBusStopService.updateBusStop(bsId, dto);
        return ResponseEntity.ok("정류장 정보가 수정되었습니다.");
    }

    @Operation(summary = "정류장 검색", description = "사용자가 검색창에 키워드를 입력했을 때, 해당 키워드에 해당하는 정류장명 반환")
    @GetMapping(value = "/searchBS", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBusNames(@RequestParam String keyword) {
        List<BusStop> busStop = busStopDataService.getBusStopsByNm(keyword);
        return ResponseEntity.ok(busStop);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "정류장 삭제", description = "정류장 ID(bsId)로 해당 정류장을 삭제합니다. (만약 해당 정류장이 노선에 이어져 있는 경우 삭제는 불가합니다.)")
    @DeleteMapping("/deleteBusStop")
    public ResponseEntity<?> deleteBusStop(@RequestParam String bsId) {
        try {
            deleteBusStopService.backupBusStop(bsId);
            deleteBusStopService.deleteBusStopIfNotLinked(bsId);
            return ResponseEntity.ok(Map.of("success", true, "message", "정류장이 삭제되었습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Operation(summary = "정류장에 오는 노선 조회", description = "정류장 ID(bsId)로 해당 정류장에 오는 노선을 조회합니다.")
    @GetMapping("/RouteByBS")
    public ResponseEntity<List<RouteIdAndNoDTO>> getRoutesForBusStop(@RequestParam String bsId) {
        List<RouteIdAndNoDTO> routes = busStopDataService.getRoutesByBusStop(bsId);
        System.out.println("routes: " + routes);
        return ResponseEntity.ok(routes);
    }

}
