'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const router = useRouter();
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    const res = await fetch('http://localhost:8080/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ loginId, password }),
    });

    if (res.ok) {
      const data = await res.json();
      // 💡 핵심: 서버가 준 JWT 토큰을 로컬 스토리지에 저장!
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('userId', data.userId);
      
      alert('로그인 성공!');
      router.push('/'); // 메인 주문 페이지로 이동
    } else {
      alert('아이디 또는 비밀번호가 틀렸습니다.');
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <form onSubmit={handleLogin} className="w-full max-w-md space-y-4 bg-white p-8 shadow-lg rounded-2xl">
        <h2 className="text-2xl font-bold text-center">로그인</h2>
        <input className="w-full p-3 border rounded" placeholder="아이디" onChange={e => setLoginId(e.target.value)} required />
        <input className="w-full p-3 border rounded" type="password" placeholder="비밀번호" onChange={e => setPassword(e.target.value)} required />
        <button className="w-full bg-black text-white p-3 rounded font-bold hover:bg-gray-800">로그인</button>
        <p className="text-center text-sm text-gray-500 cursor-pointer" onClick={() => router.push('/signup')}>계정이 없으신가요? 회원가입</p>
      </form>
    </div>
  );
}