import { mount, flushPromises } from '@vue/test-utils';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import Tournament from '@/models/user/Tournament';
import appVuetify from '@/vuetify';

vi.mock('@/store', () => ({ useStore: vi.fn() }));
vi.mock('vue-router', async (importOriginal) => ({
  ...(await importOriginal<typeof import('vue-router')>()),
  useRouter: () => ({ push: vi.fn() }),
}));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import TournamentsListView from '@/views/student/tournament/TournamentsListView.vue';

const makeTournament = (id: number, creatorName: string, topic: string) =>
  new Tournament(
    {
      id,
      courseAcronym: 'ES',
      startTime: '2030-01-01T10:00:00Z',
      endTime: '2030-01-01T11:00:00Z',
      numberOfQuestions: 5,
      canceled: false,
      opened: false,
      closed: false,
      creator: { id: 1000 + id, name: creatorName, username: creatorName.toLowerCase() },
      topicsDto: [{ name: topic }],
      participants: [],
      privateTournament: false,
    } as any,
    { username: 'me' } as any
  );

describe('TournamentsListView (student)', () => {
  const mountView = async (tournaments: Tournament[], useAppIcons = false) => {
    (useStore as any).mockReturnValue({ setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn(), getUser: { id: 1 } });
    vi.spyOn(RemoteServices, 'getOpenedTournamentsForCourseExecution').mockResolvedValue(tournaments);
    const vuetify = useAppIcons ? appVuetify : createVuetify({ components, directives });
    const wrapper = mount(TournamentsListView, { props: { type: 'OPEN' }, global: { plugins: [vuetify] } });
    await flushPromises();
    return wrapper;
  };

  const rows = (wrapper: any) =>
    wrapper.findAll('tbody tr').filter((r: any) => !r.text().includes('No data'));

  beforeEach(() => vi.restoreAllMocks());

  test('lists every tournament, not just the first 10', async () => {
    const wrapper = await mountView(
      Array.from({ length: 25 }, (_, i) => makeTournament(i + 1, `Person${i}`, 'Topic'))
    );
    expect(rows(wrapper)).toHaveLength(25);
  });

  test('search matches creator and topics by text, never by "[object Object]"', async () => {
    const wrapper = await mountView([
      makeTournament(1, 'Alice', 'Algebra'),
      makeTournament(2, 'Bruno', 'Graphs'),
    ]);
    const search = async (text: string) => {
      await wrapper.find('input').setValue(text);
      await flushPromises();
      return rows(wrapper);
    };

    expect(await search('bruno')).toHaveLength(1);
    expect(await search('algebra')).toHaveLength(1);
    expect(await search('object')).toHaveLength(0);
  });

  test('join icon carries the Font Awesome style class so it actually renders', async () => {
    const wrapper = await mountView([makeTournament(1, 'Alice', 'Algebra')], true);
    const join = wrapper.find('[data-cy="JoinTournament"] i');
    expect(join.classes()).toContain('fas');
    expect(join.classes()).toContain('fa-sign-in-alt');
  });
});
