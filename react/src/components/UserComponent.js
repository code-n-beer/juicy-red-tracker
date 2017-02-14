'use strict';

import React from 'react'
import R from 'react.reactive'

require('styles//User.css');

import state$ from '../util/state.js'

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

let Login = (props) => (
    <div>
    Login:
    <input type="text" placeholder="email" name="email"/>
    <input type="text" placeholder="password will be sent as plain text" name="password"/>
    <button name="login"> Login </button>
    </div>
)

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
