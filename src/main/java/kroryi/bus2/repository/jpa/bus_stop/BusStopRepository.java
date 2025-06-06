package kroryi.bus2.repository.jpa.bus_stop;

import kroryi.bus2.dto.busStop.BusStopDTO;
import kroryi.bus2.entity.busStop.BusStop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop,Long> {

    @Query("SELECT b FROM BusStop b")
    List<BusStop> findBusStops();



    List<BusStop> findByBsNmContaining(String bsNm);

    @Query("SELECT b FROM BusStop b WHERE b.bsNm LIKE %:bsNm% OR REPLACE(b.bsNm, ' ', '') LIKE %:bsNm%")
    List<BusStop> searchByBsNmIgnoreSpace(@Param("bsNm") String bsNm);

    Optional<BusStop> findByBsId(String bsId);

    boolean existsByBsId(String bsId);

    @Query("SELECT new kroryi.bus2.dto.busStop.BusStopDTO(b.bsId, b.bsNm, b.xPos, b.yPos) " +
            "FROM BusStop b " +
            "WHERE b.xPos BETWEEN :minX AND :maxX " +
            "AND b.yPos BETWEEN :minY AND :maxY")
    List<BusStopDTO> findInBounds(@Param("minX") double minX,
                                  @Param("maxX") double maxX,
                                  @Param("minY") double minY,
                                  @Param("maxY") double maxY);

    @Query("SELECT COUNT(r) FROM RouteStopLink r WHERE r.bsId = :bsId")
    int countByBsId(@Param("bsId") String bsId);

    void deleteByBsId(String bsId);

    @Query("SELECT b FROM BusStop b " +
            "WHERE LOWER(b.bsId) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.bsNm) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<BusStop> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("""
SELECT distinct bs.bsId FROM BusStop bs
WHERE 
ABS(bs.xPos - :x) * 111320 * COS(RADIANS(:y)) <= :radius
AND 
ABS(bs.yPos - :y) * 110540 <= :radius
""")
    List<String> findNearbyStationIdsWithGeo(
            @Param("x") Double x,
            @Param("y") Double y,
            @Param("radius") Double radius);


}
