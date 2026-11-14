import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import { ExecutionFraudSpeed } from '@/models/management/fraud/ExecutionFraudSpeed';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import SpeedReportDialog from '@/views/teacher/fraud/SpeedReportDialog.vue';

const report = () =>
  new ExecutionFraudSpeed({
    quizzesAnalysed: 3,
    users: [
      {
        userInfo: { username: 'ana', name: 'Ana', user_id: '5' },
        quizzesAnalysed: 3,
        flaggedQuizzes: 2,
        totalPoints: 5,
        blockedOpenings: 1,
        quizzes: [
          { quizId: 9, title: 'Quiz 9', answered: 5, tooFast: 3, meanSeconds: 12.5, points: 3, level: 'HIGH', reasons: ['almost the same time on every question'] },
          { quizId: 8, title: 'Quiz 8', answered: 5, tooFast: 0, meanSeconds: 22, points: 1, level: 'LOW', reasons: [] },
        ],
      },
    ],
  } as any);

describe('SpeedReportDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async () => {
    const wrapper = mount(SpeedReportDialog, {
      props: { dialog: true },
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  test('lists the students the quizzes flagged, with in how many of the quizzes and why', async () => {
    vi.spyOn(RemoteServices, 'getExecutionSpeedReport').mockResolvedValue(report());
    await mountDialog();

    expect(document.body.querySelector('[data-cy="SpeedReportSummary"]')!.textContent).toContain('3 quiz(zes) analysed, 1 student(s) listed');
    const student = document.body.querySelector('[data-cy="SpeedStudent"]')!;
    expect(student.textContent).toContain('Ana');
    expect(student.querySelector('[data-cy="FlaggedQuizzes"]')!.textContent).toContain('flagged in 2 of 3 quizzes');
    expect(student.querySelector('[data-cy="TotalPoints"]')!.textContent).toContain('5 point(s)');
    expect(student.querySelector('[data-cy="RefusedOpenings"]')!.textContent).toContain('1 refused opening(s)');
  });

  test('opening a student shows each quiz with its level and the reasons in words', async () => {
    vi.spyOn(RemoteServices, 'getExecutionSpeedReport').mockResolvedValue(report());
    await mountDialog();

    (document.body.querySelector('.v-expansion-panel-title') as HTMLElement).click();
    await flushPromises();

    const quizzes = Array.from(document.body.querySelectorAll('[data-cy="SpeedQuiz"]'));
    expect(quizzes.map((quiz) => quiz.querySelector('.font-weight-medium')!.textContent)).toEqual(['Quiz 9', 'Quiz 8']);
    expect(quizzes[0].textContent).toContain('HIGH');
    expect(quizzes[0].textContent).toContain('almost the same time on every question');
  });

  test('says so when no student stands out', async () => {
    vi.spyOn(RemoteServices, 'getExecutionSpeedReport').mockResolvedValue(new ExecutionFraudSpeed({ quizzesAnalysed: 2, users: [] } as any));
    await mountDialog();

    expect(document.body.querySelector('[data-cy="SpeedReportEmpty"]')).not.toBeNull();
  });

  test('says these are signs and not proof', async () => {
    vi.spyOn(RemoteServices, 'getExecutionSpeedReport').mockResolvedValue(report());
    await mountDialog();

    expect(document.body.querySelector('[data-cy="SpeedReportDialog"]')!.textContent).toContain('never proof');
  });

  test('a failure is reported and the dialog closes', async () => {
    vi.spyOn(RemoteServices, 'getExecutionSpeedReport').mockRejectedValue('the fraud service did not answer');
    const wrapper = await mountDialog();

    expect(store.setError).toHaveBeenCalledWith('the fraud service did not answer');
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });
});
