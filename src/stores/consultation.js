import { defineStore } from 'pinia'

// 咨询数据状态管理
export const useConsultationStore = defineStore('consultation', {
  state: () => ({
    consultations: [
      {
        id: 'C20260426001',
        expertId: 1,
        expertName: '张教授',
        userName: '农户A',
        subject: '大棚西红柿病虫害防治',
        status: '处理中',
        createTime: '2026-04-26 11:15',
        messages: [
          {
            sender: 'user',
            content: '专家您好，我的大棚西红柿最近出现了叶片发黄的情况，不知道是什么原因？',
            time: '2026-04-26 11:15'
          }
        ]
      },
      {
        id: 'C20260425002',
        expertId: 2,
        expertName: '李博士',
        userName: '农户B',
        subject: '土壤酸化问题',
        status: '已回复',
        createTime: '2026-04-25 16:45',
        messages: [
          {
            sender: 'user',
            content: '专家您好，我的土壤检测显示酸化严重，pH值只有5.0，应该如何改良？',
            time: '2026-04-25 16:45'
          },
          {
            sender: 'expert',
            content: '您好，土壤酸化可以通过添加石灰、有机肥等方式改良。建议您每亩使用50-100公斤生石灰，同时增加有机肥的使用量，改善土壤结构。',
            time: '2026-04-25 17:20'
          }
        ]
      },
      {
        id: 'C20260424003',
        expertId: 3,
        expertName: '王研究员',
        userName: '农户C',
        subject: '玉米种植技术',
        status: '待处理',
        createTime: '2026-04-24 09:30',
        messages: [
          {
            sender: 'user',
            content: '专家您好，我想咨询一下玉米的最佳种植时间和密度。',
            time: '2026-04-24 09:30'
          }
        ]
      }
    ]
  }),
  actions: {
    // 添加新咨询
    addConsultation(consultation) {
      this.consultations.unshift(consultation)
    },
    
    // 更新咨询状态
    updateConsultationStatus(id, status) {
      const consultation = this.consultations.find(item => item.id === id)
      if (consultation) {
        consultation.status = status
      }
    },
    
    // 添加消息
    addMessage(id, message) {
      const consultation = this.consultations.find(item => item.id === id)
      if (consultation) {
        consultation.messages.push(message)
      }
    },
    
    // 获取专家的咨询列表
    getExpertConsultations(expertId) {
      return this.consultations.filter(item => item.expertId === expertId)
    },
    
    // 获取用户的咨询列表
    getUserConsultations() {
      return this.consultations
    }
  }
})
