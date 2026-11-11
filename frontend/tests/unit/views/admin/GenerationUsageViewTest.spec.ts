import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import { GenerationUsage } from '@/models/admin/GenerationUsage';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import GenerationUsageView from '@/views/admin/GenerationUsageView.vue';

const usage = (): GenerationUsage => ({
  days: ['2030-01-01', '2030-01-02', '2030-01-03'],
  courses: [
    { courseExecutionId: 1, course: 'Software Architecture', academicTerm: '2030/1', requests: [2, 0, 1], questions: [6, 0, 3], totalRequests: 3, totalQuestions: 9 },
    { courseExecutionId: 2, course: 'Databases', academicTerm: '2030/1', requests: [0, 0, 1], questions: [0, 0, 5], totalRequests: 1, totalQuestions: 5 },
  ],
});

describe('GenerationUsageView', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  const mountView = async () => {
    const wrapper = mount(GenerationUsageView, { global: { plugins: [createVuetify({ components, directives })] } });
    await flushPromises();
    return wrapper;
  };

  test('asks for the last 10 days and shows a row per course with a column per day', async () => {
    const read = vi.spyOn(RemoteServices, 'getGenerationUsage').mockResolvedValue(usage());
    const wrapper = await mountView();

    expect(read).toHaveBeenCalledWith(10);
    const rows = wrapper.findAll('[data-cy="UsageRow"]');
    expect(rows).toHaveLength(2);
    expect(rows[0].text()).toContain('Software Architecture');
    expect(rows[0].findAll('[data-cy="UsageCell"]').map((cell) => cell.text())).toEqual(['2 (6)', '·', '1 (3)']);
    expect(rows[0].find('[data-cy="UsageTotal"]').text()).toBe('3 (9)');
    expect(wrapper.text()).toContain('01-02');
  });

  test('adds up the requests of all the courses for each day', async () => {
    vi.spyOn(RemoteServices, 'getGenerationUsage').mockResolvedValue(usage());
    const wrapper = await mountView();

    expect(wrapper.findAll('[data-cy="UsageDayTotal"]').map((cell) => cell.text())).toEqual(['2', '0', '2']);
  });

  test('says so when nobody asked for questions', async () => {
    vi.spyOn(RemoteServices, 'getGenerationUsage').mockResolvedValue({ days: ['2030-01-01'], courses: [] });
    const wrapper = await mountView();

    expect(wrapper.find('[data-cy="UsageEmpty"]').exists()).toBe(true);
    expect(wrapper.find('[data-cy="UsageTable"]').exists()).toBe(false);
  });

  test('a failed read is reported', async () => {
    vi.spyOn(RemoteServices, 'getGenerationUsage').mockRejectedValue('not allowed');
    await mountView();

    expect(store.setError).toHaveBeenCalledWith('not allowed');
  });
});
