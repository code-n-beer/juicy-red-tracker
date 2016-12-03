<template>
  <div class="login">
    <div v-if="loggedIn" class="logged-in">
      <p> Logged in! </p>
      <p> {{ loggedIn }} </p>
    </div>
    <div v-else class="not-logged-in">
      <input type="text" placeholder="email" name="email" v-model="email"/>
      <input type="text" placeholder="password will be sent as plain text" name="password" v-model="password"/>
      <button name="login"> Login </button>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue'
  import Rx from 'rxjs/Rx'
  import VueRx from 'vue-rx'
// AFAIK Vue.use rx has to be done in all components that want to use Rxjs..
  Vue.use(VueRx, Rx)

  import {login} from '../util/auth'
  export default {
    name: 'User',
    data() {
      return {
        email: '',
        password: ''
      }
    },
    subscriptions() {
      const email = this.$watchAsObservable('email').pluck('newValue')
      const password = this.$watchAsObservable('password').pluck('newValue')
      const creds = email.combineLatest(password)
      const clickedyClack = this.$fromDOMEvent('button[name=login]', 'click')
            .withLatestFrom(creds, (_, [email, pass]) => [email, pass])
            .switchMap(([email, pass]) => login(email, pass))
      return {
        loggedIn: clickedyClack
      }
    }
  }
</script>

<style lang="sass">
  .login {
    background-color: white;
  }
</style>
