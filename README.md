# HoYoverse Notice

호요버스 게임(원신, 붕괴3rd, 붕괴 스타레일)의 일일보상 수령 여부, 레진 잔량 등 계정 상태를 한 화면에서 확인하기 위한 개인 대시보드.

## 구조

```
hoyoverse_notice/
├── backend/    # Spring Boot — HoYoLAB API 호출 및 REST 제공
└── frontend/   # React — 대시보드 UI
```

## 기술 스택

- **Backend**: Java + Spring Boot
- **Frontend**: React
- **Deploy**: GitHub Pages (FE) + 별도 호스팅 (BE)
