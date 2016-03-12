var pg = require('pg');
var express = require('express');
var bodyParser = require('body-parser');
var uuid = require('node-uuid');
var app = express();
var knex = require('knex')({
  client: 'pg',
  connection: process.env.DATABASE_URL
});

app.use(bodyParser.json());

pg.defaults.ssl = true;

function belongsToUser(user, tableName, id) {
  return new Promise((resolve, reject) => {
    knex(tableName)
      .where({user_id: user.id, id: id})
      .then((resp) => {
        resp.length > 0 ? resolve(true) : resolve(false);
      });
  });
}

function isAuthed(req) {
  const token = req.headers.accesstoken;
  return new Promise((resolve, reject) => {
    if (!token) {
      resolve(false);
    } else {
      knex('session')
        .where({token: token})
        .then((resp) => {
          if (resp.length > 0) {
            knex('user')
              .where({
                id: resp[0].user_id
              })
              .then((user) => {
                resolve(user[0]);
              });
          } else {
            resolve(false);
          }
        });
      }
  });
}

app.post('/api/user/:userId/category/:categoryId/task', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        const task = {
          category_id: req.params.categoryId,
          task_id: req.body.task_id || undefined,
          name: req.body.name,
          user_id: user.id
        };
        belongsToUser(user, 'category', req.params.categoryId)
          .then((doesBelong) => {
            if (doesBelong) {
              knex('task')
                .returning('id')
                .insert(task)
                .then((rows) => {
                  res.json(rows);
                });
            } else {
              res.json({error: 'This category belongs to someone else'});
            }
          });
      }
    });
});

app.delete('/api/session', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        knex('session')
          .where({token: req.headers.accesstoken})
          .del()
          .then(() => {
            res.json({message: 'session deleted'});
          });
      }
    });
});

app.post('/api/user/:userId/category', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        knex('category')
          .returning('id')
          .insert({
            user_id: user.id,
            name: req.body.name
          })
          .then((rows) => {
            res.json(rows);
          });
      } else {
        res.status(403).json({error: 'Not authed brada'});
      }
    });
});

app.post('/api/user', (req, res) => {
  knex('user')
    .returning('id')
    .insert({
      email: req.body.email,
      password: req.body.password
    })
    .then((rows) => {
      res.json(rows);
    });
});

app.post('/api/session', (req, res) => {
  knex('user')
    .where({
      email: req.body.email,
      password: req.body.password
    })
    .then((resp) => {
      if (resp.length < 1) res.status(403).json({error: "Non-kosher e-mail / password combination"});
      resp.forEach((user) => {
        const token = uuid.v4();
        knex('session')
          .insert({
            user_id: user.id,
            token: token
          })
          .then(() => {
            res.json({token: token})
          });
      });
    });
});

app.listen(process.env.PORT || 3000 , () => {
  console.log('Example app listening on port 3000!');
});
