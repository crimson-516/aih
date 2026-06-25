<template>
    <div class="login-page">
        <div class="login-box">
            <div class="avatar-wrap">
                <img src="/src/img/avatar.png" alt="头像" class="avatar" />
            </div>

            <h1 class="title">{{ isRegister ? '小豆包ai助手' : '小豆包ai助手' }}</h1>
            <p class="subtitle">{{ isRegister ? '注册' : '登录' }}</p>

            <div class="divider">
                <span class="line"></span>
                <span class="heart">♡</span>
                <span class="line"></span>
            </div>

            <div class="input-group">
                <label class="label">昵称</label>
                <div class="input-wrap">
                    <el-input v-model="username" placeholder="请输入..." maxlength="10" />
                    <span class="count">{{ username.length }}/10</span>
                </div>
            </div>

            <div class="input-group">
                <label class="label">密码</label>
                <div class="input-wrap">
                    <el-input v-model="password" type="password" placeholder="输入密码" maxlength="11" />
                    <el-icon class="eye-icon">
                        <View />
                    </el-icon>
                    <span class="count">{{ password.length }}/11</span>
                </div>
            </div>

            <div class="switch-text" @click="isRegister = !isRegister">
                {{ isRegister ? '已有账户？去登录' : '还没账号？去注册' }}
            </div>

            <el-button class="submit-btn" @click="submit">
                {{ isRegister ? '注册' : '登录' }}
            </el-button>

            <div class="dots">
                <span class="dot"></span>
                <span class="dot active"></span>
                <span class="dot"></span>
            </div>
        </div>

        <div v-if="showSuccess" class="modal-mask">
            <div class="modal-box">
                <div class="modal-icon">
                    <Star />
                </div>
                <h2>{{ isRegister ? '注册成功' : '登录成功' }}</h2>
                <el-button class="modal-btn" @click="toMain">
                    {{ isRegister ? '去登录' : '开始聊天~' }}
                </el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElInput, ElButton, ElIcon } from 'element-plus'
import { View, Star } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const isRegister = ref(false)
const username = ref('')
const password = ref('')
const showSuccess = ref(false)

// 登录 + 注册 提交
const submit = async () => {
    if (!username.value || !password.value) {
        alert('请输入完整')
        return
    }

    try {
        let res;
        if (isRegister.value) {
            // 注册
            res = await request.post('/ai/register', {
                userName: username.value,
                passWord: password.value
            })

            if (res.data && res.data.code !== 200) {
                alert('注册失败：' + (res.data.info || '未知错误'))
                return
            }

            alert('注册成功！请登录')
            isRegister.value = false
            return
        } else {
            // 登录
            res = await request.post('/ai/login', {
                userName: username.value,
                passWord: password.value
            })
        }

        if (res.data && res.data.code !== 200) {
            alert('登录失败：' + (res.data.info || '用户名或密码错误'))
            return
        }

        //和后端接口对应
        const userVO = res.data.data;

        if (!userVO || !userVO.token) {
            alert('登录异常：未获取到有效凭证')
            return
        }

        localStorage.setItem('token', userVO.token)
        localStorage.setItem('userId', userVO.userId)
        localStorage.setItem('userName', userVO.userName)

        showSuccess.value = true

    } catch (e) {
        console.error('错误：', e)
        const msg = e.response?.data?.info || e.message || '请求失败'
        alert('请求错误：' + msg)
    }
}

const toMain = () => {
    showSuccess.value = false
    router.push('/main')
}
</script>

<style scoped>
.login-page {
    width: 100vw;
    height: 100vh;
    background: #fff5f9;
    display: flex;
    align-items: center;
    justify-content: center;
}

.login-box {
    width: 90%;
    max-width: 360px;
    background: #fff;
    border-radius: 24px;
    padding: 40px 30px;
    text-align: center;
}

.avatar-wrap {
    margin-bottom: 16px;
}

.avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 3px solid #e8c5e5;
}

.title {
    font-size: 32px;
    margin: 0 0 8px;
}

.subtitle {
    color: #b479c9;
    font-size: 18px;
    margin: 0 0 24px;
}

.divider {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 30px;
}

.line {
    flex: 1;
    height: 1px;
    background: #e8c5e5;
}

.heart {
    color: #e8c5e5;
    font-size: 18px;
}

.input-group {
    text-align: left;
    margin-bottom: 20px;
}

.label {
    display: block;
    color: #666;
    margin-bottom: 8px;
    font-size: 16px;
}

.input-wrap {
    position: relative;
}

:deep(.el-input__wrapper) {
    border-radius: 12px;
    border: 1px solid #e0d4e9;
}

.count {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    color: #aaa;
    font-size: 14px;
}

.eye-icon {
    position: absolute;
    right: 45px;
    top: 50%;
    transform: translateY(-50%);
    color: #aaa;
    font-size: 18px;
}

.switch-text {
    text-align: left;
    font-size: 14px;
    color: #b479c9;
    cursor: pointer;
    margin-bottom: 16px;
    text-decoration: underline;
}

.submit-btn {
    width: 100%;
    height: 56px;
    border-radius: 28px;
    background: linear-gradient(90deg, #ffb7c5 0%, #d8a8e6 100%);
    border: none;
    color: white;
    font-size: 20px;
    font-weight: bold;
}

.dots {
    display: flex;
    justify-content: center;
    gap: 8px;
    margin-top: 30px;
}

.dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #e8e8e8;
}

.dot.active {
    background: #ffb7c5;
}

.modal-mask {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 999;
}

.modal-box {
    background: #fff;
    width: 300px;
    padding: 36px;
    border-radius: 20px;
    text-align: center;
}

.modal-icon {
    width: 70px;
    height: 70px;
    background: linear-gradient(180deg, #ffe8f2 0%, #f8e0f5 100%);
    border-radius: 50%;
    margin: 0 auto 20px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.modal-icon .el-icon {
    font-size: 30px;
    color: #b479c9;
}

.modal-btn {
    width: 100%;
    height: 50px;
    border-radius: 25px;
    background: linear-gradient(90deg, #ff6ba3 0%, #b44edb 100%);
    border: none;
    color: white;
    font-size: 18px;
}
</style>