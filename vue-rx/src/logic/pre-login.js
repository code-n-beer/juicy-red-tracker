import Rx from 'rxjs/Rx'

import {newStateObservable} from '../util/state'

const local = Rx.Observable.of({token: localStorage.getItem('accesstoken')})
newStateObservable(local)
