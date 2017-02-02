import Vue from 'vue'
import Rx from 'rxjs/Rx'
import VueRx from 'vue-rx'
import Hello from './app/Hello.vue'

import VueRouter from 'vue-router'
Vue.use(VueRouter, VueRx, Rx)

const router = new VueRouter({
  mode: 'history',
  routes: [
    {
      path: '/pomodoro/',
      components: {
        default: Hello
      }
    }
  ]
})

export default new Vue({
  el: '#root',
  router,
  render: h => h('router-view')
})

// Load business logic files
import './logicLoader.js'
