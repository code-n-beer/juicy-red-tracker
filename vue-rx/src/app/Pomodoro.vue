<template>
  <div>
    <div>
      <input class="new-category" v-model="newCatName" placeholder="New category name">
      <button name="new-category-button"> Create </button>
    </div>

    Select category:
    <select class="category-select" v-model="categorySelect">
      <option v-for="category in state$.categories" v-bind:value="category.id"> {{category.name}} </option>
    </select>

    <div>
      <input class="new-task" v-model="newTaskName" placeholder="New task name">
      <button name="new-task-button"> Create </button>
    </div>

    <input class="pomo-length" v-model="pomodoroLength" placeholder="Pomodoro length in minutes">

    <select class="task-select">
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

  import {state$} from '../util/state'
  import {POST} from '../util/rest.js'

  export default {
    name: 'Pomodoro',
    data() {
      return {
        pomodoroLength: 25,
        categorySelect: false,
      }
    },
    subscriptions() {
      const selectedCategory = this.$watchAsObservable('categorySelect')
            .pluck('newValue')

      const tasksPerCategory$ = state$
            .filter(s => s.tasks)
            .map(s => groupBy(s.tasks, t => t.category_id))
            .combineLatest(selectedCategory, (tasks, selected) => tasks[selected])

      const pomodoroLength$ = this.$watchAsObservable('pomodoroLength')
            .pluck('newValue')
            .startWith(25) // TODO: figure out how to not need more than one initialization (25)
            .map(val => parseInt(val))
      const clickFinish = this.$fromDOMEvent('button[name=finish]', 'click')
            .map(e => false)
      const clickStop = this.$fromDOMEvent('button[name=stop]', 'click')
            .map(e => false)
      const clickStart = this.$fromDOMEvent('button[name=start]', 'click')
            .map(e => true)

      const newTaskName = this.$watchAsObservable('newTaskName')
            .pluck('newValue')

      const clickNewTask = this.$fromDOMEvent("button[name=new-task-button]", 'click')
            .withLatestFrom(newTaskName, (click, taskName) => taskName)
            .filter(e => e) // should have a name
            .withLatestFrom(selectedCategory, (taskName, catId) => Rx.Observable.fromPromise(POST(`/user/category/${catId}/task`, {'name': taskName})))

      const newCatName = this.$watchAsObservable('newCatName')
            .pluck('newValue')

      const clickNewCat = this.$fromDOMEvent("button[name=new-category-button]", 'click')
            .withLatestFrom(newCatName, (click, catName) => catName)
            .filter(e => e)
            .map(n => Rx.Observable.fromPromise(POST('/user/category', {'name': n})))

      const running = Rx.Observable.merge(clickStart, clickStop, clickFinish)

      const pomodoroTimer = running.withLatestFrom(pomodoroLength$, (a, b) => [a,b])
            .switchMap(([isRunning,length]) => {
              return isRunning
                ? Rx.Observable.timer(0, 1000).take(length * 60)
                : Rx.Observable.empty()})
      return {
        selectedCategory, tasksPerCategory$, newCatName, newTaskName, clickNewTask, clickNewCat, running, pomodoroLength$, pomodoroTimer, state$
      }
    }
  }
</script>

<style>
  .pomo-length {
  }
</style>
