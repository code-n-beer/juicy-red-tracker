<template>
  <div>
    <input class="pomo-length" v-model="pomodoroLength" placeholder="Pomodoro length in minutes">
    <input class="pomo-name" v-model="pomodoroName" placeholder="Pomodoro task name">
    <button name="start"> Start </button>
    <button name="stop"> Stop </button>
    <button v-if="running" name="finish"> Finish </button>
    <p v-if="running"> running </p>
    <ul name="pomodoros">
      <li v-for="pomo in pomos">
        {{pomo.length}} {{pomo.name}} 
      </li>
    </ul>

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
      const clickStart = this.$fromDOMEvent('button[name=start]', 'click')
            .map(e => true)
      const running = this.$fromDOMEvent('button[name=stop]', 'click')
            .map(e => false)
            .merge(clickStart, clickFinish)
      const pomodoro = running.filter(running => running)
            .withLatestFrom(pomodoroLength$, (a, b) => b)
            .switchMap((t => Rx.Observable.timer(0, 1000)))
            .map(e => console.log(e))

            
      // start timers
      // get a stream of pomodoro data
      // combine with finish click stream
      // post the stuff
      //const pomos = clickFinish.withLatestFrom(running, (a,b) => [a,b]).filter([a,b] => b).scan((acc,[a,b]) => [x, ...acc], [])


      return {
        running, pomodoro, pomodoroLength$ //, pomos
      }
    }
  }
</script>

<style>
  .pomo-length {
  }
</style>
