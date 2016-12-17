<template>
  <div>
    <h2>dem pomos</h2>
    <p> ses {{userData}} sos </p>
    <ul name="pomodoros">
      <li v-for="pomo in pomos">
        {{pomo.length}} {{pomo.name}}
      </li>
    </ul>
  </div>

</template>

<script>
  import {state$} from '../util/state'
  export default {
    name: 'PomodoroList',
    subscriptions() {
      return {
        userData: state$
      }
    }
    // mabby use a ready made list component?
    //components: {
    //  User, Pomodoro
    //}
  };
</script>
