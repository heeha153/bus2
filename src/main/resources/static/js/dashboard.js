// WebSocket 연결 설정
const socket = new WebSocket("wss://localhost:8081/ws/dashboard");

socket.onopen = () => {
    console.log("✅ WebSocket 연결 성공!");
    // 연결이 되면 서버로부터 데이터를 요청
    socket.send("getDashboardData");
};


socket.onmessage = function (event) {
    const response = JSON.parse(event.data);
    console.log("Received:", response);

    if (response.type === "redisStats") {
        const data = response.data;

        // 업데이트: 모든 데이터 표시
        document.getElementById("routesCount").innerText = data.routesCount || "-";
        document.getElementById("requestToday").innerText = data.requestToday || "-";
        document.getElementById("memoryUsage").innerText = data.usedMemory || "-";
        document.getElementById("connectedClients").innerText = data.connectedClients || "-";
    }
};

socket.onerror = (error) => {
    console.error("❌ WebSocket 오류:", error);
};

socket.onclose = () => {
    console.log("🔌 WebSocket 연결 종료");
};

// 대시보드 업데이트 함수
function updateDashboard(data) {
    if (data.routesCount !== undefined) {
        document.getElementById("routesCount").innerText = data.routesCount;
    }
    if (data.requestToday !== undefined) {
        document.getElementById("requestToday").innerText = data.requestToday;
    }
    if (data.memoryUsage !== undefined) {
        document.getElementById("memoryUsage").innerText = data.memoryUsage + " MB";
    }
    if (data.connectedClients !== undefined) {
        document.getElementById("connectedClients").innerText = data.connectedClients;
    }
}


// 응답 속도 차트 렌더링
// 1. 24시간 고정 라벨 생성
const fixedLabels = Array.from({ length: 24 }, (_, i) =>
    `${String(i).padStart(2, '0')}:00`
);

// 2. 응답 속도 데이터 불러오기 및 정렬
fetch('/api/admin/metrics/response-time/hourly')
    .then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                throw new Error(`서버 오류: ${res.status} - ${text}`);
            });
        }
        return res.json();
    })
    .then(data => {
        const dataMap = Object.fromEntries(data.map(d => [d.date, d.averageResponseTime]));

        // 3. 라벨과 매칭하여 null/값 배열 생성
        const alignedValues = fixedLabels.map(label => dataMap[label] ?? null);

        // 4. 그래프 그리기
        drawChart(fixedLabels, alignedValues);
    })
    .catch(err => {
        console.error("응답속도 차트 로딩 실패", err.message);
    });

// 5. Chart.js 그리기 함수 정의
function drawChart(labels, values) {
    const ctx = document.getElementById('apiResponseChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: '응답 속도 (ms)',
                data: values,
                fill: false,
                borderColor: 'rgba(75, 192, 192, 1)',
                tension: 0.3,
                spanGaps: false // ❗ null값 구간은 선 끊기게!
            }]
        },
        options: {
            responsive: false,
            plugins: {
                tooltip: {
                    callbacks: {
                        label: ctx => ctx.raw !== null ? `${ctx.raw.toFixed(2)} ms` : '데이터 없음'
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: { display: true, text: 'ms' }
                },
                x: {
                    title: { display: true, text: '시간 (hour)' }
                }
            }
        }
    });
}