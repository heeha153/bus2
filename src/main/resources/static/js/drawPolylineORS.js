// let currentPolylines = [];
//
// function drawBusRouteMapORS(data) {
//     const map = window.kakaoMap;
//     const mapContainer = document.getElementById('map');
//
//     if (!mapContainer) {
//         console.error("🛑 map 요소를 찾을 수 없습니다.");
//         return;
//     }
//
//     if (!data || (!Array.isArray(data.forward) && !Array.isArray(data.reverse))) {
//         console.warn("⚠️ 경로 데이터가 비어있거나 잘못된 형식입니다:", data);
//         return;
//     }
//
//     // 🔥 기존 폴리라인 제거
//     currentPolylines.forEach(poly => poly.setMap(null));
//     currentPolylines = [];
//
//     const bounds = new kakao.maps.LatLngBounds();
//
//     // ✅ 내부 함수: 하나의 방향을 그리는 함수
//     function drawPathSection(sectionData, strokeColor) {
//         if (!Array.isArray(sectionData) || sectionData.length < 2) return;
//
//         for (let i = 0; i < sectionData.length - 1; i++) {
//             const start = new kakao.maps.LatLng(sectionData[i].ypos, sectionData[i].xpos);
//             const end = new kakao.maps.LatLng(sectionData[i + 1].ypos, sectionData[i + 1].xpos);
//
//             bounds.extend(start);
//             bounds.extend(end);
//
//             const polyline = new kakao.maps.Polyline({
//                 path: [start, end],
//                 strokeWeight: 3,
//                 strokeColor: strokeColor,
//                 strokeOpacity: 0.9,
//                 strokeStyle: 'solid'
//             });
//
//             polyline.setMap(map);
//             currentPolylines.push(polyline);
//         }
//     }
//
//     // 🔵 정방향만 보기
//     // drawPathSection(data.forward, '#007bff');
//
//     // 🔴 역방향만 보기
//     // drawPathSection(data.reverse, '#FF0000');
//
//     // ✅ 둘 다 보기 (기본값)
//     drawPathSection(data.forward, '#007bff');   // 파란색
//     drawPathSection(data.reverse, '#FF0000');   // 빨간색
//
//
//     // ✅ 전체 범위에 맞춰 지도 조정
//     map.setBounds(bounds);
// }

let currentPolylines = [];

function drawBusRouteMapORS(data) {
    const map = window.leafletMap;
    const mapContainer = document.getElementById('map');

    if (!mapContainer) {
        console.error("🛑 map 요소를 찾을 수 없습니다.");
        return;
    }

    if (!data || (!Array.isArray(data.forward) && !Array.isArray(data.reverse))) {
        console.warn("⚠️ 경로 데이터가 비어있거나 잘못된 형식입니다:", data);
        return;
    }

    // 🔥 기존 폴리라인 제거
    currentPolylines.forEach(poly => map.removeLayer(poly));
    currentPolylines = [];

    const bounds = L.latLngBounds();

    // ✅ 내부 함수: 하나의 방향을 그리는 함수
    function drawPathSection(sectionData, strokeColor) {
        if (!Array.isArray(sectionData) || sectionData.length < 2) return;

        const latlngs = [];

        for (let i = 0; i < sectionData.length; i++) {
            const lat = parseFloat(sectionData[i].ypos);
            const lng = parseFloat(sectionData[i].xpos);
            if (isFinite(lat) && isFinite(lng)) {
                const point = L.latLng(lat, lng);
                latlngs.push(point);
                bounds.extend(point);
            }
        }

        const polyline = L.polyline(latlngs, {
            color: strokeColor,
            weight: 3,
            opacity: 0.9
        }).addTo(map);

        currentPolylines.push(polyline);
    }

    // 🔵 정방향
    drawPathSection(data.forward, '#007bff');

    // 🔴 역방향
    drawPathSection(data.reverse, '#FF0000');

    // ✅ 전체 범위에 맞춰 지도 조정
    map.fitBounds(bounds);
}


