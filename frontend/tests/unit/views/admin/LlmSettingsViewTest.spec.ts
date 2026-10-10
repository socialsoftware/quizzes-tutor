import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import { LlmSettingsView } from '@/models/admin/LlmSettings';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import LlmSettingsViewPage from '@/views/admin/LlmSettingsView.vue';

const settingsView = (): LlmSettingsView => ({
  settings: {
    primary: { provider: 'nvidia_nim', model: 'nvidia/nemotron-3-ultra-550b-a55b' },
    fallbacks: [{ provider: 'ollama', model: 'llama3.1:8b' }],
    ollamaBaseUrl: 'http://ollama:11434',
    timeout: 120,
    thinking: false,
    maxRetries: 2,
    discussionSuggestions: true,
    contextChars: 12000,
  },
  keys: { ollama: true, nvidia_nim: true, openai: false, anthropic: false },
  providers: ['ollama', 'openai', 'anthropic', 'nvidia_nim'],
  defaultModels: { ollama: 'llama3.1:8b', openai: 'gpt-4o-mini', anthropic: 'claude-sonnet-5', nvidia_nim: 'nvidia/nemotron-3-ultra-550b-a55b' },
});

describe('LlmSettingsView', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getLlmSettings').mockResolvedValue(settingsView());
    vi.spyOn(RemoteServices, 'getOllamaModels').mockResolvedValue({ models: ['llama3.1:8b'], pulls: {}, error: null });
    vi.spyOn(RemoteServices, 'getProviderModels').mockImplementation(async (provider: string) =>
      provider === 'nvidia_nim'
        ? { provider, models: ['meta/llama-3.3-70b-instruct', 'nvidia/nemotron-3-ultra-550b-a55b'], error: null }
        : { provider, models: [], error: `NoApiKey: no API key for ${provider}` }
    );
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.useRealTimers();
  });

  const mountPage = async () => {
    const wrapper = mount(LlmSettingsViewPage, { global: { plugins: [createVuetify({ components, directives })] } });
    await flushPromises();
    return wrapper;
  };

  const vm = (wrapper: any) => wrapper.vm as any;
  const saveButton = (wrapper: any) => wrapper.find('[data-cy="SaveLlmSettings"]');

  test('shows the main model, the fallbacks and the Ollama models; nothing to save yet', async () => {
    const wrapper = await mountPage();

    expect(wrapper.findAll('[data-cy="LlmModelRow"]')).toHaveLength(2);
    expect(wrapper.findAll('[data-cy="OllamaModel"]').map((chip) => chip.text())).toEqual(['llama3.1:8b']);
    expect(wrapper.text()).toContain('never shown');
    expect(saveButton(wrapper).attributes('disabled')).toBeDefined();
  });

  test('each provider in use offers the models it lists, read once', async () => {
    const wrapper = await mountPage();

    expect(RemoteServices.getProviderModels).toHaveBeenCalledTimes(1);
    expect(RemoteServices.getProviderModels).toHaveBeenCalledWith('nvidia_nim', false);
    expect(vm(wrapper).modelsOf('nvidia_nim')).toEqual(['meta/llama-3.3-70b-instruct', 'nvidia/nemotron-3-ultra-550b-a55b']);
    expect(vm(wrapper).modelsOf('ollama')).toEqual(['llama3.1:8b']);
    expect(wrapper.text()).toContain('2 models from NVIDIA NIM');
  });

  test('a provider whose list cannot be read says why, and the list can be read again', async () => {
    const wrapper = await mountPage();

    vm(wrapper).draft.fallbacks[0] = { provider: 'anthropic', model: 'claude-sonnet-5' };
    await flushPromises();
    expect(wrapper.text()).toContain('Could not read the models of Anthropic');

    await wrapper.findAll('[data-cy="RefreshModels"]')[0].trigger('click');
    await flushPromises();
    expect(RemoteServices.getProviderModels).toHaveBeenLastCalledWith('nvidia_nim', true);
  });

  test('a model that is not in the provider\'s list is pointed out', async () => {
    const wrapper = await mountPage();

    vm(wrapper).draft.primary = { provider: 'nvidia_nim', model: 'nvidia/nemotron-3-super-120b-a12b' };
    await flushPromises();

    expect(wrapper.text()).toContain('may have been withdrawn');
  });

  test('saving sends the changed settings', async () => {
    const save = vi.spyOn(RemoteServices, 'saveLlmSettings').mockImplementation(async (settings) => ({ ...settingsView(), settings }));
    const wrapper = await mountPage();

    vm(wrapper).draft.primary = { provider: 'ollama', model: 'qwen3:4b' };
    vm(wrapper).draft.timeout = 600;
    await flushPromises();
    await saveButton(wrapper).trigger('click');
    await flushPromises();

    expect(save.mock.calls[0][0]).toEqual(expect.objectContaining({ primary: { provider: 'ollama', model: 'qwen3:4b' }, timeout: 600 }));
    expect(vm(wrapper).dirty).toBe(false);
  });

  test('a provider without its API key cannot be saved and says why', async () => {
    const wrapper = await mountPage();

    vm(wrapper).draft.fallbacks[0] = { provider: 'openai', model: 'gpt-4o-mini' };
    await flushPromises();

    expect(wrapper.find('[data-cy="MissingKey"]').text()).toContain('OpenAI');
    expect(saveButton(wrapper).attributes('disabled')).toBeDefined();
  });

  test('values out of range or a bad address cannot be saved', async () => {
    const wrapper = await mountPage();

    for (const change of [{ timeout: 1 }, { maxRetries: 9 }, { contextChars: 10 }, { ollamaBaseUrl: 'ollama:11434' }]) {
      vm(wrapper).draft = { ...settingsView().settings, ...change };
      await flushPromises();
      expect(saveButton(wrapper).attributes('disabled')).toBeDefined();
    }
  });

  test('fallbacks are added, reordered and removed', async () => {
    const wrapper = await mountPage();

    await wrapper.find('[data-cy="AddFallback"]').trigger('click');
    expect(vm(wrapper).draft.fallbacks.map((f: any) => f.provider)).toEqual(['ollama', 'nvidia_nim']);

    vm(wrapper).moveFallback(1, -1);
    expect(vm(wrapper).draft.fallbacks.map((f: any) => f.provider)).toEqual(['nvidia_nim', 'ollama']);

    vm(wrapper).removeFallback(0);
    expect(vm(wrapper).draft.fallbacks.map((f: any) => f.provider)).toEqual(['ollama']);
  });

  test('a model is downloaded to Ollama and the page keeps asking until it is there', async () => {
    vi.useFakeTimers();
    vi.spyOn(RemoteServices, 'pullOllamaModel').mockResolvedValue({ models: [], pulls: { 'qwen3:4b': 'downloading' }, error: null });
    const wrapper = await mountPage();
    const list = RemoteServices.getOllamaModels as any;
    list.mockResolvedValue({ models: ['llama3.1:8b', 'qwen3:4b'], pulls: { 'qwen3:4b': 'done' }, error: null });

    vm(wrapper).modelToPull = ' qwen3:4b ';
    await flushPromises();
    await wrapper.find('[data-cy="PullModel"]').trigger('click');
    await flushPromises();

    expect(RemoteServices.pullOllamaModel).toHaveBeenCalledWith('qwen3:4b');
    expect(wrapper.find('[data-cy="PullState"]').text()).toContain('downloading');

    await vi.advanceTimersByTimeAsync(5000);

    expect(wrapper.find('[data-cy="PullState"]').text()).toContain('downloaded');
    expect(wrapper.findAll('[data-cy="OllamaModel"]').map((chip) => chip.text())).toEqual(['llama3.1:8b', 'qwen3:4b']);
  });

  test('an Ollama server that does not answer is shown as a warning, not an error', async () => {
    (RemoteServices.getOllamaModels as any).mockResolvedValue({ models: [], pulls: {}, error: 'ConnectError: connection refused' });

    const wrapper = await mountPage();

    expect(wrapper.find('[data-cy="OllamaError"]').text()).toContain('connection refused');
    expect(store.setError).not.toHaveBeenCalled();
  });

  test('a model can be tried, and a model that does not answer says why', async () => {
    vi.spyOn(RemoteServices, 'testLlmModel')
      .mockResolvedValueOnce({ ok: true, seconds: 1.2, reply: '{"ok": true}', error: null })
      .mockResolvedValueOnce({ ok: false, seconds: 0.3, reply: null, error: 'NotFoundError: model withdrawn' });
    const wrapper = await mountPage();
    const testButton = () => wrapper.findAll('[data-cy="TestModel"]')[0];

    await testButton().trigger('click');
    await flushPromises();
    expect(wrapper.find('[data-cy="TestResult"]').text()).toBe('Answered in 1.2 s');

    await testButton().trigger('click');
    await flushPromises();
    expect(wrapper.find('[data-cy="TestError"]').text()).toContain('model withdrawn');
    expect(RemoteServices.testLlmModel).toHaveBeenCalledWith({ provider: 'nvidia_nim', model: 'nvidia/nemotron-3-ultra-550b-a55b' });
  });
});
