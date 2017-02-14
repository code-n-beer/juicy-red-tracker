require('normalize.css/normalize.css')
require('styles/App.css')

import React from 'react'
import Rx from 'rxjs/Rx'
import R from 'react.reactive'

import UserComponent from 'components/UserComponent'

const AppComponent = () => {
  const time = Rx.Observable.interval(1000)
        .startWith(null)
        .map(() => new Date().toTimeString())
  return (
      <div>
        <h4> Testink </h4>
        <R.div> Time is {time} </R.div>
        <UserComponent/>
      </div>
  )
}

AppComponent.displayName = 'AppComponent'
export default AppComponent;
