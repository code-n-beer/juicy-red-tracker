const API = 'localhost:3000/api'
function POST(endpoint, data) {
  return fetch(API + endpoint, {
    method: 'post',
    mode: 'cors',
    body: JSON.stringify(data),
    headers: new Headers({
      'Content-Type': 'application/json',
      'accesstoken' : localStorage.getItem('accesstoken')
    })
  })
}

function GET(endpoint) {
  return fetch(API + endpoint, {
    headers: new Headers({
      'Content-Type': 'application/json',
      'accesstoken' : localStorage.getItem('accesstoken')
    })
  })
}

export default {
  GET, POST
}
