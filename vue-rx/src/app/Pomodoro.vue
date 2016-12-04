<template>
  <div>
    <button name="start"> Start </button>
    <button name="stop"> Stop </button>
    <p v-if="running"> running </p>
  </div>

</template>

<script>
  export default {
    name: 'Pomodoro',
    data() {
      return {
      }
    },
    subscriptions() {
      const clickStart = this.$fromDOMEvent('button[name=start]', 'click')
      .map(e => true)
      const clickedy = this.$fromDOMEvent('button[name=stop]', 'click')
            .map(e => false)
            .merge(clickStart)
      return {
        running: clickedy
      }
    }
  }
</script>

<style>
</style>
