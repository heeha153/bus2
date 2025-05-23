package kroryi.bus2.repository.jpa;

import kroryi.bus2.entity.route.RouteStopLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddRouteStopLinkRepository extends JpaRepository<RouteStopLink, Long> {
    List<RouteStopLink> findByRouteId(String routeId);

    void deleteByRouteId(String routeId);

}
