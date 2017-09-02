import Bacon from 'baconjs'
import {POST} from './ajax.js'
const storage$ = Bacon.fromEvent(window, 'click')
      .onValue(() => ({token: localStorage.getItem('accesstoken') }))

export const register = (creds) => POST('/user', creds)
export const login = (creds) => console.log('login') || POST('/session', creds)

export const logout = () => {
  window.localStorage.removeItem('accesstoken')
  location.reload()
}
