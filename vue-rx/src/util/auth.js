import rest from './rest.js'
export const login = (email, password) => {
  return rest.POST('/session', {email, password})
}
export const logout = () => {
  // NYI
}
