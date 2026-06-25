import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

//新增：导入 Element Plus 样式 + 组件
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

//新增：导入所有图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

//新增：注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}


app.use(createPinia())
app.use(router)

//新增：使用 Element Plus
app.use(ElementPlus)
app.mount('#app')
