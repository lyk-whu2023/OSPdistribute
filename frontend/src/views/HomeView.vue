<template>
  <div class="home-view">
    <h1>欢迎来到OSP分布式系统</h1>
    <div class="nav-links">
      <router-link to="/auth" class="nav-link">用户登录/注册</router-link>
      <router-link to="/user" class="nav-link">用户中心</router-link>
      <router-link to="/address" class="nav-link">地址管理</router-link>
    </div>
  </div>
</template>

<script setup>
// HomeView.vue
</script>

<style scoped>
.home-view {
  text-align: center;
  padding: 100px 20px;
}

.nav-links {
  margin-top: 50px;
}

.nav-link {
  display: inline-block;
  margin: 0 20px;
  padding: 12px 30px;
  background: #409eff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background 0.3s;
}

.nav-link:hover {
  background: #66b1ff;
}
</style>