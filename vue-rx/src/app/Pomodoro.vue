<template>
  <div v-show="loggedIn">
    <div>
      <input class="new-category" v-model="newCatName" placeholder="New category name">
      <button name="new-category-button"> Create </button>
    </div>

    Select category:
    <select class="category-select" v-model="categorySelect">
      <option v-for="category in categories" v-bind:value="category.id"> {{category.name}} </option>
    </select>

  <div v-if="selectedCategory">
      <div>
        <input class="new-task" v-model="newTaskName" placeholder="New task name">
        <button name="new-task-button"> Create </button>
      </div>

      Select task:
      <select class="task-select" v-model="taskSelect">
      <option v-for="task in tasksPerCategory$" v-bind:value="task.id"> {{task.name}} </option>
      </select>

      <div v-if="selectedTask">
        <input class="pomo-length" v-model="pomodoroLength" placeholder="Pomodoro length in minutes">
        <button v-show="running" name="stop"> Stop </button>
        <button v-show="running" name="finish"> Finish </button>
        <button v-show="!running"  name="start"> Start </button>
        <p v-show="running"> runnink {{pomodoroTimer}} plop </p>
      </div>
    </div>
  </div>
</template>

<script>
  import Rx from 'rxjs/Rx'
  import groupBy from 'lodash.groupby'

  import {state$, newStateObservable} from '../util/state'
  import {POST} from '../util/rest.js'

  const audio = new Audio('notification.mp3')

  export default {
    name: 'Pomodoro',
    data() {
      return {
        pomodoroLength: 25,
        categorySelect: false,
        taskSelect: false,
        newCatName: '',
        newTaskName: '',
      }
    },
    subscriptions() {
      const loggedIn = state$
            .filter(s => s.id)

      const selectedCategory = this.$watchAsObservable('categorySelect')
            .pluck('newValue')

      const selectedTask = this.$watchAsObservable('taskSelect')
            .pluck('newValue')

      const tasksPerCategory$ = selectedCategory
            .withLatestFrom(state$, (selectedCat, state) => {
              return state.categories[selectedCat].tasks
            })

      const categories = state$
            .map(s => s.categories)

      const pomodoroLength$ = this.$watchAsObservable('pomodoroLength')
            .pluck('newValue')
            .startWith(25) // TODO: figure out how to not need more than one initialization (25)
            .map(val => parseInt(val))
      const clickFinish = this.$fromDOMEvent('button[name=finish]', 'click')
            .map(e => false)

      const clickFinishReq = clickFinish
            .withLatestFrom(pomodoroLength$, selectedTask, (click, pLength, taskId) => Rx.Observable.fromPromise(POST(`/user/task/${taskId}/pomodoro`, {minutes: pLength, success: true})))
            .flatMap(e => e)
            .withLatestFrom(state$, (newPomo, state) => ({pomodoros: [...state.pomodoros, newPomo]}))

      newStateObservable(clickFinishReq)

      const clickStop = this.$fromDOMEvent('button[name=stop]', 'click')
            .map(e => false)
      const clickStart = this.$fromDOMEvent('button[name=start]', 'click')
            .map(e => true)

      const newTaskName = this.$watchAsObservable('newTaskName')
            .pluck('newValue')

      function getModifyTaskDiff(newTask, state) {
			  let parentCat = state.categories[newTask.category_id]
        parentCat.tasks.push(newTask)
        let tasks = Object.assign({}, state.tasks)
        tasks[newTask.id] = newTask
        return {
          categories: Object.assign({}, state.categories, {[newTask.category_id]: parentCat}),
          tasks: tasks
        }
      }

      const clickNewTask = this.$fromDOMEvent("button[name=new-task-button]", 'click')
            .withLatestFrom(newTaskName, (click, taskName) => taskName)
            .filter(e => e) // should have a name
            .withLatestFrom(selectedCategory, (taskName, catId) => Rx.Observable.fromPromise(POST(`/user/category/${catId}/task`, {'name': taskName})))
            .flatMap(e => e)
            .withLatestFrom(state$, (newTask, state) => getModifyTaskDiff(newTask, state))

      newStateObservable(clickNewTask)

      const newCatName = this.$watchAsObservable('newCatName')
            .pluck('newValue')

      const clickNewCat = this.$fromDOMEvent("button[name=new-category-button]", 'click')
            .withLatestFrom(newCatName, (click, catName) => catName)
            .filter(e => e)
            .flatMap(n => Rx.Observable.fromPromise(POST('/user/category', {'name': n})))
            .map(newCat => Object.assign(newCat, {tasks: []}))
            .withLatestFrom(state$, (newCat, state) => ({categories: Object.assign({}, state.categories, {[newCat.id]: newCat})}))
            .map(e => e)

      newStateObservable(clickNewCat)

      const running = Rx.Observable.merge(clickStart, clickStop, clickFinish)

      function formatTime(time) {
      	const minutes = ~~(time/ 60)
        const seconds = time - minutes * 60
        return `${minutes} minutes ${seconds} seconds`
      }

      function sendNotification() {
        Notification.requestPermission(function (permission) {
          if (permission === "granted") {
            var notification = new Notification("Pomodoro finished!");
          }
        });
      }

      function ringBell(time) {
        if(time <= 1) {
          console.log('played!')
          audio.play()
          sendNotification()
        }
        return time;
      }

      const pomodoroTimer = running.withLatestFrom(pomodoroLength$, (a, b) => [a,b])
            .switchMap(([isRunning, length]) => {
              return isRunning
                //? Rx.Observable.timer(0, 1000).take(length)
                ? Rx.Observable.timer(0, 1000).take(length * 60)
                : Rx.Observable.empty()})
            .withLatestFrom(pomodoroLength$, (elapsed, length) => length * 60 - elapsed)
            //.withLatestFrom(pomodoroLength$, (elapsed, length) => length - elapsed)
            .map(ringBell)
            .map(formatTime)

      return {
        loggedIn, categories, selectedCategory, selectedTask, tasksPerCategory$, newCatName, newTaskName, running, pomodoroLength$, pomodoroTimer, state$
      }
    }
  }
</script>

<style>
  .pomo-length {
  }
</style>
