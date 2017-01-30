<template>
  <div>
    <h2>dem pomos</h2>
    <div> {{JSON.stringify(userData, null, 2)}} </div>
    <ul name="pomodoros">
      <li v-for="pomo in pomos">
        {{pomo.length}} {{pomo.name}} {{pomo.date}}
      </li>
    </ul>
  </div>

  </template>

<script>
  import {state$} from '../util/state'
  export default {
    name: 'PomodoroList',
    subscriptions() {
      const pomos = state$
            .filter(e => e.pomodoros)
            .pluck('pomodoros')
            .map(pomos =>
                 pomos.map(pomo => {
                   console.log(pomo.id)
                   const d = new Date(pomo.created_at)
                   return {
                     name: 'todo',
                     date: `${d.toUTCString()}`,
                     length: pomo.minutes
                   }
                 }))
      return {
        userData: state$,
        pomos
      }
    }
    // mabby use a ready made list component?
    //components: {
    //  User, Pomodoro
    //}
  };
</script>
