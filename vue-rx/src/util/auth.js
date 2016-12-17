import Rx from 'rxjs/Rx'

import rest from './rest.js'

// If access token state changes, update stream
const storage = Rx.Observable.fromEvent(window, 'storage')
      .map(_ => ({token: localStorage.getItem('accesstoken')}))
      .startWith({token: localStorage.getItem('accesstoken')})

export const state = {
  userData$: storage
    .scan((state, val) => Object.assign(state, val),
          //start with existing localstorage contents
          {token: localStorage.getItem('accesstoken')})
}

export const login = (creds) => {
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
  state.userData$ = state.userData$.merge(login$)
  return login$
}

export const logout = () => {
  window.localStorage.removeItem('accesstoken')
  location.reload()
}

