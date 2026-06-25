import { createRouter, createWebHistory } from 'vue-router'
import LoginAndRegister from '../views/LoginAndRegister.vue'
import Main from '../views/Main.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: LoginAndRegister },
    { path: '/main', component: Main }
  ]
})

export default router