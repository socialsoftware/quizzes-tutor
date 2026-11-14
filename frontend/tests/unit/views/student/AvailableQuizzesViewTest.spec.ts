import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import StatementQuiz from '@/models/statement/StatementQuiz';

vi.mock('@/store', () => ({ useStore: vi.fn() }));
const push = vi.fn();
vi.mock('vue-router', async (importOriginal) => ({ ...(await importOriginal<typeof import('vue-router')>()), useRouter: () => ({ push }) }));

import AvailableQuizzesView from '@/views/student/AvailableQuizzesView.vue';

const quiz = (overrides: Record<string, unknown> = {}) =>
  new StatementQuiz({
    id: 7,
    quizAnswerId: 70,
    title: 'Midterm',
    availableDate: '2030-01-01T10:00:00Z',
    conclusionDate: '2030-01-01T11:00:00Z',
    questions: [],
    answers: [],
    ...overrides,
  } as any);

describe('AvailableQuizzesView', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn(), setStatementQuiz: vi.fn() };
    (useStore as any).mockReturnValue(store);
    push.mockReset();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  const mountView = async (quizzes: StatementQuiz[]) => {
    vi.spyOn(RemoteServices, 'getAvailableQuizzes').mockResolvedValue(quizzes);
    const wrapper = mount(AvailableQuizzesView, { global: { plugins: [createVuetify({ components, directives })] } });
    await flushPromises();
    return wrapper;
  };

  test('says which quizzes open only once and which need the Safe Exam Browser', async () => {
    const wrapper = await mountView([quiz({ singleSession: true, safeBrowserOnly: true })]);

    expect(wrapper.find('[data-cy="SingleSessionBadge"]').exists()).toBe(true);
    expect(wrapper.find('[data-cy="SafeBrowserBadge"]').exists()).toBe(true);
  });

  test('a quiz that opens only once asks first, and does not start if the student says no', async () => {
    const start = vi.spyOn(RemoteServices, 'startQuiz').mockResolvedValue(quiz());
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountView([quiz({ singleSession: true })]);

    await wrapper.find('.list-row').trigger('click');
    await flushPromises();

    expect(confirmation.mock.calls[0][0]).toContain('only be opened once');
    expect(start).not.toHaveBeenCalled();

    confirmation.mockReturnValue(true);
    await wrapper.find('.list-row').trigger('click');
    await flushPromises();

    expect(start).toHaveBeenCalledWith(7);
    expect(push).toHaveBeenCalledWith({ name: 'solve-quiz' });
  });

  test('an ordinary quiz starts without asking', async () => {
    const start = vi.spyOn(RemoteServices, 'startQuiz').mockResolvedValue(quiz());
    const confirmation = vi.spyOn(window, 'confirm');
    const wrapper = await mountView([quiz()]);

    await wrapper.find('.list-row').trigger('click');
    await flushPromises();

    expect(confirmation).not.toHaveBeenCalled();
    expect(start).toHaveBeenCalledWith(7);
  });

  test('a refusal of the server is shown', async () => {
    vi.spyOn(RemoteServices, 'startQuiz').mockRejectedValue('This quiz can only be opened once and it was already opened');
    const wrapper = await mountView([quiz()]);

    await wrapper.find('.list-row').trigger('click');
    await flushPromises();

    expect(store.setError).toHaveBeenCalledWith('This quiz can only be opened once and it was already opened');
    expect(push).not.toHaveBeenCalled();
  });
});
