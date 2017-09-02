import React from 'baret'
import Bacon from 'baconjs'

const oncePerSecond = Bacon.interval(1000).toProperty()

const Clock = () =>
  <div>
    The time is {oncePerSecond.map(() => new Date().toString())}.
  </div>

 export default Clock