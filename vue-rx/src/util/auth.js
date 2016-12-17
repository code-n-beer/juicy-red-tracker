import Rx from 'rxjs/Rx'

import rest from './rest.js'

//      .map(_ => state => Object.assign(state, {token: localStorage.getItem('accesstoken')}))

const stateObserver$ = new Rx.Subject()
      .scan((stateObservable, newObservable) => Rx.Observable.merge(stateObservable, newObservable))

export const newStateObservable = (observable) => {
  const sub = new Rx.Subject()
  observable.subscribe(sub)
  stateObserver$
    .next(sub
          .map((obj) => state => Object.assign({}, state, obj)))
}

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

export const state$ = new Rx.Subject()

const reducer = stateObserver$
      .do(_ => console.log('state observer 1'))
      .do(x => console.log(x))
      .switchMap(x => x)
      .do(_ => console.log('state observer 2'))
      .do(x => console.log(x))
      .scan((state, fn) => fn(state), {})
      .startWith({token: localStorage.getItem('accesstoken')})

reducer.subscribe(state$)

export const logout = () => {
  window.localStorage.removeItem('accesstoken')
  location.reload()
}

//export const state = {
//  userData$: storage
//    .scan((state, val) => Object.assign(state, val),
//          //start with existing localstorage contents
//          {token: localStorage.getItem('accesstoken')})
//}
//const login$ = new Rx.Subject()
//      .do(_ => console.log('zero'))
//      .flatMap(creds => rest.POST('/session', creds))
//      .do(_ => console.log('one'))
//      .map(res => {
//        console.log('tuut tuut')
//        if (!res.token) {
//          console.log('not has token')
//          throw new Error(res.error)
//        } else {
//          console.log('haz token')
//        }
//        localStorage.setItem('accesstoken', res.token)
//        return res
//      })
//      .map((obj) => state => Object.assign({}, state, obj))
//      .do(_ => console.log('blblbl'))
//      .catch(e => {
//        console.log('errord')
//        console.log(e)
//        return Rx.Observable.of(state => Object.assign(state, {error: e.message}))
//      })
//export const login = (creds) => {
//  login$.next(creds)
//  return login$
//}

