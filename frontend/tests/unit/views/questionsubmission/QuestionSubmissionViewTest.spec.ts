import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import QuestionSubmissionView from '@/views/questionsubmission/QuestionSubmissionView.vue';

describe('QuestionSubmissionView, the questions written by the AI', () => {
  afterEach(() => vi.restoreAllMocks());

  const open = async (canGenerateQuestions: boolean) => {
    const store = {
      isTeacher: true,
      canGenerateQuestions,
      setLoading: vi.fn(),
      clearLoading: vi.fn(),
      setError: vi.fn(),
    };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getCourseExecutionQuestionSubmissions').mockResolvedValue([]);
    vi.spyOn(RemoteServices, 'getTopics').mockResolvedValue([]);
    const generations = vi.spyOn(RemoteServices, 'getQuestionGenerations').mockResolvedValue([]);

    mount(QuestionSubmissionView, { global: { plugins: [createVuetify({ components, directives })] } });
    await flushPromises();
    return { store, generations };
  };

  test('a teacher with question generation is shown them', async () => {
    const { generations } = await open(true);

    expect(generations).toHaveBeenCalledTimes(1);
  });

  // The demo teacher is a teacher, but the backend refuses it the generated questions (400): asking
  // for them put an error alert over the page, which a Cypress spec of the CI tripped on
  test('a teacher without it (the demo teacher) is not even asked, so no error is shown', async () => {
    const { store, generations } = await open(false);

    expect(generations).not.toHaveBeenCalled();
    expect(store.setError).not.toHaveBeenCalled();
  });
});
