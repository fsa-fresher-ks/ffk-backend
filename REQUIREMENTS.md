# Requirements
- all query API requires:
  - pagination
  - sort
- DB: mysql
- using 3-layer architecture, organize in folders: controller, service, repository
- service must be interface-based
- user custom user detail service to authenticate user in DB
- CORS accept *
- /auth/login and /auth/register must be public API

## Entity: User
- name
- email
- password (at least 8 characters)
- role (admin, customer, staff_account, staff_sale, staff_inventory)
- status (active, inactive)

## API: Auth
- login
- register

## API: User
- create an user
- update an user
- delete an user
- find an user by id
- list all users
- search user by keywork (name, email)
