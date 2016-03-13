var pg = require('pg');
var _ = require('lodash');
var knex = require('knex')({
  client: 'pg',
  connection: process.env.DATABASE_URL
});

pg.defaults.ssl = true;

function getRandomIntInclusive(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function generateGoalsForTask(taskId) {
  var date = new Date('2016-01-01');
  return _.range(12).map((monthNumber) => {
    date.setMonth(monthNumber);
    return goal = {
      user_id: 1,
      task_id: taskId,
      start_at: date,
      amount: (getRandomIntInclusive(30, 60) + getRandomIntInclusive(-10, 10)) * 25,
      end_at: new Date(2016, monthNumber + 1, 0)
    };
  });
}

var user = {email: 'jerry@cnb.fi', password: 'Foobar123'};

var study = {user_id: 1, name: 'Study'};
var work = {user_id: 1, name: 'Work'};
var sport = {user_id: 1, name: 'Sport'};

var categories = [study, work, sport];

var algebra = {user_id: 1, name: 'Algebra', category_id: 1};
var daa = {user_id: 1, name: 'Design and Analysis of Algorithms', category_id: 1};
var cartoon = {user_id: 1, name: 'Drawing Cartoons', category_id: 1};

var pomApi = {user_id: 1, name: 'Pomodoro API', category_id: 2};
var pomFront = {user_id: 1, name: 'Pomodoro Front', category_id: 2};
var pomMob = {user_id: 1, name: 'Mobile Clients for Pomodoro', category_id: 2};

var gym = {user_id: 1, name: 'Gym', category_id: 3};
var swim = {user_id: 1, name: 'Swim', category_id: 3};
var run = {user_id: 1, name: 'Run', category_id: 3};

var faces = {user_id: 1, name: 'Faces', category_id: 1, task_id: 3};
var hands = {user_id: 1, name: 'Hands', category_id: 1, task_id: 3};

var ios = {user_id: 1, name: 'iOS', category_id: 2, task_id: 6};
var android = {user_id: 1, name: 'Android', category_id: 2, task_id: 6};

var tasks = [algebra, daa, cartoon, pomApi, pomFront, pomMob, gym, swim, run, faces, hands, ios, android];
var leafNodes = [1, 2, 4, 5, 7, 8, 9, 10, 11, 12, 13];

var goals = leafNodes.map((ln) => generateGoalsForTask(ln));
var pomodoros = leafNodes.map((ln) => {
  var date = new Date('2016-01-01');
  var yearlyPomodoros = _.range(52).map(() => {
    var weeklyPomodoros = _.range(getRandomIntInclusive(5, 17)).map(() => {
      return {
        user_id: 1,
        task_id: ln,
        minutes: 25,
        created_at: date.toJSON(),
        success: getRandomIntInclusive(1, 4) <= 3 ? true : false
      };
    });
    date.setHours(168);
    return weeklyPomodoros;
  });
  return yearlyPomodoros;
});

knex('user')
  .insert(user)
  .then(() => {
    knex.batchInsert('category', categories)
      .then(() => {
        knex.batchInsert('task', tasks)
          .then(() => {
            goals.forEach((g) => {
              knex.batchInsert('goal', g);
            });
            pomodoros.forEach((taskPoms) => {
              taskPoms.forEach((weeklyPoms) => {
                knex.batchInsert('pomodoro', weeklyPoms);
              });
            });
            console.log('Inserted everything zomg');
          });
      });
  });
