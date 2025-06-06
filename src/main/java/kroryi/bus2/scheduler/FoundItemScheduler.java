package kroryi.bus2.scheduler;

import kroryi.bus2.entity.lost.FoundItem;
import kroryi.bus2.repository.jpa.FoundItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FoundItemScheduler {

    private final FoundItemRepository foundItemRepository;

    // 매일 새벽 1시 실행
    @Scheduled(cron = "0 0 1 * * *")
//    @Scheduled(fixedDelay = 10000) // 10초마다 실행 (테스트용)
    public void hideOldFoundItems() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        // createdAt 기준으로 7일 경과된 게시글 조회
        List<FoundItem> oldItems = foundItemRepository.findByVisibleTrueAndCreatedAtBefore(cutoff);

        if (oldItems.isEmpty()) {
            log.info("숨길 습득물 없음");
            return;
        }

        oldItems.forEach(item -> item.setVisible(false));
        foundItemRepository.saveAll(oldItems);

        log.info("{}개의 습득물을 숨김 처리했습니다.", oldItems.size());
    }
}
