import React from 'baret'
import Bacon from 'baconjs'
//import {register, login} from '../../util/auth.js'

//import {loggedIn$} from '../../util/state.js'
class User extends React.Component {
  //componentWillMount() {
  //  GET('/user/')
  //    .flatMap(res => console.log(res))
  //}
  //this will never work   { Bacon.fromBinder(sink => <Login loginInputChanged={sink}/>) }
                //{Bacon.fromBinder(sink => {
                //  console.log('ohhi')
                //  this.onChange = sink})
                //  .startWith("initial")
                //  .map(text => console.log(this.onChange) || <Input text={text} onChange={this.onChange}/>)
                //}
  constructor(props) {
    super(props)
    this.username = new Bacon.Bus()
    this.password = new Bacon.Bus()
  }
  render() {
    console.log('wat')

    const inputs = this.username.combine(this.password, (user, pass) =>
    [user,pass])
    .map(([user, pass]) => {
      <div>
        <Input text={user} text$={this.username}/>
        <Input text={pass} text$={this.password}/>
      </div>
    })
    console.log('adlkjg;dlkdlkjd;hlfj')
    return <div>
                <p> User Bar </p>
                <button> Login </button>
                <button> Register </button>
                {inputs
    .startWith(['vittu','saatana'])
                }
           </div>
  }
}

//class Login extends React.Component {
//  render = () =>
//    <div>
//      <div> Login:
//        <Input/>
//      </div>
//    </div>
//}

const Input = ({text, text$}) => <input type="text" value={text} onChange={(e) => text$.push(e.target.value)}/>

//class Input extends React.Component {
//  render = ({text, text$}) => <input type="text" value={text} onChange={(e) => text$.push(e.target.value)}/>
//}
export default User
