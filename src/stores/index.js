import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarOpen: true,
    user: {
      name: '管理员',
      role: 'admin'
    }
  }),
  actions: {
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen
    },
    setUser(user) {
      this.user = user
    },
    logout() {
      this.user = {
        name: '管理员',
        role: 'admin'
      }
    }
  }
})
