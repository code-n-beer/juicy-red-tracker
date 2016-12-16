<template>
  <div>
    <input class="pomo-length" v-model="pomodoroLength" placeholder="Pomodoro length in minutes">
    <input class="pomo-name" v-model="pomodoroName" placeholder="Pomodoro task name">
    <button v-show="running" name="stop"> Stop </button>
    <button v-show="running" name="finish"> Finish </button>
    <button v-show="!running"  name="start"> Start </button>
    <p v-show="running"> runnink {{pomodoroTimer}} plop </p>
  </div>

</template>

<script>
  import Rx from 'rxjs/Rx'
  export default {
    name: 'Pomodoro',
    data() {
      return {
        pomodoroLength: 25
      }
    },
    subscriptions() {
      const pomodoroLength$ = this.$watchAsObservable('pomodoroLength')
            .pluck('newValue')
            .startWith(25) // TODO: figure out how to not need more than one initialization (25)
            .map(val => parseInt(val))
      const clickFinish= this.$fromDOMEvent('button[name=finish]', 'click')
            .map(e => false)
      const clickStop = this.$fromDOMEvent('button[name=stop]', 'click')
            .map(e => false)
      const clickStart = this.$fromDOMEvent('button[name=start]', 'click')
            .map(e => true)

      const running = Rx.Observable.merge(clickStart, clickStop, clickFinish)

      const pomodoroTimer = running.withLatestFrom(pomodoroLength$, (a, b) => [a,b])
            .switchMap(([isRunning,length]) => {
              return isRunning
                ? Rx.Observable.timer(0, 1000).take(length * 60)
                : Rx.Observable.empty()})
      return {
        running, pomodoroLength$, pomodoroTimer
      }
    }
  }
</script>

<style>
  .pomo-length {
  }
</style>
