import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10 * 60 * 1000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      // 只放纯 token，不加任何前缀！！！
      config.headers.Authorization = token.trim()
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default request