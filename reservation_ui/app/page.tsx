'use client'; // 클라이언트 사이드에서 작동하는 컴포넌트임을 선언

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';

export default function ReservationPage() {
  const router = useRouter();
  const [stock, setStock] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // 1. 초기 데이터 로드 및 로그인 상태 확인
  const fetchStock = async () => {
    try {
      // 1. 로컬 스토리지에서 토큰 가져오기
      const token = localStorage.getItem('accessToken');
      
      // 2. 헤더에 Authorization 추가
      const response = await fetch('http://localhost:8080/api/products/12', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          // 토큰이 있을 경우에만 Bearer 스키마와 함께 전송
          'Authorization': token ? `Bearer ${token}` : ''
        }
      });
      
      if (!response.ok) {
        // 만약 401(Unauthorized) 에러가 난다면 로그인 페이지로 보낼 수도 있습니다.
        if (response.status === 401) console.error("인증 실패: 로그인이 필요합니다.");
        throw new Error('네트워크 응답이 좋지 않습니다.');
      }

      const data = await response.json();
      if (data && data.stock) {
        setStock(data.stock.quantity);
      }
    } catch (error) {
      console.error("데이터 로드 실패:", error);
    }
  };

  useEffect(() => {
    fetchStock();
    // 로컬 스토리지에 토큰이 있는지 확인하여 로그인 상태 업데이트
    const token = localStorage.getItem('accessToken');
    setIsLoggedIn(!!token);
  }, []);

  // 2. 로그아웃 함수
  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('userId');
    setIsLoggedIn(false);
    alert('로그아웃 되었습니다.');
  };

  // 3. 예약 요청 함수
  const handleReserve = async () => {
    const token = localStorage.getItem('accessToken');
    const userId = localStorage.getItem('userId');

    // 로그인 체크
    if (!token || !userId) {
      alert('로그인이 필요한 서비스입니다.');
      router.push('/login');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const response = await fetch('http://localhost:8080/api/reservations', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` // ✅ JWT 토큰을 헤더에 담아 보냄
        },
        body: JSON.stringify({
          userId: Number(userId), // ✅ 저장된 유저 ID 사용
          productId: 12,           // 본인의 상품 ID
          amount: 1
        }),
      });

      if (response.ok) {
        setMessage('✅ 예약에 성공했습니다!');
        await fetchStock(); // 성공 시 재고 다시 불러오기
      } else if (response.status === 401 || response.status === 403) {
        setMessage('❌ 인증이 만료되었습니다. 다시 로그인해주세요.');
        handleLogout();
      } else {
        setMessage('❌ 예약 실패 (재고 부족 또는 서버 오류)');
      }
    } catch (error) {
      setMessage('⚠️ 서버 통신 에러');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen flex flex-col items-center justify-center bg-gray-50 p-6">
      {/* 상단 로그인/로그아웃 버튼 */}
      <div className="absolute top-6 right-6">
        {isLoggedIn ? (
          <button onClick={handleLogout} className="text-sm text-gray-500 hover:underline">로그아웃</button>
        ) : (
          <button onClick={() => router.push('/login')} className="text-sm text-blue-600 font-bold hover:underline">로그인</button>
        )}
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md text-center">
        <h1 className="text-3xl font-bold mb-6 text-gray-800">🍪 두쫀쿠 선착순 판매</h1>
        
        <div className="bg-blue-50 p-6 rounded-xl mb-6">
          <p className="text-gray-600 mb-2">남은 재고</p>
          <p className="text-5xl font-black text-blue-600">
            {stock !== null ? `${stock}개` : '불러오는 중...'}
          </p>
        </div>

        <button
          onClick={handleReserve}
          disabled={loading || stock === 0}
          className={`w-full py-4 rounded-xl text-white font-bold text-lg transition-all ${
            loading || stock === 0 
              ? 'bg-gray-400 cursor-not-allowed' 
              : 'bg-black hover:bg-gray-800 active:scale-95'
          }`}
        >
          {loading ? '처리 중...' : stock === 0 ? '품절' : '지금 예약하기'}
        </button>

        {message && (
          <p className={`mt-4 font-medium ${message.includes('✅') ? 'text-green-600' : 'text-red-600'}`}>
            {message}
          </p>
        )}
      </div>
      
      <p className="mt-8 text-gray-400 text-sm">
        Next.js + Spring Boot (Pessimistic Lock Test)
      </p>
    </main>
  );
}