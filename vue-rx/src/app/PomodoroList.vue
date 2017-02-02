<template>
  <div>
    <h2>dem pomos</h2>
    <ul name="taskpomos">
      <li v-for="task in tasksWithPomos">
        {{task.name}}
        <div v-for="pomo in task.pomos">
          {{pomo.minutes}} minutes on {{(new Date(pomo.created_at)).toUTCString()}}
        </div>
      </li>
    </ul>
  </div>

  </template>

<script>
  import {state$} from '../util/state'
  export default {
    name: 'PomodoroList',
    subscriptions() {
      const tasksWithPomos = state$
            .filter(s => s.tasks && s.pomodoros)
            .filter(s => Object.keys(s.tasks).length > 0)
            .map(state => state.tasks && state.pomodoros ? Object.assign(...Object.keys(state.tasks).map(k => ({[k]: Object.assign(state.tasks[k], {'pomos': state.pomodoros.filter(p => p.task_id === state.tasks[k].id && p.minutes !== 0)})}))) : state.tasks)

      return {
        tasksWithPomos
      }
    }
    // mabby use a ready made list component?
    //components: {
    //  User, Pomodoro
    //}
  };
</script>
