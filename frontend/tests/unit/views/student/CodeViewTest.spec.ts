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
vi.mock('vue-router', async (importOriginal) => ({
  ...(await importOriginal<typeof import('vue-router')>()),
  useRouter: () => ({ push }),
  useRoute: () => ({ name: 'code' }),
}));

import CodeView from '@/views/student/CodeView.vue';

const started = () =>
  new StatementQuiz({ id: 7, quizAnswerId: 70, title: 'Midterm', timeToAvailability: null, questions: [], answers: [] } as any);

describe('CodeView', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn(), setStatementQuiz: vi.fn(), currentCourse: { courseExecutionId: 3 } };
    (useStore as any).mockReturnValue(store);
    push.mockReset();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  const enterCode = async (wrapper: ReturnType<typeof mount>) => {
    await wrapper.find('input').setValue('1234');
    await wrapper.find('button').trigger('click');
    await flushPromises();
  };

  test('a quiz that has started is asked for once: asking opens it, and a quiz opened once cannot be opened twice', async () => {
    const ask = vi.spyOn(RemoteServices, 'getQuizByCode').mockResolvedValue(started());
    const wrapper = mount(CodeView, { global: { plugins: [createVuetify({ components, directives })] } });

    await enterCode(wrapper);

    expect(ask).toHaveBeenCalledTimes(1);
    expect(push).toHaveBeenCalledWith({ name: 'solve-quiz' });
    expect(store.setError).not.toHaveBeenCalled();
  });
});
