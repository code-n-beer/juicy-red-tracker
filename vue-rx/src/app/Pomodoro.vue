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

    <div>
      <input class="new-task" v-model="newTaskName" placeholder="New task name">
      <button name="new-task-button"> Create </button>
    </div>

    <input class="pomo-length" v-model="pomodoroLength" placeholder="Pomodoro length in minutes">

    <select class="task-select" v-model="taskSelect">
      <option v-for="task in tasksPerCategory$" v-bind:value="task.id"> {{task.name}} </option>
    </select>

    <button v-show="running" name="stop"> Stop </button>
    <button v-show="running" name="finish"> Finish </button>
    <button v-show="!running"  name="start"> Start </button>
    <p v-show="running"> runnink {{pomodoroTimer}} plop </p>
  </div>
</template>

<script>
  import Rx from 'rxjs/Rx'
  import groupBy from 'lodash.groupby'

  import {state$, newStateObservable} from '../util/state'
  import {POST} from '../util/rest.js'

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
            .withLatestFrom(state$, (newCat, state) => ({categories: Object.assign({}, state.categories, {[newCat.id]: newCat})}))
            .map(e => e)

      newStateObservable(clickNewCat)

      const running = Rx.Observable.merge(clickStart, clickStop, clickFinish)

      function formatTime(time) {
      	const minutes = ~~(time/ 60)
        const seconds = time - minutes * 60
        return `${minutes} minutes ${seconds} seconds`
      }

      let audio = new Audio('https://cdn.rawgit.com/code-n-beer/juicy-red-tracker/98910de70e169e71aaa01c684c8934a6a4214fea/re-frame-pomofront/resources/public/audio/notification.mp3')

      function ringBell(time) {
        if(time <= 1) {
          console.log('played!')
          audio.play()
        }
        return time;
      }

      const pomodoroTimer = running.withLatestFrom(pomodoroLength$, (a, b) => [a,b])
            .switchMap(([isRunning, length]) => {
              return isRunning
                ? Rx.Observable.timer(0, 1000).take(length)
                //? Rx.Observable.timer(0, 1000).take(length * 60)
                : Rx.Observable.empty()})
            //.withLatestFrom(pomodoroLength$, (elapsed, length) => length * 60 - elapsed)
            .withLatestFrom(pomodoroLength$, (elapsed, length) => length - elapsed)
            .map(ringBell)
            .map(formatTime)

      return {
        loggedIn, categories, selectedCategory, tasksPerCategory$, newCatName, newTaskName, running, pomodoroLength$, pomodoroTimer, state$
      }
    }
  }
</script>

<style>
  .pomo-length {
  }
</style>
