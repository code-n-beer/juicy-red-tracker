import Rx from 'rxjs/Rx'

import rest from './rest.js'

// If access token state changes, update stream
const storage = Rx.Observable.fromEvent(window, 'storage')
      .map(_ => ({token: localStorage.getItem('accesstoken')}))

export const login$ = new Rx.Subject()
  .flatMap(creds => rest.POST('/session', creds))
  .do(res => {
    if (!res.token) {
      throw new Error(res.error)
    }
    // Side effect, yikes
    localStorage.setItem('accesstoken', res.token)
  })
  .flatMap(_ => rest.GET('/user/dummy'))
    .catch(e => Rx.Observable.of(e.message))

export const userData$ = login$.merge(storage)
  .scan((state, val) => Object.assign(state, val),
        //start with existing localstorage contents
        {token: localStorage.getItem('accesstoken')})

export const logout = () => {
  // NYI
}

