import Rx from 'rxjs/Rx'
import isEqual from 'lodash.isequal'

const initialStateObserver = Rx.Observable.of({token: localStorage.getItem('accesstoken')})
      .map((obj) => state => Object.assign({}, state, obj))

const stateObserver$ = new Rx.BehaviorSubject(initialStateObserver)
      .scan((stateObservable, newObservable) => Rx.Observable.merge(stateObservable, newObservable))

export const newStateObservable = (observable) => {
  const sub = new Rx.BehaviorSubject({})
        .map((obj) => state => Object.assign({}, state, obj))
  observable.subscribe(sub)
  stateObserver$.next(sub)
}

// Don't put initial state here because it's not within an observable
// so the stateObserver$ switchMap below will not listen to it after
// first other observer comes in
export const state$ = new Rx.BehaviorSubject({})

const reducer = stateObserver$
      .switchMap(x => x)
      .scan((state, fn) => fn(state), {})
      .distinctUntilChanged(isEqual)
      .do(e => console.log(e))

reducer.subscribe(state$)
