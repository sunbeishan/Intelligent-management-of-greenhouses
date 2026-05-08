import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/expert/login',
    component: () => import('../views/ExpertLogin.vue'),
    meta: { title: '专家登录' }
  },
  { path: '/expert/dashboard',
    component: () => import('../views/ExpertDashboard.vue'),
    meta: { title: '专家控制台' }
  },
  { path: '/expert/environment',
    component: () => import('../views/ExpertEnvironment.vue'),
    meta: { title: '专家环境监测' }
  },
  { path: '/expert/farm',
    component: () => import('../views/ExpertFarm.vue'),
    meta: { title: '专家农田信息' }
  },
  { path: '/expert/profile',
    component: () => import('../views/ExpertProfile.vue'),
    meta: { title: '专家个人中心' }
  },
  {
    path: '/dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '统计分析' }
  },
  {
    path: '/environment',
    component: () => import('../views/Environment.vue'),
    meta: { title: '环境监测' }
  },
  {
    path: '/crop',
    component: () => import('../views/CropIdentification.vue'),
    meta: { title: '作物识别' }
  },
  {
    path: '/farm',
    component: () => import('../views/Farm.vue'),
    meta: { title: '农田信息管理' }
  },
  {
    path: '/irrigation',
    component: () => import('../views/Irrigation.vue'),
    meta: { title: '灌溉管理' }
  },
  {
    path: '/expert',
    component: () => import('../views/Expert.vue'),
    meta: { title: '专家咨询' }
  },
  {
    path: '/materials',
    component: () => import('../views/Materials.vue'),
    meta: { title: '农资管理' },
    children: [
      {
        path: '',
        redirect: 'purchase'
      },
      {
        path: 'purchase',
        component: () => import('../views/MaterialsPurchase.vue'),
        meta: { title: '农资采购' }
      },
      {
        path: 'products',
        component: () => import('../views/MaterialsProducts.vue'),
        meta: { title: '产品出售' }
      },
      {
        path: 'inventory',
        component: () => import('../views/MaterialsInventory.vue'),
        meta: { title: '库存' }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('../views/System.vue'),
    meta: { title: '系统管理' }
  },
  {
    path: '/profile',
    component: () => import('../views/Profile.vue'),
    meta: { title: '个人中心' }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title + ' - 智慧农业管理系统'
  next()
})

export default router
