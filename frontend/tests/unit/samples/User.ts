import User from '@/models/user/User';

export const userZe = new User({
  id: 1,
  name: 'José',
  username: 'jj',
  email: 'j@j.j',
  role: 'STUDENT',
  admin: false,
  active: true,
  creationDate: '2021',
  lastAccess: '2021',
});

export const userBeto = new User({
  id: 2,
  name: 'Beto',
  username: 'jj',
  email: 'j@j.j',
  role: 'STUDENT',
  admin: false,
  active: true,
  creationDate: '2021',
  lastAccess: '2021',
});
