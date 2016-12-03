<template>
  <div class="login">
    <div v-if="loggedIn" class="logged-in">
      <p> Logged in! </p>
    </div>
    <div v-else class="not-logged-in">
      <input type="text" placeholder="email" name="email" v-model="email"/>
      <input type="text" placeholder="password will be sent as plain text" name="password" v-model="password"/>
      <button name="login"> Login </button>
    </div>
  </div>
</template>

<script>
  // The importing has to be done in all components that have Rxjs..
  import Vue from 'vue'
  import Rx from 'rxjs/Rx'
  import VueRx from 'vue-rx'
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
      login('herp', 'derp')
      console.log('vittu nyt hermanni')
      const email = this.$watchAsObservable('email').pluck('newValue')
      const password = this.$watchAsObservable('password').pluck('newValue')
      const creds = email.combineLatest(password)
            .map(arr => {console.log(arr); return arr})
      const clickedyClack = this.$fromDOMEvent('button[name=login]', 'click')
            .map(arr => {console.log(arr); return arr})
            .withLatestFrom(creds, (_, [email, pass]) => [email, pass])
            .map(arr => {console.log(arr); return arr})
            .switchMap(([email, pass]) => login(email, pass))
            .map(response => console.log(response))
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
