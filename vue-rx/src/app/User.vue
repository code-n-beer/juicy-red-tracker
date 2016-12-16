<template>
  <div class="login">
    <div v-if="userData && !userData.error" class="logged-in">
      <p v-show="userData.token"> Logged in! </p>
    </div>
    <p v-if="loginData && loginData.error"> {{ loginData.error }} </p>
    <div v-if="!userData || !userData.token" class="not-logged-in">
      <input type="text" placeholder="email" name="email" v-model="email"/>
      <input type="text" placeholder="password will be sent as plain text" name="password" v-model="password"/>
      <button name="login"> Login </button>
    </div>
  </div>
</template>

<script>
  import Rx from 'rxjs/Rx'
  import {login$, userData$} from '../util/auth'
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
      const loginRes = this.$fromDOMEvent('button[name=login]', 'click')
                           .do(_ => console.log('1'))
                           .withLatestFrom(creds, (_, [email, pass]) => [email, pass])
                           .do(_ => console.log('2'))
                           .switchMap(([email, password]) => {
                             console.log('loginninin')
                             login$.next({email, password})
                             return [email, password]
                           })
                           .do(_ => console.log('3'))
      //.flatMap(result => {console.log('flatmap'); return result})
      return {
        loginResponse: loginRes,
        userData: userData$,
        loginData: login$.do(_ => console.log('derp'))
      }
    }
  }
</script>

<style lang="sass">
 .login {
   background-color: white;
 }
</style>
