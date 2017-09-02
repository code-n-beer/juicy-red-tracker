import Bacon from 'baconjs'

const API = `${location.protocol}//${location.hostname}:3002/api`
console.log(API)

export const POST = (endpoint, data) =>
  Bacon.fromPromise(fetch(API + endpoint, {
    method: 'post',
    mode: 'cors',
    body: JSON.stringify(data),
    headers: new Headers({
      'Content-Type': 'application/json',
      'accesstoken' : localStorage.getItem('accesstoken')
    })
  }).then(res => res.ok ? res.json() : Bacon.Error("Login fail")).catch(e => Bacon.Error("POST crashed")))

export const GET = (endpoint) =>
  Bacon.fromPromise(fetch(API + endpoint, {
    headers: new Headers({
      'Content-Type': 'application/json',
      'accesstoken' : localStorage.getItem('accesstoken')
    })
  }).then(res => res.json()))
