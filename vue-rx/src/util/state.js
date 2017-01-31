import Rx from 'rxjs/Rx'
import isEqual from 'lodash.isequal'

const initialStateObserver = Rx.Observable.of({})
      .map((obj) => state => Object.assign({}, state, obj))

// Don't put initial state here because it's not within an observable
// so the stateObserver$ switchMap below will not listen to it after
// first other observer comes in
export const state$ = new Rx.BehaviorSubject({})

const reducer = new Rx.BehaviorSubject(initialStateObserver)
      .scan((stateObservable, newObservable) => Rx.Observable.merge(stateObservable, newObservable))
      .do(_ => console.log('state observer des'))
      .do(e => console.log(e))
      .switchMap(x => x)
      .scan((state, fn) => fn(state), {})
      .do(_ => console.log('three'))
      .do(e => console.log(e))
      .distinctUntilChanged(isEqual)
      .do(_ => console.log('four'))
      .do(e => console.log(e))

export const newStateObservable = (observable) => {
  reducer.next(observable
                      .share()
                      .map((obj) => state => Object.assign({}, state, obj)))
}

reducer.subscribe(state$)
