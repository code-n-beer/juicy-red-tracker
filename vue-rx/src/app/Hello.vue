<template>
  <div>
    <h1>{{ hello }}</h1>
    <user> </user>
    <pomodoro> </pomodoro>
    <PomodoroList> </PomodoroList>
  </div>

</template>

<script>
  import Vue from 'vue'
  import Rx from 'rxjs/Rx'
  import VueRx from 'vue-rx'
  Vue.use(VueRx, Rx)

  import User from './User.vue'
  import Pomodoro from './Pomodoro.vue'
  import PomodoroList from './PomodoroList.vue'
  export default {
    name: 'Hello',
    data() {
      return {
        hello: 'Pomodoro'
      };
    },
    components: {
      User, Pomodoro, PomodoroList
    }
  };
</script>
