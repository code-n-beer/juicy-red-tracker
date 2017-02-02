const API = 'http://' + location.hostname + ':3002/api'
export function POST(endpoint, data) {
  console.log('postink!')
  return fetch(API + endpoint, {
    method: 'post',
    mode: 'cors',
    body: JSON.stringify(data),
    headers: new Headers({
      'Content-Type': 'application/json',
      'accesstoken' : localStorage.getItem('accesstoken')
    })
  }).then(res => res.json())
}

export function GET(endpoint) {
  console.log('gettink! ' + endpoint)
  return fetch(API + endpoint, {
    headers: new Headers({
      'Content-Type': 'application/json',
      'accesstoken' : localStorage.getItem('accesstoken')
    })
  }).then(res => res.json())
}
