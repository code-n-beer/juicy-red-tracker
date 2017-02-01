import Rx from 'rxjs/Rx'

import {state$, newStateObservable} from '../util/state'
import {GET} from '../util/rest.js'

const fetchUserData = state$
      .filter(state => !state.userDataFetched && !state.userDataFetching && state.token)
      .map(_ => ({userDataFetching: true, userDataFetched: false}))
      .do(e => console.log(e))

const fetchFinished = state$
      .filter(state => state.userDataFetching)
      .map(state => state.userDataFetching)
//TODO: implement debounce with immediate first release...
// ... or just fix the reason why this is called three times immediately
      .debounceTime(1)
      .flatMap(_ => Rx.Observable.fromPromise(GET('/user/')))
      .map(obj => ({...obj, userDataFetching: false, userDataFetched: true}))

newStateObservable(fetchUserData)
newStateObservable(fetchFinished)
