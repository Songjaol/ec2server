// LogoutButton.js

import { useNavigate } from 'react-router-dom';

function LogoutButton() {
    const navigate = useNavigate();

    const handleLogout = () => {
        // 1. localStorage에서 토큰을 제거합니다.
        localStorage.removeItem("token");
        
        // 2. (선택) 사용자에게 알림
        alert("로그아웃되었습니다.");
        
        // 3. 로그인 페이지로 리디렉션
        navigate("/login"); 
    };

    return <button onClick={handleLogout}>로그아웃</button>;
}