<template>
  <div>
    <h1>{{ hello }}</h1>
    <user></user>
  </div>

</template>

<script>
  import User from './User.vue'
  export default {
    name: 'Hello',
    data() {
      return {
        hello: 'ses!'
      };
    },
    components: {
      User
    }
  };
</script>
