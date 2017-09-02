import Bacon from 'baconjs'
import {POST} from './ajax.js'

export const register = (creds) => POST('/user', creds)
export const login = (creds) => console.log('login') || POST('/session', creds)

export const logout = () => {
  window.localStorage.removeItem('accesstoken')
  location.reload()
}
