import React from 'baret'
import styles from './App.css'

import User from './components/user.js'
import Clock from './components/clock.js'

const App = () =>
  <div className={styles.App}>
    <User/>
    <div className={styles.header}>
      <h2>Juice da pomo</h2>
    </div>
    <p className={styles.intro}>
    </p>

    <Clock/>

  </div>


export default App
