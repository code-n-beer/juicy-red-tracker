import Bacon from 'baconjs'
const state = new Bacon.Bus()

const loggedIn$ = new Bacon.Bus()

export {state as default, loggedIn$}
