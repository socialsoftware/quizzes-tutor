import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import { TeacherApiKeys } from '@/models/management/generation/TeacherApiKeys';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import ApiKeysDialog from '@/views/teacher/generation/ApiKeysDialog.vue';

const keys = (configured: string[] = [], available = true): TeacherApiKeys => ({
  available,
  keys: ['nvidia_nim', 'openai', 'anthropic'].map((provider) => ({
    provider,
    configured: configured.includes(provider),
    lastFour: configured.includes(provider) ? '3456' : null,
    updatedAt: null,
  })),
});

describe('ApiKeysDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async () => {
    const wrapper = mount(ApiKeysDialog, {
      props: { dialog: true },
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  const rows = () => Array.from(document.body.querySelectorAll('[data-cy="ApiKeyRow"]'));
  const type = async (row: Element, value: string) => {
    const input = row.querySelector('[data-cy="KeyInput"] input') as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
    await flushPromises();
  };
  const click = async (row: Element, cy: string) => {
    (row.querySelector(`[data-cy="${cy}"]`) as HTMLElement).click();
    await flushPromises();
  };

  test('shows which providers have a key of the teacher and how it ends, never the key', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys(['openai']));
    await mountDialog();

    expect(rows()).toHaveLength(3);
    expect(rows()[1].querySelector('[data-cy="KeyConfigured"]')!.textContent).toContain('ending in 3456');
    expect(rows()[0].querySelector('[data-cy="KeyNotSet"]')).not.toBeNull();
    expect((rows()[0].querySelector('[data-cy="KeyInput"] input') as HTMLInputElement).type).toBe('password');
  });

  test('a key is sent as typed, then the field is emptied and the status updates', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    const save = vi.spyOn(RemoteServices, 'saveMyApiKey').mockResolvedValue(keys(['nvidia_nim']));
    const wrapper = await mountDialog();

    expect((rows()[0].querySelector('[data-cy="SaveKey"]') as HTMLButtonElement).disabled).toBe(true);
    await type(rows()[0], '  nvapi-secret-1234  ');
    await click(rows()[0], 'SaveKey');
    expect(wrapper.emitted('changed')).toHaveLength(1);

    expect(save).toHaveBeenCalledWith('nvidia_nim', 'nvapi-secret-1234');
    expect((rows()[0].querySelector('[data-cy="KeyInput"] input') as HTMLInputElement).value).toBe('');
    expect(rows()[0].querySelector('[data-cy="KeyConfigured"]')).not.toBeNull();
  });

  test('a key the provider refuses is reported and not shown as saved', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    vi.spyOn(RemoteServices, 'saveMyApiKey').mockRejectedValue('The provider did not accept this key: 401');
    await mountDialog();

    await type(rows()[1], 'sk-bad-key-1234');
    await click(rows()[1], 'SaveKey');

    expect(store.setError).toHaveBeenCalledWith('The provider did not accept this key: 401');
    expect(rows()[1].querySelector('[data-cy="KeyConfigured"]')).toBeNull();
  });

  test('removing a key asks first', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys(['anthropic']));
    const remove = vi.spyOn(RemoteServices, 'removeMyApiKey').mockResolvedValue(keys());
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    await mountDialog();

    await click(rows()[2], 'RemoveKey');
    expect(remove).not.toHaveBeenCalled();

    confirmation.mockReturnValue(true);
    await click(rows()[2], 'RemoveKey');
    expect(remove).toHaveBeenCalledWith('anthropic');
    expect(rows()[2].querySelector('[data-cy="KeyNotSet"]')).not.toBeNull();
  });

  test('a server that cannot keep keys says so and takes none', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys([], false));
    await mountDialog();

    expect(document.body.querySelector('[data-cy="KeysUnavailable"]')).not.toBeNull();
    expect((rows()[0].querySelector('[data-cy="KeyInput"] input') as HTMLInputElement).disabled).toBe(true);
  });

  test('what was typed is dropped when the dialog is reopened', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    const wrapper = await mountDialog();
    await type(rows()[0], 'nvapi-typed-and-not-saved');

    await wrapper.setProps({ dialog: false });
    await wrapper.setProps({ dialog: true });
    await flushPromises();

    expect((rows()[0].querySelector('[data-cy="KeyInput"] input') as HTMLInputElement).value).toBe('');
  });
});
