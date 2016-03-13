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

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

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

app.post('/api/user/category/:categoryId/task', (req, res) => {
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
              res.status(403).json({error: 'This category belongs to someone else'});
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

app.post('/api/user/category', (req, res) => {
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

app.post('/api/user/task/:taskId/pomodoro', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        belongsToUser(user, 'task', req.params.taskId)
          .then((doesBelong) => {
            if (doesBelong) {
              const pomodoro = {
                user_id: user.id,
                task_id: req.params.taskId,
                minutes: req.body.minutes,
                created_at: new Date(),
                success: req.body.success
              };
              knex('pomodoro')
                .returning('id')
                .insert(pomodoro)
                .then((rows) => {
                  res.json(rows);
                });
            } else {
              res.status(403).json({error: 'This task doesn\'t belong to you'});
            }
          });
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

app.post('/api/user/goal', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        const goal = {
          user_id: user.id,
          task_id: req.body.task_id,
          amount: req.body.amount,
          start_at: req.body.start_at,
          end_at: req.body.end_at
        };
        knex('goal')
          .returning('id')
          .insert(goal)
          .then((rows) => {
            res.json(rows);
          });
      }
    });
});

function treeify(task, tasks, parents) {
  var parent = tasks.filter((t) => task.task_id === t.id)[0];
  if (!parent) {
    if (parents.indexOf(task) === -1) {
      parents.push(task);
    }
    return;
  }
  if (!parent.task) {
    parent.task = [];
  }
  parent.task.push(task);
  if (parent.task_id) {
    treeify(parent, tasks, parents);
  } else {
    if (parents.indexOf(parent) === -1) {
      parents.push(parent);
    }
  }
}

app.get('/api/user/task', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        knex('task')
          .where({user_id: user.id})
          .then((tasks) => {
            if (!req.query.flatten) {
              const leafNodes = tasks.filter((t) => {
                return tasks.filter((possibleChild) => possibleChild.task_id === t.id).length === 0
              });
              var parents = [];
              leafNodes.forEach((ln) => {
                treeify(ln, tasks, parents);
              });
              res.json(parents);
            } else {
              res.json(tasks);
            }
          });
        }
    });
});

app.get('/api/user/category', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        knex('category')
          .where({user_id: user.id})
          .then((categories) => {
            res.json(categories)
          });
      }
    });
});

app.get('/api/user/pomodoro', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        knex('pomodoro')
          .where({user_id: user.id})
          .then((pomodoros) => {
            res.json(pomodoros);
          });
      }
    });
});

app.get('/api/user', (req, res) => {
  isAuthed(req)
    .then((user) => {
      if (user) {
        knex('goal')
          .where({user_id: user.id})
          .then((goals) => {
            knex('category')
              .where({user_id: user.id})
              .then((categories) => {
                knex('task')
                  .where({user_id: user.id})
                  .then((tasks) => {
                    const leafNodes = tasks.filter((t) => {
                      return tasks.filter((possibleChild) => possibleChild.task_id === t.id).length === 0
                    });
                    var parents = [];
                    leafNodes.forEach((ln) => {
                      treeify(ln, tasks, parents);
                    });
                    parents.forEach((p) => {
                      categories.filter((c) => {
                        if (!c.tasks) {
                          c.tasks = [];
                        }
                        return p.category_id === c.id;
                      })[0].tasks.push(p);
                    });
                    knex('pomodoro')
                      .where({user_id: user.id})
                      .then((pomodoros) => {
                        delete user.password
                        user.pomodoros = pomodoros;
                        user.categories = categories;
                        user.goals = goals;
                        res.json(user);
                      });
                  });
              });
          });
      }
    });
});

app.listen(process.env.PORT || 3000 , () => {
  console.log('Example app listening on port 3000!');
});

app.get('/api/user/dummy', (req, res) => {
  res.json({
    id: 1337,
    email: 'teppo@testaaja.fi',
    category: [
      {
        id: 1,
        name: 'Work',
        task: [
          {
            id: 1,
            name: 'Epic Tomato Project',
            category_id: 1,
            task_id: false,
            task: [
              {
                id: 3,
                name: 'Fetch the hobbits',
                category_id: 1,
                task_id: 1,
                task: []
              }
            ]
          },
          {
            id: 2,
            name: 'Legacy Projekt',
            category_id: 1,
            task_id: false,
            task: [
              {
                id: 4,
                name: 'Write Pascal',
                category_id: 1,
                task_id: 2,
                task: []
              }
            ]
          }
        ]
      },
      {
        id: 2,
        name: 'Study',
        task: [
          {
            id: 5,
            name: 'Park Chemistry',
            category_id: 2,
            task_id: false,
            task: [
              {
                id: 7,
                name: 'Read Valdemar',
                category_id: 2,
                task_id: 5,
                task: []
              }
            ]
          },
          {
            id: 6,
            name: 'Pronouns 101',
            category_id: 2,
            task_id: false,
            task: [
              {
                id: 8,
                name: 'Study the first 500',
                category_id: 1,
                task_id: 6,
                task: []
              }
            ]
          }
        ]
      }
    ],
    pomodoro: [
      {
        id: 1,
        task_id: 3,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      },
      {
        id: 2,
        task_id: 3,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      },
      {
        id: 3,
        task_id: 3,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      },
      {
        id: 4,
        task_id: 3,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      },
      {
        id: 5,
        task_id: 4,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      },
      {
        id: 6,
        task_id: 7,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      },
      {
        id: 7,
        task_id: 7,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      },
      {
        id: 8,
        task_id: 8,
        minutes: 25,
        created_at: '2016-03-12T19:45:12.821Z',
        success: true
      }
    ],
    goal: [
      {
        id: 1,
        task_id: 1,
        amount: 125,
        start_at: '2016-03-07T00:00:00.000Z',
        end_at: '2016-03-13T21:59:00.000Z'
      },
      {
        id: 2,
        task_id: 2,
        amount: 75,
        start_at: '2016-03-07T00:00:00.000Z',
        end_at: '2016-03-13T21:59:00.000Z'
      },
      {
        id: 3,
        task_id: 5,
        amount: 100,
        start_at: '2016-03-07T00:00:00.000Z',
        end_at: '2016-03-13T21:59:00.000Z'
      },
      {
        id: 4,
        task_id: 6,
        amount: 150,
        start_at: '2016-03-07T00:00:00.000Z',
        end_at: '2016-03-13T21:59:00.000Z'
      }
    ]
  });
});
