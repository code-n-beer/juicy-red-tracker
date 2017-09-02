import React from 'baret'
import Bacon from 'baconjs'
import {loggedIn$} from '../../util/state.js'
import {login, logout} from '../../util/auth.js'
class User extends React.Component {
  constructor(props) {
    super(props)
    this.username = new Bacon.Bus()
    this.password = new Bacon.Bus()
    this.login = new Bacon.Bus()
    this.register = new Bacon.Bus()
    this.inputs = this.username.combine(this.password, (email, password) => {return {email, password}})
    const login$ = Bacon.when(
      [this.inputs, this.login], creds => login(creds)
    ).flatMap(o => o)
    login$.onError(a => console.log('login failed'))
    login$.onValue(val => loggedIn$.push(val))
  }
  render() {
    const inputs = loggedIn$.combine(this.inputs, (val, {email}) => val && val.token ? [val.token, email] : false)
    .startWith([localStorage.getItem('accesstoken'),localStorage.getItem('email')])
    .map(([token, email])=> {
      if (email && token) {
        localStorage.setItem('accesstoken', token)
        localStorage.setItem('email', email)
        return <div>
          Hello {email}!
          <button onClick={() => logout()}> Logout </button>
        </div>
      } else {
        return <div>
              <button onClick={() => this.login.push('clicked')}> Login </button>
              <button> Register </button>
              <Input text$={this.username}/>
              <Input text$={this.password}/>
        </div>
      }
    })
    return <div>
              {inputs}
           </div>
  }
}

const Input = ({text, text$}) => <input type="text" onChange={(e) => text$.push(e.target.value)}/>

export default User
