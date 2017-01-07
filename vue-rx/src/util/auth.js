import Rx from 'rxjs/Rx'

import {POST} from './rest.js'
import {newStateObservable} from './state.js'

// If window localstorage changes, update access token
const storage$ = Rx.Observable.fromEvent(window, 'storage')
      .map(_ => ({token: localStorage.getItem('accesstoken')}))

newStateObservable(storage$)

export const login = (creds) => {
  const login$ = Rx.Observable.fromPromise(POST('/session', creds))
        .map(res => {
          if (!res.token) {
            throw new Error(res.error)
          }
          localStorage.setItem('accesstoken', res.token)
          return res
        })
        .catch(e => {
          return Rx.Observable.of({error: e.message})
        }).share()
  newStateObservable(login$)
  return login$
}

export const logout = () => {
  window.localStorage.removeItem('accesstoken')
  location.reload()
}

