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
    <div> {{JSON.stringify(userData, null, 2)}} </div>
  </div>

  </template>

<script>
  import {state$} from '../util/state'
  export default {
    name: 'PomodoroList',
    subscriptions() {
      const tasksWithPomos = state$
            .filter(e => e.tasks)
            .map(state => state.tasks
                 .map(task => Object.assign(task, {'pomos': state.pomodoros.filter(p => p.task_id === task.id && p.minutes !== 0)})))
            .do(p => console.log(p))
      return {
        userData: state$,
        tasksWithPomos
      }
    }
    // mabby use a ready made list component?
    //components: {
    //  User, Pomodoro
    //}
  };
</script>
