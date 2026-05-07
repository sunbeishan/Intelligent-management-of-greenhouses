import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => {
    const savedUser = localStorage.getItem('user')
    let userData = {
      name: '管理员',
      role: 'admin'
    }
    if (savedUser) {
      try {
        userData = JSON.parse(savedUser)
      } catch (e) {
        console.error('Failed to parse saved user:', e)
      }
    }
    return {
      sidebarOpen: true,
      user: userData
    }
  },
  actions: {
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen
    },
    setUser(user) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout() {
      this.user = {
        name: '管理员',
        role: 'admin'
      }
      localStorage.removeItem('user')
    }
  }
})
