<template>
  <div class="login">
    <!-- https://jsfiddle.net/fddxe9zd/ -->
    <div v-if="userData && userData.token" class="logged-in">
      <p v-show="userData.token"> Logged in! </p>
      <button v-show="userData.token" name="logout"> Logout </button>
    </div>
    <p v-if="loginData && loginData.error"> {{ loginData.error }} </p>
    <p v-if="loginData && loginData.message"> {{ loginData.message }} </p>
    <div v-if="!userData || !userData.token" class="not-logged-in">
      <div>
        Login:
        <input type="text" placeholder="email" name="email" v-model="email"/>
        <input type="text" placeholder="password will be sent as plain text" name="password" v-model="password"/>
        <button name="login"> Login </button>
      </div>

      <div>
        Register:
        <input type="text" placeholder="email" name="reg-email" v-model="regEmail"/>
        <input type="text" placeholder="password will be sent as plain text" name="reg-password" v-model="regPassword"/>
        <button name="register"> Register </button>
      </div>
    </div>
  </div>
</template>

<script>
 import Rx from 'rxjs/Rx'
 import {login, logout, register} from '../util/auth'
 import {state$} from '../util/state.js'
 export default {
   name: 'User',
   data() {
     return {
       email: '',
       password: '',
       regEmail: '',
       regPassword: ''
     }
   },
   subscriptions() {
     const logout$ = this.$fromDOMEvent('button[name=logout]', 'click')
                         .do(_ => console.log('hoh'))
                         .do(_ => logout())
     const email = this.$watchAsObservable('email').pluck('newValue')
     const password = this.$watchAsObservable('password').pluck('newValue')
     const creds = email.combineLatest(password)

     const loginRes = this.$fromDOMEvent('button[name=login]', 'click')
                          .withLatestFrom(creds, (_, [email, pass]) => [email, pass])
                          .switchMap(([email, password]) => login({email, password}))

     const registerEmail = this.$watchAsObservable('regEmail').pluck('newValue')
     const registerPass = this.$watchAsObservable('regPassword').pluck('newValue')
     const regCreds = registerEmail.combineLatest(registerPass)

     const registerRes = this.$fromDOMEvent('button[name=register]', 'click')
           .do(e=>console.log(e))
           .withLatestFrom(regCreds, (_, [email, pass]) => [email, pass])
           .do(e=>console.log(e))
           .switchMap(([email, password]) => register({email, password}))
           .do(e=>console.log(e))

     const logState = loginRes.merge(registerRes);
     return {
       userData: state$,
       //loginData: loginRes,
       loginData: logState,
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
