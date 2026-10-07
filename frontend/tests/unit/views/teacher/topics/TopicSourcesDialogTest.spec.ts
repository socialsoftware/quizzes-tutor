import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import TopicNode from '@/models/management/TopicNode';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import GenerationSection from '@/models/management/generation/GenerationSection';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import TopicSourcesDialog from '@/views/teacher/topics/TopicSourcesDialog.vue';

const material = (id: string, filename: string, status = 'READY') =>
  new GenerationMaterial({ id, filename, status, chunkCount: 3, parser: 'x', parseSeconds: 1, error: null } as GenerationMaterial);

const topic = (sources: { materialId: string; sectionPath: string }[]) =>
  new TopicNode({ id: 7, name: 'Networks', parentId: null, sequence: null, numberOfQuestions: 0, sources } as TopicNode);

describe('TopicSourcesDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([
      material('m1', 'book.pdf'),
      material('m2', 'slides.pptx'),
      material('m3', 'still-reading.pdf', 'PROCESSING'),
    ]);
    vi.spyOn(RemoteServices, 'getGenerationSections').mockImplementation(async (materialId: string) =>
      materialId === 'm1'
        ? [
            new GenerationSection({ path: 'Networks', title: 'Networks', chunk_count: 1 }),
            new GenerationSection({ path: 'Networks > HTTP', title: 'HTTP', chunk_count: 2 }),
            new GenerationSection({ path: 'Networks > DNS', title: 'DNS', chunk_count: 2 }),
          ]
        : [new GenerationSection({ path: 'Intro', title: 'Intro', chunk_count: 1 })]
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
  const vm = (wrapper: any) => wrapper.vm as any;

  test('lists what the topic is linked to, grouped by document name', async () => {
    await mountDialog(topic([
      { materialId: 'm1', sectionPath: 'Networks > HTTP' },
      { materialId: 'm2', sectionPath: 'Intro' },
      { materialId: 'm1', sectionPath: 'Networks > DNS' },
    ]));

    const groups = [...document.body.querySelectorAll('[data-cy="SourceGroup"]')].map((g) => g.textContent!.replace(/\s+/g, ' ').trim());
    expect(groups).toHaveLength(2);
    expect(groups[0]).toContain('book.pdf');
    expect(groups[0]).toContain('Networks > HTTP');
    expect(groups[0]).toContain('Networks > DNS');
    expect(groups[1]).toContain('slides.pptx');
  });

  test('says when nothing is linked yet', async () => {
    await mountDialog(topic([]));

    expect(document.body.querySelector('[data-cy="NoSources"]')).not.toBeNull();
  });

  test('warns about a link whose section is no longer in the document', async () => {
    await mountDialog(topic([
      { materialId: 'm1', sectionPath: 'Networks > HTTP' },
      { materialId: 'm1', sectionPath: 'Old chapter that was merged' },
    ]));

    const chips = [...document.body.querySelectorAll('[data-cy="SourceChip"]')].map((c) => c.textContent!.replace(/\s+/g, ' ').trim());
    expect(chips[0]).not.toContain('no longer');
    expect(chips[1]).toContain('no longer in the document');
  });

  test('a link to a document that is gone is shown as such and flagged', async () => {
    await mountDialog(topic([{ materialId: 'deleted', sectionPath: 'X' }]));

    const group = document.body.querySelector('[data-cy="SourceGroup"]')!.textContent!;
    expect(group).toContain('document no longer available');
    expect(group).toContain('no longer in the document');
  });

  test('only finished documents can be picked to add sections from', async () => {
    const wrapper = await mountDialog(topic([]));

    expect(vm(wrapper).readyMaterials.map((m: GenerationMaterial) => m.id)).toEqual(['m1', 'm2']);
  });

  test('removing a link and saving sends the rest', async () => {
    const update = vi.spyOn(RemoteServices, 'updateTopicSources').mockResolvedValue(new TopicNode());
    const wrapper = await mountDialog(topic([
      { materialId: 'm1', sectionPath: 'Networks > HTTP' },
      { materialId: 'm1', sectionPath: 'Networks > DNS' },
    ]));

    vm(wrapper).removeSource('m1', 'Networks > HTTP');
    await vm(wrapper).save();

    expect(update).toHaveBeenCalledTimes(1);
    expect(update.mock.calls[0][0]).toBe(7);
    expect(update.mock.calls[0][1].map((s: any) => s.sectionPath)).toEqual(['Networks > DNS']);
    expect(wrapper.emitted('saved')).toHaveLength(1);
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('adding sections of a document offers only the ones not linked yet', async () => {
    const update = vi.spyOn(RemoteServices, 'updateTopicSources').mockResolvedValue(new TopicNode());
    const wrapper = await mountDialog(topic([{ materialId: 'm1', sectionPath: 'Networks' }]));

    vm(wrapper).materialToAdd = 'm1';
    await flushPromises();
    expect(vm(wrapper).availableSections.map((s: GenerationSection) => s.path)).toEqual(['Networks > HTTP', 'Networks > DNS']);

    vm(wrapper).sectionsToAdd = ['Networks > DNS'];
    vm(wrapper).addSections();
    await vm(wrapper).save();

    expect(update.mock.calls[0][1].map((s: any) => [s.materialId, s.sectionPath])).toEqual([
      ['m1', 'Networks'],
      ['m1', 'Networks > DNS'],
    ]);
  });

  test('changing the document to add from clears the sections picked for the other one', async () => {
    const wrapper = await mountDialog(topic([]));
    vm(wrapper).materialToAdd = 'm1';
    await flushPromises();
    vm(wrapper).sectionsToAdd = ['Networks > HTTP'];

    vm(wrapper).materialToAdd = 'm2';
    await flushPromises();

    expect(vm(wrapper).sectionsToAdd).toEqual([]);
  });

  test('a failed save is reported and the dialog stays open', async () => {
    vi.spyOn(RemoteServices, 'updateTopicSources').mockRejectedValue('Unable to connect to server');
    const wrapper = await mountDialog(topic([{ materialId: 'm1', sectionPath: 'Networks' }]));

    await vm(wrapper).save();

    expect(store.setError).toHaveBeenCalledWith('Unable to connect to server');
    expect(wrapper.emitted('saved')).toBeUndefined();
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
  });
});
