<template>
  <div>
    <h1>{{ hello }}</h1>
    <user></user>
    <pomodoro/>
  </div>

</template>

<script>
  import User from './User.vue'
  import Pomodoro from './Pomodoro.vue'
  export default {
    name: 'Hello',
    data() {
      return {
        hello: 'Pomodoro'
      };
    },
    components: {
      User, Pomodoro
    }
  };
</script>
