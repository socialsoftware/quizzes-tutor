import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import QuestionGeneration from '@/models/management/generation/QuestionGeneration';
import Review from '@/models/management/Review';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import ShowGeneratedQuestionDialog from '@/views/teacher/generation/ShowGeneratedQuestionDialog.vue';

const generation = (overrides: Record<string, unknown> = {}) =>
  new QuestionGeneration({
    id: 11,
    courseExecutionId: 1,
    jobId: 2,
    question: {
      id: 5,
      title: 'What does 404 mean?',
      content: 'What does 404 mean?',
      status: 'SUBMITTED',
      topics: [],
      creationDate: '2030-01-01T10:00:00Z',
      questionDetailsDto: { type: 'multiple_choice', options: [] },
    },
    status: 'IN_REVIEW',
    modelId: 'ollama/llama3.1:8b',
    promptVersion: 'mcq-v1',
    groundingMode: 'STRICT',
    verificationRetries: 2,
    needsHumanAttention: false,
    explanation: 'It is the missing resource code',
    sourceChunkIds: ['m1:0', 'm1:3'],
    ...overrides,
  } as any);

describe('ShowGeneratedQuestionDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn(), getUser: { id: 7 } };
    (useStore as any).mockReturnValue(store);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async (questionGeneration: QuestionGeneration) => {
    const wrapper = mount(ShowGeneratedQuestionDialog, {
      props: { dialog: true, questionGeneration },
      global: {
        plugins: [createVuetify({ components, directives })],
        stubs: { ShowQuestion: true, ShowReviews: true },
      },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  const body = () => document.body;
  const pick = async (wrapper: any, type: string, comment: string) => {
    (wrapper.vm as any).selected = type;
    (wrapper.vm as any).comment = comment;
    await flushPromises();
    (body().querySelector('[data-cy="SubmitButton"]') as HTMLElement).click();
    await flushPromises();
  };

  test('tells the teacher which model wrote it and from how much material', async () => {
    await mountDialog(generation());

    const origin = body().querySelector('[data-cy="Origin"]')!.textContent!;
    expect(origin).toContain('ollama/llama3.1:8b');
    expect(origin).toContain('only from the materials');
    expect(origin).toContain('2 section(s)');
    expect(body().querySelector('[data-cy="Explanation"]')!.textContent).toContain('missing resource code');
  });

  test('warns when the automatic checks gave up', async () => {
    await mountDialog(generation({ needsHumanAttention: true }));

    expect(body().querySelector('[data-cy="AttentionAlert"]')!.textContent).toContain('2 retries');
  });

  test('no warning when the checks passed', async () => {
    await mountDialog(generation());

    expect(body().querySelector('[data-cy="AttentionAlert"]')).toBeNull();
  });

  test('approving sends the review as the logged in teacher and closes the dialog', async () => {
    const create = vi.spyOn(RemoteServices, 'createGenerationReview').mockResolvedValue(new Review());
    const wrapper = await mountDialog(generation());

    await pick(wrapper, 'APPROVE', 'Good question');

    const sent = create.mock.calls[0][0];
    expect(sent.questionGenerationId).toBe(11);
    expect(sent.type).toBe('APPROVE');
    expect(sent.comment).toBe('Good question');
    expect(sent.userId).toBe(7);
    expect(wrapper.emitted('reviewed')).toHaveLength(1);
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('a plain comment keeps the dialog open and clears the form', async () => {
    vi.spyOn(RemoteServices, 'createGenerationReview').mockResolvedValue(new Review());
    const wrapper = await mountDialog(generation());

    await pick(wrapper, 'COMMENT', 'Is option B right?');

    expect(wrapper.emitted('reviewed')).toHaveLength(1);
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
    expect((wrapper.vm as any).comment).toBe('');
  });

  test('a review needs a type, and asking for changes or commenting needs a comment', async () => {
    const create = vi.spyOn(RemoteServices, 'createGenerationReview').mockResolvedValue(new Review());
    const wrapper = await mountDialog(generation());

    await pick(wrapper, null as any, 'text');
    expect(store.setError).toHaveBeenLastCalledWith(expect.stringContaining('review type'));

    await pick(wrapper, 'REQUEST_CHANGES', '   ');
    expect(store.setError).toHaveBeenLastCalledWith(expect.stringContaining('comment'));

    await pick(wrapper, 'COMMENT', '');
    expect(store.setError).toHaveBeenLastCalledWith(expect.stringContaining('comment'));

    expect(create).not.toHaveBeenCalled();
  });

  test.each(['APPROVE', 'REJECT'])('%s needs no comment', async (type) => {
    const create = vi.spyOn(RemoteServices, 'createGenerationReview').mockResolvedValue(new Review());
    const wrapper = await mountDialog(generation());

    await pick(wrapper, type, '');

    expect(store.setError).not.toHaveBeenCalled();
    expect(create.mock.calls[0][0].type).toBe(type);
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('a failed review is reported and the dialog stays open', async () => {
    vi.spyOn(RemoteServices, 'createGenerationReview').mockRejectedValue('Generated question already approved or rejected cannot be reviewed again');
    const wrapper = await mountDialog(generation());

    await pick(wrapper, 'REJECT', 'Wrong');

    expect(store.setError).toHaveBeenCalledWith(expect.stringContaining('cannot be reviewed again'));
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
    expect(wrapper.emitted('reviewed')).toBeUndefined();
  });

  test('a question already decided cannot be reviewed again', async () => {
    await mountDialog(generation({ status: 'APPROVED' }));

    expect(body().querySelector('[data-cy="SubmitButton"]')).toBeNull();
  });
});
