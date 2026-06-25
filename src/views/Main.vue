<template>
    <div class="chat-page">
        <div class="chat-header">
            <button class="menu-btn" @click="toggleSidebar">
                <el-icon>
                    <Grid />
                </el-icon>
            </button>
            <img src="/src/img/avatar.png" class="header-avatar" />
            <span class="title">
                小豆包AI助手<br />
                <span style="font-size:12px; color:pink;">在线 。</span>
            </span>
            <el-button link class="logout-btn" @click="logout">退出</el-button>
        </div>

        <div class="chat-main">
            <div class="sidebar" :class="{ open: sidebarOpen }">
                <div class="sidebar-title">
                    对话记忆
                    <el-button type="primary" size="small" text @click="createNewSession"
                        style="margin-left:8px;color:#fff;background:#e8a1c1">
                        <el-icon>
                            <Plus />
                        </el-icon>
                    </el-button>
                </div>

                <div class="history-list">
                    <div v-for="item in historyList" :key="item.id" class="history-item"
                        :class="{ active: activeId === item.id }" @click="switchSession(item.id)">
                        <span>{{ item.title }}</span>
                        <el-icon class="delete-icon" @click.stop="handleDeleteClick(item)">
                            <Delete />
                        </el-icon>
                    </div>
                </div>
            </div>

            <div class="chat-area">
                <div class="message-list" ref="msgList">
                    <div v-for="(msg, idx) in messages" :key="idx" class="message-wrapper">
                        <div v-if="msg.isAi === 1" class="ai-message">
                            <img src="/src/img/avatar.png" class="msg-avatar" />
                            <div class="bubble ai-bubble">
                                <span v-if="msg.messageType === 'text'">
                                    <span v-if="msg.isHtml" v-html="msg.content"></span>
                                    <span v-else>{{ msg.content }}</span>
                                </span>
                                <img v-else-if="msg.messageType === 'image'" :src="msg.content"
                                    style="max-width:200px; border-radius:8px;">
                                <a v-else-if="msg.messageType === 'file'" :href="msg.content" target="_blank">📎
                                    点击查看文件</a>
                                <video v-else-if="msg.messageType === 'video'" :src="msg.content" controls
                                    style="width:250px; border-radius:8px;"></video>
                            </div>
                        </div>
                        <div v-else class="user-message">
                            <div class="bubble user-bubble">
                                <span v-if="msg.messageType === 'text'">{{ msg.content }}</span>
                                <img v-else-if="msg.messageType === 'image'" :src="msg.content"
                                    style="max-width:200px; border-radius:8px;">
                                <a v-else-if="msg.messageType === 'file'" :href="msg.content" target="_blank">📎
                                    发送文件</a>
                                <video v-else-if="msg.messageType === 'video'" :src="msg.content" controls
                                    style="width:250px; border-radius:8px;"></video>
                            </div>
                            <img src="/src/img/user.png" class="msg-avatar" />
                        </div>
                    </div>
                </div>

                <div class="input-bar">
                    <div class="attach-icon"><el-icon>
                            <Paperclip />
                        </el-icon></div>
                    <el-input v-model="inputText" placeholder="给小豆包发消息~ ❤️" @keyup.enter="sendMessage"
                        class="chat-input" />
                    <el-button type="primary" circle @click="sendMessage" class="send-btn">
                        <el-icon>
                            <Promotion />
                        </el-icon>
                    </el-button>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElInput, ElButton, ElIcon } from 'element-plus'
import { Grid, Plus, Delete, Paperclip, Promotion } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const sidebarOpen = ref(false)
const inputText = ref('')
const msgList = ref(null)

const userId = ref(localStorage.getItem('userId') || '')
const historyList = ref([])
const activeId = ref(null)
const messages = ref([])

// 链接转a标签工具
function linkify(text) {
    const urlPattern = /(https?:\/\/[^\s]+)/g
    return text.replace(urlPattern, (url) => {
        return `<a href="${url}" target="_blank" rel="noopener noreferrer" style="color: #42b983; text-decoration: underline;">${url}</a>`
    })
}

// ====================== 删除会话 ======================
const handleDeleteClick = (item) => {
    console.log('===== 删除点击 =====')
    console.log('完整会话item:', item)
    console.log('真实ID:', item.id)
    deleteSession(item.id)
}

// ====================== 加载会话列表 ======================
const loadUserConversations = async (autoSwitch = true) => {
    try {
        const res = await request.get('/ai/conversations')
        historyList.value = res.data.data || []

        if (historyList.value.length && autoSwitch && !activeId.value) {
            activeId.value = historyList.value[0].id
            loadSessionMessages(activeId.value)
        }
    } catch (e) { console.error('加载会话失败', e) }
}

// ====================== 加载消息 ======================
const loadSessionMessages = async (conversationId) => {
    if (!conversationId) return
    try {
        const res = await request.get('/ai/messages', { params: { conversationId } })
        messages.value = res.data.data || []

        if (messages.value.length === 0) {
            messages.value.push({
                isAi: 1,
                content: '我是小豆包ai助手，有什么可以帮你的嘛？❤️',
                messageType: 'text'
            })
        }

        nextTick(() => {
            if (msgList.value) msgList.value.scrollTop = msgList.value.scrollHeight
        })
    } catch (e) {
        messages.value = []
    }
}

let ws = null
onMounted(() => {
    if (!userId.value) { router.push('/'); return }

    const token = localStorage.getItem('token') || ''
    ws = new WebSocket(`ws://localhost:8080/ws/chat?token=${encodeURIComponent(token)}`)

    ws.onopen = () => {
        console.log('ws connected')
        ws.send('hello websocket')
    }

    ws.onmessage = (event) => {
        console.log('ws message:', event.data)
    }

    ws.onerror = (err) => {
        console.log('ws error', err)
    }

    ws.onclose = () => {
        console.log('ws closed')
    }
    loadUserConversations(true)
})

onUnmounted(() => {
    ws?.close()
})

const toggleSidebar = () => { sidebarOpen.value = !sidebarOpen.value }

// ====================== 新建会话 ======================
const createNewSession = async () => {
    try {
        const res = await request.post('/ai/conversation')
        console.log('新建会话后端返回的完整数据:', res.data)

        if (res.data && res.data.data) {
            const newConversationId = res.data.data.conversationId || res.data.data.id

            if (!newConversationId) {
                console.error('未从后端返回中提取到有效的会话ID')
                return
            }

            const newSessionItem = {
                id: Number(newConversationId),
                title: res.data.data.title || '新的聊天'
            }

            historyList.value.unshift(newSessionItem)
            activeId.value = newSessionItem.id

            messages.value = [
                {
                    isAi: 1,
                    content: '我是小豆包ai助手，有什么可以帮你的嘛？❤️',
                    messageType: 'text'
                }
            ]

            loadUserConversations(false)
        }
        sidebarOpen.value = false
    } catch (e) {
        console.error('新建会话失败', e)
    }
}

// ====================== 切换会话 ======================
const switchSession = (id) => {
    activeId.value = id
    loadSessionMessages(id)
    sidebarOpen.value = false
}

// ====================== 删除会话 ======================
const deleteSession = async (id) => {
    const conversationId = Number(id)
    console.log('最终传给后端的ID:', conversationId)

    if (isNaN(conversationId) || conversationId <= 0) {
        alert('删除失败：会话ID无效')
        return
    }

    if (historyList.value.length <= 1) {
        alert('至少保留一个对话哦')
        return
    }

    try {
        await request.post('/ai/delete', {}, {
            params: { conversationId }
        })

        if (activeId.value === id) {
            const remainSessions = historyList.value.filter(item => item.id !== id)
            if (remainSessions.length > 0) {
                const nextSession = remainSessions[0]
                activeId.value = nextSession.id
                loadSessionMessages(activeId.value)
            }
        }
        await loadUserConversations(false)
    } catch (e) {
        console.error('删除失败', e)
        alert('删除失败')
    }
}

// ====================== 打字机动画工具（核心伪流式逻辑） ======================
function typeWriter(elIndex, fullHtml, speed = 30) {
    let index = 0
    const timer = setInterval(() => {
        messages.value[elIndex].content = fullHtml.slice(0, index + 1)
        index++
        nextTick(() => {
            if (msgList.value) msgList.value.scrollTop = msgList.value.scrollHeight
        })
        if (index >= fullHtml.length) {
            clearInterval(timer)
        }
    }, speed)
}

// ====================== 发送消息（纯同步接口 + 前端打字机伪流式） ======================
const sendMessage = async () => {
    if (!inputText.value.trim() || !activeId.value) return
    const content = inputText.value.trim()
    inputText.value = ''

    // 1. 先推入用户消息
    messages.value.push({ isAi: 0, content, messageType: 'text' })
    // 2. 先创建空AI占位消息
    messages.value.push({ isAi: 1, content: 'thinking...', messageType: 'text', isHtml: true })
    const aiMsgIndex = messages.value.length - 1

    nextTick(() => {
        if (msgList.value) msgList.value.scrollTop = msgList.value.scrollHeight
    })

    try {
        // 调用原有同步chat接口，一次性拿到完整AI回复（自带联网检索、RAG、人设）
        const formData = new URLSearchParams()
        formData.append('conversationId', activeId.value)
        formData.append('content', content)
        formData.append('messageType', 'text')

        const res = await request.post('/ai/chat', formData)
        if (res.data.code !== 200) throw new Error(res.data.info || '出错')

        // 后端一次性返回完整回答文本
        const aiReplyObj = res.data.data
        const fullRawText = aiReplyObj.content
        // 处理链接超链接
        const fullHtmlText = linkify(fullRawText)

        // 调用打字机函数逐字渲染，实现伪流式打字效果
        typeWriter(aiMsgIndex, fullHtmlText, 30)

    } catch (err) {
        console.error('发送失败', err)
        messages.value[aiMsgIndex].content = '小豆包开小差啦，请重试'
    }
}

// ====================== 退出 ======================
const logout = () => {
    localStorage.clear()
    router.push('/')
}
</script>
<style scoped>
.chat-page {
    width: 100vw;
    height: 100vh;
    display: flex;
    flex-direction: column;
    background: #fff5f9;
}

.chat-header {
    height: 60px;
    background: #fff;
    display: flex;
    align-items: center;
    padding: 0 16px;
    border-bottom: 1px solid #f0d6ee;
}

.menu-btn {
    border: none;
    background: transparent;
    font-size: 20px;
    color: #b479c9;
    cursor: pointer;
}

.header-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    margin: 0 10px;
}

.title {
    font-weight: bold;
}

.logout-btn {
    margin-left: auto;
    color: #e8a1c1;
}

.chat-main {
    flex: 1;
    display: flex;
    position: relative;
    overflow: hidden;
}

.sidebar {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 240px;
    background: #fff;
    border-right: 1px solid #f0d6ee;
    z-index: 99;
    transform: translateX(-100%);
    transition: transform 0.3s;
}

.sidebar.open {
    transform: translateX(0);
}

.sidebar-title {
    padding: 16px;
    font-weight: bold;
    color: #fff;
    background: #e8a1c1;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.history-list {
    padding: 10px;
    overflow-y: auto;
}

.history-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 12px;
    border-radius: 8px;
    cursor: pointer;
    margin-bottom: 6px;
}

.history-item.active {
    background: #ffe8f2;
    color: #e8a1c1;
}

.delete-icon {
    color: #ff9999;
    font-size: 16px;
}

.chat-area {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.message-list {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
}

.message-wrapper {
    display: flex;
    margin-bottom: 16px;
    width: 100%;
}

.ai-message {
    display: flex;
    align-items: flex-start;
    gap: 10px;
}

.user-message {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    margin-left: auto;
}

.msg-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    flex-shrink: 0;
}

.bubble {
    max-width: 70%;
    padding: 12px 16px;
    border-radius: 12px;
}

.ai-bubble {
    background: #fff;
    color: #333;
}

.user-bubble {
    background: linear-gradient(90deg, #ffb7c5 0%, #d8a8e6 100%);
    color: white;
}

.ai-bubble,
.user-bubble {
    word-break: break-all;
    /* 强制长单词/链接换行 */
    overflow-wrap: break-word;
    /* 兼容所有浏览器的自动换行 */
    white-space: pre-wrap;
    /* 保留换行，同时允许自动折行 */
    max-width: 600px;
    /* 限制气泡最大宽度，不超过聊天框 */
}

.input-bar {
    display: flex;
    align-items: center;
    padding: 16px;
    background: rgba(255, 255, 255, 0.8);
    border-top: 1px solid #f0d6ee;
    position: relative;
}

.attach-icon {
    position: absolute;
    left: 20px;
    top: 50%;
    transform: translateY(-50%);
    color: #b479c9;
    font-size: 20px;
    cursor: pointer;
}

.chat-input {
    flex: 1;
    margin: 0 10px 0 40px;
}

:deep(.chat-input .el-input__wrapper) {
    border-radius: 20px;
    background: #fff;
    border: 1px solid #f0d6ee;
    padding-left: 30px;
}

.send-btn {
    background: linear-gradient(90deg, #ffb7c5 0%, #d8a8e6 100%);
    border: none;
}
</style>
