import axios from "axios";

const API_URL = "http://localhost:8088/api/auth/";

const register = (username, email, password) => {
  return axios.post(API_URL + "signup", {
    username,
    email,
    password,
  });
};

const login = (username, password) => {
  return axios
    .post(API_URL + "signin", {
      username,
      password,
    })
    .then((response) => {
      if (response.data.username) {
        localStorage.setItem("user", JSON.stringify(response.data));
      }
      return response.data;
    });
    
};

const logout = () => {
  const jsonObject = JSON.parse(localStorage.getItem("user"));
  localStorage.removeItem("user");

  return axios.post(API_URL + 'signout', {}, {
    headers: {
      'Authorization': `Bearer ${jsonObject.token}`
    }
  }).then((response) => {
    return response.data;
  });
};

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem("user"));
};

const AuthService = {
  register,
  login,
  logout,
  getCurrentUser,
}

export default AuthService;
