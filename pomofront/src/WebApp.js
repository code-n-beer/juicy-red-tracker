import Cycle from 'cycle-react';
import ReactDOM from 'react-dom';
import React from 'react';  // eslint-disable-line no-unused-vars
import Application from './containers/application';
import { browserHistory } from 'react-router';
import Rx from 'rx';

const Main = Cycle.component( 'Main', () => {
	return Rx.Observable.just( <Application history={ browserHistory }/> );
} );

//ReactDOM.render.applyToDOM( '#app', computer );
ReactDOM.render( <Main />, document.querySelector( '#app' ) );
