import rest from './auth.js'
const login = (email, password) => {
  rest.POST('/api/session', {email, password})
}
const logout = () => {
  // NYI
}
export default {
  login, logout
}
