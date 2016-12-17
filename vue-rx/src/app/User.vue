<template>
  <div class="login">
    <!-- https://jsfiddle.net/fddxe9zd/ -->
    <div v-if="userData && userData.token" class="logged-in">
      <p v-show="userData.token"> Logged in! </p>
      <button v-show="userData.token" name="logout"> Logout </button>
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
  import {login, logout, state$} from '../util/auth'
  export default {
    name: 'User',
    data() {
      return {
        email: '',
        password: ''
      }
    },
    subscriptions() {
      const logout$ = this.$fromDOMEvent('button[name=logout]', 'click')
                           .do(_ => console.log('hoh'))
                           .do(_ => logout())

      const email = this.$watchAsObservable('email').pluck('newValue')
                           //.do(_ => console.log('mail'))
      const password = this.$watchAsObservable('password').pluck('newValue')
                           //.do(_ => console.log('pass'))
      const creds = email.combineLatest(password)
                           //.do(_ => console.log('creds'))
      const loginRes = this.$fromDOMEvent('button[name=login]', 'click')
                           .do(_ => console.log('1'))
                           .withLatestFrom(creds, (_, [email, pass]) => [email, pass])
                           .do(_ => console.log('2'))
                           .switchMap(([email, password]) => login({email, password}))
                           .do(_ => console.log('3'))
                           .do(x => console.log(x))
                           //.flatMap(result => {console.log('flatmap'); return result})

      return {
        userData: state$, //.do(x => console.log(x)),
        loginData: loginRes,
        logout$
      }
    }
  }
</script>

<style lang="sass">
 .login {
   background-color: white;
 }
</style>
