import Rx from 'rxjs/Rx'

import rest from './rest.js'

export const userData$ = new Rx.BehaviorSubject({token: localStorage.getItem('accesstoken')})

const addUserProp = (obj) => userData$.next(Object.assign(userData$.getValue(), obj))

export const login = (email, password) => {
  return rest.POST('/session', {email, password}).then(res => {
    if (res.token) {
      localStorage.setItem('accesstoken', res.token)
      addUserProp({token: res.token})
    }
    return res
  })
}
export const logout = () => {
  // NYI
}

// If access token state changes, update stream
const storage = Rx.Observable.fromEvent(window, 'storage')
storage.subscribe(_ => {
  addUserProp({token: localStorage.getItem('accesstoken')})
})

