import React from 'react';
import { Link } from 'react-router';
import Cycle from 'cycle-react';
import Rx from 'rx';
import { DOM } from 'rx-dom';

/*
class Testink extends React.Component {
	render() {
		return <div> Cheers { this.props.test } </div>;
	}
}
*/

const List = Cycle.component( 'List', ( ints, props ) => {
	return props.get( 'items' ).map( items =>
		<ul>
			{ items.map( ( item, idx ) => {
				console.log( item.name );
				console.log( idx );
				return <li key={ idx }> { item.name } </li>
			} ) }
		</ul>
	);
} );

const Clicker = Cycle.component( 'Counter', ( interactions ) => {
	return Rx.Observable.merge(
		interactions.get( 'plus' )
		.map( () => 1 ),

		interactions.get( 'minus' )
		.map( () => -1 )
	)
	.scan( ( acc, i ) => acc + i )
	.startWith( 0 )
	.map( i => {
		return <div>
			<p> { i } </p>
			<button onClick={ interactions.listener( 'plus' ) }> plus one </button>
			<button onClick={ interactions.listener( 'minus' ) }> minus one </button>
		</div>
	} );
} );

const User = Cycle.component( 'User', () => {
	return DOM.getJSON( 'http://localhost:3000/api/user/dummy' )
	//.startWith( { message: 'Loading' } )
	//.map( obj => Object.keys( obj ) )
	.map( user => {
		console.log( user.category );
		return <div>
			<h1> { user .email } </h1>
			<List items={ user.category }/>
		</div>
	} );
} );

export default class Home extends React.Component {
	render() {
		return (
			<div>
				<div> <Link to="/about"> About </Link> </div>
				<div> <Link to="/map"> Map </Link> </div>
				<Clicker />
				<User />
				<p> </p>
				<p></p>
			</div>
		);
	}
}
