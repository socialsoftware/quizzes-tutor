import { mount } from '@vue/test-utils';
import { afterEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import { FraudScores } from '@/models/management/fraud/FraudScores';
import { QuizFraudInformation } from '@/models/management/fraud/QuizFraudInformation';
import { UserFraudSpeed } from '@/models/management/fraud/UserFraudSpeed';
import { Quiz } from '@/models/management/Quiz';

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import ShowQuizFraudScoresDialog from '@/views/teacher/fraud/ShowQuizFraudScoresDialog.vue';

const info = (username: string, name = username) => ({ username, name, user_id: `id-${username}` });
const timeScores = () => new FraudScores([{ userInfo: info('ana'), score: 1.5 } as any, { userInfo: info('rui'), score: 0.2 } as any]);
const speed = (username: string, level: string, points: number, reasons: string[]) =>
  new UserFraudSpeed({ userInfo: info(username), answered: 8, tooFast: 6, meanSeconds: 3.2, points, level, reasons } as UserFraudSpeed);

describe('QuizFraudInformation with speed', () => {
  test('puts the speed of each student next to their other scores', () => {
    const result = new QuizFraudInformation(
      timeScores(), new FraudScores([]), new FraudScores([]),
      [speed('ana', 'HIGH', 3, ['6 of 8 answers came in less time than it takes to read'])]
    );

    expect(result.users['ana'].scoreTime).toBe(1.5);
    expect(result.users['ana'].speedLevel).toBe('HIGH');
    expect(result.users['ana'].speedPoints).toBe(3);
    expect(result.users['ana'].speedReasons).toEqual(['6 of 8 answers came in less time than it takes to read']);
    expect(result.users['ana'].speedTooFast).toBe(6);
    expect(result.users['rui'].speedLevel).toBeUndefined();
  });

  test('a student only the speed analysis knows about is listed too', () => {
    const result = new QuizFraudInformation(timeScores(), new FraudScores([]), new FraudScores([]), [speed('eva', 'LOW', 1, ['x'])]);

    expect(Object.keys(result.users).sort()).toEqual(['ana', 'eva', 'rui']);
  });

  test('without speed data the old scores are as they were', () => {
    const result = new QuizFraudInformation(timeScores(), new FraudScores([]), new FraudScores([]));

    expect(Object.keys(result.users)).toEqual(['ana', 'rui']);
    expect(result.users['ana'].speedLevel).toBeUndefined();
  });
});

describe('ShowQuizFraudScoresDialog speed columns', () => {
  afterEach(() => {
    document.body.innerHTML = '';
  });

  const mountDialog = (speedEntries: UserFraudSpeed[]) => {
    const quiz = { title: 'Midterm' } as Quiz;
    const information = new QuizFraudInformation(timeScores(), new FraudScores([]), new FraudScores([]), speedEntries);
    return mount(ShowQuizFraudScoresDialog, {
      props: { dialog: true, quizFraudInformation: information, quiz },
      global: { plugins: [createVuetify({ components, directives })], stubs: { FraudViolin: true } },
      attachTo: document.body,
    });
  };

  test('shows the level and the reasons in words, and says it is never proof', () => {
    mountDialog([speed('ana', 'HIGH', 3, ['6 of 8 answers came in less time than it takes to read'])]);

    const text = document.body.textContent!;
    expect(text).toContain('Speed');
    expect(text).toContain('never proof');
    expect(text).toContain('Screenshots cannot be detected');
    expect(document.body.querySelectorAll('[data-cy="SpeedLevel"]')).toHaveLength(1);
    expect(document.body.querySelector('[data-cy="SpeedLevel"]')!.textContent).toContain('HIGH');
    expect(document.body.querySelector('[data-cy="SpeedReasons"]')!.textContent).toContain('6 of 8 answers came in less time');
  });

  test('a student with no flag gets no chip and no reasons', () => {
    mountDialog([speed('ana', 'NONE', 0, [])]);

    expect(document.body.querySelectorAll('[data-cy="SpeedLevel"]')).toHaveLength(0);
    expect(document.body.querySelectorAll('[data-cy="SpeedReasons"]')).toHaveLength(0);
  });
});
