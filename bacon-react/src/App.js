import React from 'baret'
import styles from './App.css'

import User from './components/user/component.js'

const App = () =>
  <div className={styles.App}>
    <User></User>
    <div className={styles.header}>
      <h2>Juice da pomo</h2>
    </div>
    <p className={styles.intro}>
    </p>

  </div>


export default App
