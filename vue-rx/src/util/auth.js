import Rx from 'rxjs/Rx'

import rest from './rest.js'

// If access token state changes, update stream
const storage = Rx.Observable.fromEvent(window, 'storage')
      .map(_ => ({token: localStorage.getItem('accesstoken')}))
      .startWith({token: localStorage.getItem('accesstoken')})

export const login$ = new Rx.Subject()
  .do(_ => console.log('one'))
  .flatMap(creds => rest.POST('/session', creds))
  .do(_ => console.log('tuu'))
  .do(res => {
    if (!res.token) {
      throw new Error(res.error)
    }
    localStorage.setItem('accesstoken', res.token)
  })
  .flatMap(_ => rest.GET('/user/dummy'))
  .do(_ => console.log('trii'))
  .catch(e => Rx.Observable.of({error: e.message}))
  .share()

export const userData$ = login$.merge(storage)
  .scan((state, val) => Object.assign(state, val),
        //start with existing localstorage contents
        {token: localStorage.getItem('accesstoken')})
  .share()

export const logout = () => {
  // NYI
}

