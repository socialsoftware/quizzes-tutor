import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import Reply from '@/models/management/Reply';
import ReplySuggestion from '@/models/management/ReplySuggestion';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import ReplyComponent from '@/views/student/discussions/ReplyComponent.vue';

describe('ReplyComponent suggestions', () => {
  let store: any;

  const mountReply = async (role: 'TEACHER' | 'STUDENT') => {
    store = { user: { id: 7, role, username: 'someone' }, setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    const discussion = { id: 3, closed: false, replies: [] as Reply[], lastReplyDate: null };
    const wrapper = mount(ReplyComponent, {
      props: { discussion: discussion as any },
      global: { plugins: [createVuetify({ components, directives })] },
    });
    await flushPromises();
    return wrapper;
  };

  const textarea = (wrapper: any) => wrapper.find('textarea').element as HTMLTextAreaElement;

  beforeEach(() => {
    vi.spyOn(RemoteServices, 'suggestReply').mockResolvedValue(new ReplySuggestion({ reply: 'A draft', sources: [] }));
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  test('only teachers are offered a suggestion', async () => {
    expect((await mountReply('TEACHER')).find('[data-cy="suggestReplyButton"]').exists()).toBe(true);
    expect((await mountReply('STUDENT')).find('[data-cy="suggestReplyButton"]').exists()).toBe(false);
  });

  test('the draft goes into the box and nothing is sent', async () => {
    const add = vi.spyOn(RemoteServices, 'addReply');
    const wrapper = await mountReply('TEACHER');

    await wrapper.find('[data-cy="suggestReplyButton"]').trigger('click');
    await flushPromises();

    expect(RemoteServices.suggestReply).toHaveBeenCalledWith(3);
    expect(textarea(wrapper).value).toBe('A draft');
    expect(add).not.toHaveBeenCalled();
  });

  test('what the teacher then sends is the draft as edited', async () => {
    const add = vi.spyOn(RemoteServices, 'addReply').mockResolvedValue(new Reply({ id: 1, message: 'x' } as Reply));
    const wrapper = await mountReply('TEACHER');
    await wrapper.find('[data-cy="suggestReplyButton"]').trigger('click');
    await flushPromises();

    await wrapper.find('textarea').setValue('A draft, corrected');
    await wrapper.find('[data-cy="submitReplyButton"]').trigger('click');
    await flushPromises();

    expect(add).toHaveBeenCalledTimes(1);
    expect((add.mock.calls[0][0] as Reply).message).toBe('A draft, corrected');
    expect(add.mock.calls[0][1]).toBe(3);
  });

  test('a draft does not silently replace what the teacher was writing', async () => {
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountReply('TEACHER');
    await wrapper.find('textarea').setValue('my own answer');

    await wrapper.find('[data-cy="suggestReplyButton"]').trigger('click');
    await flushPromises();
    expect(confirmation).toHaveBeenCalled();
    expect(RemoteServices.suggestReply).not.toHaveBeenCalled();
    expect(textarea(wrapper).value).toBe('my own answer');

    confirmation.mockReturnValue(true);
    await wrapper.find('[data-cy="suggestReplyButton"]').trigger('click');
    await flushPromises();
    expect(textarea(wrapper).value).toBe('A draft');
  });

  test('when the service cannot draft, the error is shown and the box is left alone', async () => {
    (RemoteServices.suggestReply as any).mockRejectedValue(new Error('reply suggestions are turned off'));
    const wrapper = await mountReply('TEACHER');
    await wrapper.find('textarea').setValue('keep me');
    vi.spyOn(window, 'confirm').mockReturnValue(true);

    await wrapper.find('[data-cy="suggestReplyButton"]').trigger('click');
    await flushPromises();

    expect(store.setError).toHaveBeenCalled();
    expect(textarea(wrapper).value).toBe('keep me');
  });
});
