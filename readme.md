Optional fields marked as OPTIONAL.

New user: POST /api/user

Returns: List of ids, hopefully just one. Passwords are stored as plaintext because why not.
```
  {
      "email": "asdfoo@bar.fi",
      "password": "Foobar123"
  }
```

Authenticate user: POST /api/session

Returns: Access token that you have to set to all subsequent requests as a header named accesstoken. It might also tell you to go fork yourself.
```
  {
      "email": "asdfoo1@bar.fi",
      "password": "Foobar123"
  }
```

Log out: DELETE /api/session

Return: Message stating you have logged out. It will only delete the current session token making it obsolete in the future.


Add a category for user: POST /api/user/:userId/category

Returns: List of ids.
```
  {
      "name": "Vapaa-aika"
  }
```

Add a task into some category for the current user: POST /api/user/3/category/6/task

Note: The relation is recursive, a top level task (e.g., Work, Univesity) has a category_id, but if it is supposed to be a sub-task, it will also have a task_id. We leave it to the client to manage how to show this data for the user.

Returns: List of ids.
```
  {
      "name": "Punttisali",
      "task_id": 2 (OPTIONAL)
  }
```
