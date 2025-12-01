import axios from "axios";

// axios 인스턴스 생성
export const api = axios.create({
  baseURL: "http://15.164.175.224:8081",
});

// 기존 restaurant API
export const getRestaurants = async (region) => {
  try {
    const response = await api.get(`/restaurants`, {
      params: { region }
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching restaurants:", error);
    return [];
  }
};
