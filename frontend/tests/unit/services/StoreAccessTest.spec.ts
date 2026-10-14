import { beforeEach, describe, expect, test } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useStore } from '@/store';
import AuthUser from '@/models/user/AuthUser';

const user = (role: string, questionGeneration: boolean, admin = false) =>
  new AuthUser({ id: 1, name: 'x', username: 'x', role, admin, questionGeneration, courses: {} } as any);

describe('who is offered question generation', () => {
  beforeEach(() => setActivePinia(createPinia()));

  const storeWith = (authUser: AuthUser) => {
    const store = useStore();
    store.token = 'a token';
    store.user = authUser;
    return store;
  };

  test('a teacher who logged in with an account', () => {
    expect(storeWith(user('TEACHER', true)).canGenerateQuestions).toBe(true);
  });

  test('not the demo teacher, nor students', () => {
    expect(storeWith(user('TEACHER', false)).canGenerateQuestions).toBe(false);
    expect(storeWith(user('STUDENT', true)).canGenerateQuestions).toBe(false);
  });

  test('a session saved before the flag existed is not offered it', () => {
    const old = new AuthUser({ id: 1, name: 'x', username: 'x', role: 'TEACHER', admin: false, courses: {} } as any);
    expect(storeWith(old).canGenerateQuestions).toBe(false);
  });

  test('the model settings are only for administrators of the whole Tutor, not the demo administrator', () => {
    expect(storeWith(user('DEMO_ADMIN', false)).isSystemAdmin).toBe(false);
    expect(storeWith(user('DEMO_ADMIN', false)).isAdmin).toBe(true);
    expect(storeWith(user('TEACHER', true, true)).isSystemAdmin).toBe(true);
  });
});
