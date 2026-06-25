<template>
  <router-view />
</template>
<style>
/* 全局隐藏滚动条，但保留滚动功能 */
*::-webkit-scrollbar {
  width: 0 !important;
  height: 0 !important;
  opacity: 0 !important;
  visibility: hidden !important;
}

html,
body {
  margin: 0;
  padding: 0;
  height: 100vh;
}
</style>