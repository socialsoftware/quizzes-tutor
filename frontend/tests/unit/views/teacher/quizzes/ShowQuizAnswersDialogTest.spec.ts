import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import { QuizAnswers } from '@/models/management/QuizAnswers';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import ShowQuizAnswersDialog from '@/views/teacher/quizzes/ShowQuizAnswersDialog.vue';

const answers = () =>
  new QuizAnswers({
    correctSequence: ['A'],
    timeToSubmission: 0,
    quizAnswers: [
      { id: 1, name: 'Ana', username: 'ana', creationDate: '2030-01-01T10:00:00Z', answerDate: '2030-01-01T10:10:00Z', fraud: false, sessionOpenedAt: '2030-01-01T10:00:00Z', blockedOpenings: 2, questionAnswers: [] },
      { id: 2, name: 'Rui', username: 'rui', creationDate: '2030-01-01T10:00:00Z', answerDate: '2030-01-01T10:10:00Z', fraud: false, sessionOpenedAt: null, blockedOpenings: 0, questionAnswers: [] },
    ],
  } as any);

describe('ShowQuizAnswersDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async (quizId?: number) => {
    const wrapper = mount(ShowQuizAnswersDialog, {
      props: { dialog: true, quizAnswers: answers(), conclusionDate: '2030-01-01T11:00:00Z', quizId },
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  test('shows how many times a student was refused, and offers to let in only who opened the quiz', async () => {
    await mountDialog(7);

    expect(document.body.querySelector('[data-cy="BlockedOpenings"]')!.textContent).toContain('2 refused');
    expect(document.body.querySelectorAll('[data-cy="ReopenQuizAnswer"]')).toHaveLength(1);
  });

  test('letting a student open it again asks first, then tells the server', async () => {
    const reopen = vi.spyOn(RemoteServices, 'reopenQuizAnswer').mockResolvedValue(undefined);
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    await mountDialog(7);
    const button = () => document.body.querySelector('[data-cy="ReopenQuizAnswer"]') as HTMLElement;

    button().click();
    await flushPromises();
    expect(reopen).not.toHaveBeenCalled();

    confirmation.mockReturnValue(true);
    button().click();
    await flushPromises();

    expect(reopen).toHaveBeenCalledWith(7, 1);
    expect(document.body.querySelector('[data-cy="ReopenQuizAnswer"]')).toBeNull();
  });

  test('a failure is reported and the student stays as they were', async () => {
    vi.spyOn(RemoteServices, 'reopenQuizAnswer').mockRejectedValue('not allowed');
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    await mountDialog(7);

    (document.body.querySelector('[data-cy="ReopenQuizAnswer"]') as HTMLElement).click();
    await flushPromises();

    expect(store.setError).toHaveBeenCalledWith('not allowed');
    expect(document.body.querySelector('[data-cy="ReopenQuizAnswer"]')).not.toBeNull();
  });

  test('without the quiz there is nothing to reopen', async () => {
    await mountDialog(undefined);

    expect(document.body.querySelector('[data-cy="ReopenQuizAnswer"]')).toBeNull();
  });
});
