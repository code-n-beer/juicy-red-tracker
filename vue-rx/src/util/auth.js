import Rx from 'rxjs/Rx'

import rest from './rest.js'
import {newStateObservable} from './state.js'

// If access token state changes, update stream
const storage$ = Rx.Observable.fromEvent(window, 'storage')
      .map(_ => ({token: localStorage.getItem('accesstoken')}))

newStateObservable(storage$)

export const login = (creds) => {
  console.log('run login')
  const login$ = Rx.Observable.fromPromise(rest.POST('/session', creds))
        .do(_ => console.log('one'))
        .map(res => {
          console.log('tuut tuut')
          if (!res.token) {
            console.log('not has token')
            throw new Error(res.error)
          } else {
            console.log('haz token')
          }
          localStorage.setItem('accesstoken', res.token)
          return res
        })
        .do(_ => console.log('blblbl'))
        .catch(e => {
          console.log('errord')
          console.log(e)
          return Rx.Observable.of({error: e.message})
        })
        .share()
  newStateObservable(login$)
  return login$
}

export const logout = () => {
  window.localStorage.removeItem('accesstoken')
  location.reload()
}

