import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import TopicNode from '@/models/management/TopicNode';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import MaterialChunk from '@/models/management/generation/MaterialChunk';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import TopicSourcesDialog from '@/views/teacher/topics/TopicSourcesDialog.vue';

const topic = (sources: { materialId: string; chunkId: string }[]) =>
  new TopicNode({ id: 9, name: 'Networks', parentId: null, sequence: null, numberOfQuestions: 0, sources } as TopicNode);

const material = (id: string, filename: string) =>
  new GenerationMaterial({ id, filename, status: 'READY', chunkCount: 3, parser: 'x', parseSeconds: 1, error: null } as GenerationMaterial);

const chunk = (id: string, position: number, heading: string, text: string) =>
  new MaterialChunk({ id, position, heading, text, topicId: 9 } as MaterialChunk);

describe('TopicSourcesDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([material('m1', 'book.pdf'), material('m2', 'slides.pptx')]);
    vi.spyOn(RemoteServices, 'getMaterialChunks').mockImplementation(async (materialId: string) =>
      materialId === 'm1'
        ? [chunk('m1:0', 0, 'Networks', 'Intro text'), chunk('m1:1', 1, 'Networks > HTTP', '404 means not found'), chunk('m1:2', 2, 'Networks > DNS', 'Name servers')]
        : [chunk('m2:0', 0, 'Intro', 'Slide text')]
    );
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async (node: TopicNode) => {
    const wrapper = mount(TopicSourcesDialog, {
      props: { dialog: true, topic: node },
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  const groups = () => Array.from(document.body.querySelectorAll('[data-cy="SourceGroup"]'));

  test('shows the pieces of the topic grouped by document, in reading order, with their headings', async () => {
    await mountDialog(topic([
      { materialId: 'm1', chunkId: 'm1:2' },
      { materialId: 'm2', chunkId: 'm2:0' },
      { materialId: 'm1', chunkId: 'm1:1' },
    ]));

    expect(groups().map((group) => group.querySelector('.font-weight-medium')!.textContent)).toEqual(['book.pdf', 'slides.pptx']);
    const pieces = Array.from(groups()[0].querySelectorAll('[data-cy="SourcePiece"]')).map((piece) => piece.textContent);
    expect(pieces[0]).toContain('Networks > HTTP');
    expect(pieces[0]).toContain('404 means not found');
    expect(pieces[1]).toContain('Name servers');
  });

  test('says how to bring pieces when there are none', async () => {
    await mountDialog(topic([]));

    expect(document.body.querySelector('[data-cy="NoSources"]')!.textContent).toContain('Put under topics');
    expect(RemoteServices.getMaterialChunks).not.toHaveBeenCalled();
  });

  test('a piece that is no longer in its document, or a document that is gone, is shown as such', async () => {
    await mountDialog(topic([
      { materialId: 'm1', chunkId: 'm1:99' },
      { materialId: 'deleted', chunkId: 'deleted:0' },
    ]));

    expect(document.body.textContent).toContain('No longer in the document');
    expect(document.body.textContent).toContain('A document that no longer exists');
    expect(RemoteServices.getMaterialChunks).toHaveBeenCalledTimes(1);
  });

  test('taking a piece out and saving sends the rest', async () => {
    const update = vi.spyOn(RemoteServices, 'updateTopicSources').mockResolvedValue(topic([]));
    const wrapper = await mountDialog(topic([
      { materialId: 'm1', chunkId: 'm1:1' },
      { materialId: 'm1', chunkId: 'm1:2' },
    ]));

    (document.body.querySelector('[data-cy="RemoveSource"]') as HTMLElement).click();
    await flushPromises();
    (document.body.querySelector('[data-cy="SaveSources"]') as HTMLElement).click();
    await flushPromises();

    expect(update.mock.calls[0][0]).toBe(9);
    expect(update.mock.calls[0][1].map((s: any) => s.chunkId)).toEqual(['m1:2']);
    expect(wrapper.emitted('saved')).toHaveLength(1);
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('a failed save is reported and the dialog stays open', async () => {
    vi.spyOn(RemoteServices, 'updateTopicSources').mockRejectedValue('Unable to connect to server');
    const wrapper = await mountDialog(topic([{ materialId: 'm1', chunkId: 'm1:1' }]));

    (document.body.querySelector('[data-cy="RemoveSource"]') as HTMLElement).click();
    await flushPromises();
    (document.body.querySelector('[data-cy="SaveSources"]') as HTMLElement).click();
    await flushPromises();

    expect(store.setError).toHaveBeenCalledWith('Unable to connect to server');
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
  });
});
