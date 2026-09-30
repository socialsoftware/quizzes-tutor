import { mount, flushPromises } from '@vue/test-utils';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import Tournament from '@/models/user/Tournament';

vi.mock('@/store', () => ({ useStore: vi.fn() }));
vi.mock('vue-router', async (importOriginal) => ({
  ...(await importOriginal<typeof import('vue-router')>()),
  useRouter: () => ({ push: vi.fn() }),
}));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import TournamentsView from '@/views/teacher/tournaments/TournamentsView.vue';

const makeTournament = (id: number, creatorName: string, topic: string) =>
  new Tournament({
    id,
    courseAcronym: 'ES',
    startTime: '2030-01-01T10:00:00Z',
    endTime: '2030-01-01T11:00:00Z',
    numberOfQuestions: 5,
    canceled: false,
    creator: { id, name: creatorName, username: creatorName.toLowerCase() },
    topicsDto: [{ name: topic }],
    participants: [],
    privateTournament: false,
  } as any);

describe('TournamentsView (teacher) search', () => {
  let wrapper: any;

  beforeEach(async () => {
    (useStore as any).mockReturnValue({ setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() });
    vi.spyOn(RemoteServices, 'getTournamentsForCourseExecution').mockResolvedValue([
      makeTournament(1, 'Alice', 'Algebra'),
      makeTournament(2, 'Bruno', 'Graphs'),
    ]);
    const vuetify = createVuetify({ components, directives });
    wrapper = mount(TournamentsView, { global: { plugins: [vuetify] } });
    await flushPromises();
  });

  const search = async (text: string) => {
    await wrapper.find('input').setValue(text);
    await flushPromises();
    return wrapper.findAll('tbody tr').filter((r: any) => !r.text().includes('No data'));
  };

  test('matches on creator name and topic', async () => {
    expect(await search('bruno')).toHaveLength(1);
    expect(await search('algebra')).toHaveLength(1);
  });

  test('"object" no longer matches every row through "[object Object]"', async () => {
    expect(await search('object')).toHaveLength(0);
  });
});
