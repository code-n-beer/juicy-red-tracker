'use strict';

import React from 'react'
import R from 'react.reactive'
import Rx from 'rxjs/Rx'

require('styles//User.css');

import {state$, newStateObservable} from '../util/state.js'

const userData = state$
const loginData = state$

let Messages = (props) => (
  <R.div>
    {userData && userData.token
     ? <div class="logged-in">
          <p v-show="userData.token"> Logged in! </p>
          <button v-show="userData.token" name="logout"> Logout </button>
       </div>
     : null}
  {loginData && loginData.error
   ? <p> {loginData.error} </p>
   : null
  }
  {loginData && loginData.message
   ? <p> {loginData.message} </p>
   : null
  }
  </R.div>
)

let Login = R(({events}) => {
  const clicked = Rx.Observable.fromEvent(events, 'login-btn')
        .map(() => 'ses')
  return (
    <div>
      Login:
      <input  type="text" placeholder="email" name="email"/>
      <input type="text" placeholder="password will be sent as plain text" name="password"/>
      <R.button emits={{click: "login-btn"}} name="login"> Login </R.button>
      <R.div>{clicked}</R.div>
    </div>
  )
})

let Register = (props) => (
    <div>
      Register:
      <input type="text" placeholder="email" name="reg-email" />
      <input type="text" placeholder="password will be sent as plain text" name="reg-password"/>
      <button name="register"> Register </button>
    </div>
)

let UserComponent = (props) => (
    <div className="pomodoro-component">
    <Messages/>
    {!userData || !userData.token
     ? <div> <Login/> <Register/> </div>
     : null
    }
  </div>
)

UserComponent.displayName = 'UserComponent';

// Uncomment properties you need
// PomodoroComponent.propTypes = {};
// PomodoroComponent.defaultProps = {};

export default UserComponent;
