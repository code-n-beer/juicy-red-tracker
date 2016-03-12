CREATE TABLE "user" (
  id SERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL
);

CREATE TABLE category (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL references "user" ON DELETE CASCADE,
  name TEXT NOT NULL
);

CREATE TABLE task (
  id SERIAL PRIMARY KEY,
  category_id INT references category ON DELETE CASCADE,
  task_id INT references task ON DELETE CASCADE,
  name TEXT NOT NULL
);

CREATE TABLE goal (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL references "user" ON DELETE CASCADE,
  task_id INT NOT NULL references task ON DELETE CASCADE,
  start_at TIMESTAMP NOT NULL,
  end_at TIMESTAMP NOT NULL
);

CREATE TABLE pomodoro (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL references "user" ON DELETE CASCADE,
  task_id INT NOT NULL references task ON DELETE CASCADE,
  minutes INT DEFAULT 25,
  created_at TIMESTAMP,
  success BOOLEAN DEFAULT false
);
