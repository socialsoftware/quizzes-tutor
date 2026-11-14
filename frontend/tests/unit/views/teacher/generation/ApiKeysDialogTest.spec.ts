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

const PROVIDERS = [
  { id: 'anthropic', name: 'Anthropic' },
  { id: 'gemini', name: 'Google Gemini' },
  { id: 'groq', name: 'Groq' },
  { id: 'nvidia_nim', name: 'NVIDIA NIM' },
];

const keys = (given: string[] = [], available = true): TeacherApiKeys => ({
  available,
  keys: PROVIDERS.filter((provider) => given.includes(provider.id)).map((provider) => ({
    provider: provider.id,
    name: provider.name,
    lastFour: '3456',
    updatedAt: null,
  })),
  providers: PROVIDERS,
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
  const field = (cy: string) => document.body.querySelector(`[data-cy="${cy}"] input`) as HTMLInputElement;
  const type = async (cy: string, value: string) => {
    const input = field(cy);
    input.value = value;
    input.dispatchEvent(new Event('input'));
    await flushPromises();
  };
  const click = async (cy: string, root: ParentNode = document.body) => {
    (root.querySelector(`[data-cy="${cy}"]`) as HTMLElement).click();
    await flushPromises();
  };
  // The provider is picked in the autocomplete of the panel
  const pick = async (wrapper: any, provider: string) => {
    const panel = wrapper.findComponent({ name: 'ApiKeysPanel' });
    (panel.vm as any).provider = provider;
    await flushPromises();
  };
  const saveButton = () => document.body.querySelector('[data-cy="SaveKey"]') as HTMLButtonElement;

  test('the button to save is ready, not busy, until a key is being checked, and the key field says to choose a provider first', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    let finish: (value: TeacherApiKeys) => void = () => undefined;
    vi.spyOn(RemoteServices, 'saveMyApiKey').mockReturnValue(new Promise((resolve) => (finish = resolve)));
    const wrapper = await mountDialog();

    expect(saveButton().classList.contains('v-btn--loading')).toBe(false);
    expect(field('KeyInput').placeholder).toBe('Choose a provider first');

    await pick(wrapper, 'gemini');
    expect(field('KeyInput').placeholder).toBe('Paste the key here');
    await type('KeyInput', 'AIza-secret-1234');
    await click('SaveKey');
    expect(saveButton().classList.contains('v-btn--loading')).toBe(true);

    finish(keys(['gemini']));
    await flushPromises();
    expect(saveButton().classList.contains('v-btn--loading')).toBe(false);
  });

  test('lists the keys given, by the name of the provider and how they end, never the key', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys(['gemini', 'groq']));
    await mountDialog();

    expect(rows().map((row) => row.querySelector('[data-cy="KeyProvider"]')!.textContent)).toEqual(['Google Gemini', 'Groq']);
    expect(rows()[0].querySelector('[data-cy="KeyConfigured"]')!.textContent).toContain('ending in 3456');
    expect((field('KeyInput')).type).toBe('password');
  });

  test('says when no key was given and the site\'s is used', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    await mountDialog();

    expect(document.body.querySelector('[data-cy="NoKeys"]')!.textContent).toContain("site's");
  });

  test('offers every provider for a key, a key is sent for the one picked, then the form is emptied', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    const save = vi.spyOn(RemoteServices, 'saveMyApiKey').mockResolvedValue(keys(['gemini']));
    const wrapper = await mountDialog();
    const panel = wrapper.findComponent({ name: 'ApiKeysPanel' });
    expect((panel.vm as any).providerItems.map((item: any) => item.title)).toEqual(['Anthropic', 'Google Gemini', 'Groq', 'NVIDIA NIM']);

    await pick(wrapper, 'gemini');
    expect(saveButton().disabled).toBe(true);
    await type('KeyInput', '  AIza-secret-1234  ');
    await click('SaveKey');

    expect(save).toHaveBeenCalledWith('gemini', 'AIza-secret-1234', '');
    expect(wrapper.emitted('changed')).toHaveLength(1);
    expect(field('KeyInput').value).toBe('');
    expect(rows()).toHaveLength(1);
  });

  test('a model to check the key with can be named', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    const save = vi.spyOn(RemoteServices, 'saveMyApiKey').mockResolvedValue(keys(['groq']));
    const wrapper = await mountDialog();

    await pick(wrapper, 'groq');
    await type('KeyCheckModel', ' llama-3.1-8b-instant ');
    await type('KeyInput', 'gsk-secret-1234');
    await click('SaveKey');

    expect(save).toHaveBeenCalledWith('groq', 'gsk-secret-1234', 'llama-3.1-8b-instant');
  });

  test('a key the provider refuses is reported and not shown as given', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    vi.spyOn(RemoteServices, 'saveMyApiKey').mockRejectedValue('The provider did not accept this key: 401');
    const wrapper = await mountDialog();

    await pick(wrapper, 'groq');
    await type('KeyInput', 'gsk-bad-key-1234');
    await click('SaveKey');

    expect(store.setError).toHaveBeenCalledWith('The provider did not accept this key: 401');
    expect(rows()).toHaveLength(0);
  });

  test('removing a key asks first', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys(['anthropic']));
    const remove = vi.spyOn(RemoteServices, 'removeMyApiKey').mockResolvedValue(keys());
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountDialog();

    await click('RemoveKey', rows()[0]);
    expect(remove).not.toHaveBeenCalled();

    confirmation.mockReturnValue(true);
    await click('RemoveKey', rows()[0]);
    expect(remove).toHaveBeenCalledWith('anthropic');
    expect(wrapper.emitted('changed')).toHaveLength(1);
    expect(rows()).toHaveLength(0);
  });

  test('a server that cannot keep keys says so and takes none', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys([], false));
    await mountDialog();

    expect(document.body.querySelector('[data-cy="KeysUnavailable"]')).not.toBeNull();
    expect(field('KeyInput').disabled).toBe(true);
  });

  test('what was typed is dropped when the dialog is reopened', async () => {
    vi.spyOn(RemoteServices, 'getMyApiKeys').mockResolvedValue(keys());
    const wrapper = await mountDialog();
    await pick(wrapper, 'groq');
    await type('KeyInput', 'gsk-typed-and-not-saved');

    await wrapper.setProps({ dialog: false });
    await wrapper.setProps({ dialog: true });
    await flushPromises();

    expect(field('KeyInput').value).toBe('');
  });
});
