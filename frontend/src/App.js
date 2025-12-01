import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import {Login} from "./pages/Login";
import {Register} from "./pages/Register.js";
import Menu from "./pages/Menu";
import { SelectFeel } from "./pages/SelectFeel";
import CategoryPage from "./pages/CategoryPage.jsx";
import FoodList from "./pages/FoodList.jsx";
import {ReviewProfile} from "./pages/ReviewProfile.js";
import ReviewRegister from "./pages/ReviewRegister.js";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/menu" element={<Menu />} />
        <Route path="/selectFeel" element={<SelectFeel />} />
       <Route path="/category/:foodType" element={<CategoryPage />} />
        <Route path="/foodlist" element={<FoodList />} />
        <Route path="/review-profile" element={<ReviewProfile />} />
        <Route path="/review-register" element={<ReviewRegister />} />
      </Routes>
    </Router>
  );
}

export default App;
