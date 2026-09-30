import { mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import GenerationJob from '@/models/management/generation/GenerationJob';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import JobsPanel from '@/views/teacher/generation/JobsPanel.vue';

const job = (overrides: Partial<GenerationJob>) =>
  new GenerationJob({
    id: 1,
    topic: 'HTTP',
    topicId: null,
    requestedCount: 5,
    difficulty: 'MEDIUM',
    groundingMode: 'STRICT',
    status: 'REQUESTED',
    generationStatus: 'PENDING',
    importedCount: 0,
    skippedCount: 0,
    error: null,
    creationDate: '2030-01-01T10:00:00Z',
    ...overrides,
  } as GenerationJob);

describe('JobsPanel', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    (useStore as any).mockReturnValue({ setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() });
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.useRealTimers();
  });

  const mountPanel = async () => {
    const wrapper = mount(JobsPanel, { global: { plugins: [createVuetify({ components, directives })] } });
    await vi.advanceTimersByTimeAsync(0);
    return wrapper;
  };

  test('shows what happened to each earlier request', async () => {
    vi.spyOn(RemoteServices, 'getGenerationJobs').mockResolvedValue([
      job({ id: 1, status: 'IMPORTED', importedCount: 4, skippedCount: 1 }),
      job({ id: 2, status: 'FAILED', error: 'ollama unreachable' }),
    ]);

    const wrapper = await mountPanel();

    const text = wrapper.text();
    expect(text).toContain('READY FOR REVIEW');
    expect(text).toContain('FAILED');
  });

  test('a job left pending earlier is picked up again and imported when it finishes', async () => {
    vi.spyOn(RemoteServices, 'getGenerationJobs').mockResolvedValue([job({ id: 7 })]);
    const ask = vi
      .spyOn(RemoteServices, 'getGenerationJob')
      .mockResolvedValueOnce(job({ id: 7, generationStatus: 'RUNNING' }))
      .mockResolvedValue(job({ id: 7, status: 'IMPORTED', importedCount: 5 }));

    const wrapper = await mountPanel();
    expect(wrapper.text()).toContain('QUEUED');

    await vi.advanceTimersByTimeAsync(5000);
    expect(wrapper.text()).toContain('GENERATING');
    expect(wrapper.emitted('imported')).toBeUndefined();

    await vi.advanceTimersByTimeAsync(5000);
    expect(wrapper.text()).toContain('READY FOR REVIEW');
    expect(wrapper.emitted('imported')).toHaveLength(1);

    await vi.advanceTimersByTimeAsync(60_000);
    expect(ask).toHaveBeenCalledTimes(2);
  });

  test('a job added by the form starts being followed right away', async () => {
    vi.spyOn(RemoteServices, 'getGenerationJobs').mockResolvedValue([]);
    const ask = vi.spyOn(RemoteServices, 'getGenerationJob').mockResolvedValue(job({ id: 9, status: 'FAILED', error: 'boom' }));
    const wrapper = await mountPanel();

    (wrapper.vm as any).addJob(job({ id: 9 }));
    await vi.advanceTimersByTimeAsync(5000);

    expect(ask).toHaveBeenCalledWith(9);
    expect(wrapper.text()).toContain('FAILED');
    expect(wrapper.emitted('imported')).toBeUndefined();
  });

  test('a request that fails on the server does not count as imported', async () => {
    vi.spyOn(RemoteServices, 'getGenerationJobs').mockResolvedValue([job({ id: 3 })]);
    vi.spyOn(RemoteServices, 'getGenerationJob').mockResolvedValue(job({ id: 3, status: 'FAILED', error: 'x' }));

    const wrapper = await mountPanel();
    await vi.advanceTimersByTimeAsync(5000);

    expect(wrapper.emitted('imported')).toBeUndefined();
  });
});
