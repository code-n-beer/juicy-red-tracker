import Rx from 'rxjs/Rx'

const stateObserver$ = new Rx.Subject()
      .scan((stateObservable, newObservable) => Rx.Observable.merge(stateObservable, newObservable))

export const newStateObservable = (observable) => {
  const sub = new Rx.Subject()
  observable.subscribe(sub)
  stateObserver$
    .next(sub
          .map((obj) => state => Object.assign({}, state, obj)))
}

export const state$ = new Rx.BehaviorSubject({token: localStorage.getItem('accesstoken')})

const reducer = stateObserver$
      .switchMap(x => x)
      .scan((state, fn) => fn(state), {})

reducer.subscribe(state$)
