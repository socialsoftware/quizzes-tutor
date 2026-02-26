import { mount, VueWrapper, DOMWrapper } from '@vue/test-utils';
import { flushPromises } from '@vue/test-utils';
import { describe, test, expect, vi, beforeEach, afterEach, beforeAll, afterAll } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import EditCourseDialog from '@/views/admin/courses/EditCourseDialog.vue';
import { emptyCourse, filledCourse } from '../../../samples/Course';
import RemoteServices from '@/services/RemoteServices';
// No vuex
import { useStore } from '@/store';

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;

vi.mock('@/store', () => ({
  useStore: vi.fn()
}));

describe('EditCourseDialog view test', () => {
  let wrapper: VueWrapper<any>;
  let vuetify: ReturnType<typeof createVuetify>;
  let mockStoreContext: any;
  let mockCreateExternalCourse: RemoteServices;

  beforeAll(() => {
    mockCreateExternalCourse = vi
      .spyOn(RemoteServices, 'createExternalCourse')
      .mockImplementation(() => {
        return Promise.resolve(filledCourse);
      });
  });

  afterAll(() => {
    vi.restoreAllMocks();
  });

  beforeEach(() => {
    vi.clearAllMocks();

    vuetify = createVuetify({ components, directives });

    mockStoreContext = { error: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(mockStoreContext);

    const div = document.createElement('div');
    document.body.appendChild(div);

    wrapper = mount(EditCourseDialog, {
      vuetify,
      global: { plugins: [vuetify] },
      props: { course: emptyCourse, dialog: true },
      attachTo: div
    });
  });

  afterEach(() => {
    wrapper.unmount();
  });

  test('fill and cancel', async () => {
    await flushPromises();
    const courseExecutionNameInput = new DOMWrapper(document.querySelector('[data-cy="courseExecutionNameInput"] input') as Element);
    await courseExecutionNameInput.setValue('Software Engineering');

    const courseExecutionAcronymInput = new DOMWrapper(document.querySelector('[data-cy="courseExecutionAcronymInput"] input') as Element);
    await courseExecutionAcronymInput.setValue('ES2021');

    const courseExecutionAcademicTermInput = new DOMWrapper(document.querySelector('[data-cy="courseExecutionAcademicTermInput"] input') as Element);
    await courseExecutionAcademicTermInput.setValue('Spring Semester 20/21');

    const button = new DOMWrapper(document.querySelector('button[data-cy="cancelButton"]') as Element);
    await button.trigger('click');

    expect(wrapper.emitted('close-dialog')?.length).toBe(1);

    expect(mockCreateExternalCourse).not.toHaveBeenCalled();
  });

  test('fill and save', async () => {
    await flushPromises();
    const courseExecutionNameInput = new DOMWrapper(document.querySelector('[data-cy="courseExecutionNameInput"] input') as Element);
    await courseExecutionNameInput.setValue('Software Engineering');

    const courseExecutionAcronymInput = new DOMWrapper(document.querySelector('[data-cy="courseExecutionAcronymInput"] input') as Element);
    await courseExecutionAcronymInput.setValue('ES2021');

    const courseExecutionAcademicTermInput = new DOMWrapper(document.querySelector('[data-cy="courseExecutionAcademicTermInput"] input') as Element);
    await courseExecutionAcademicTermInput.setValue('Spring Semester 20/21');

    const button = new DOMWrapper(document.querySelector('button[data-cy="saveButton"]') as Element);
    await button.trigger('click');

    expect(wrapper.emitted('new-course')?.length).toBe(1);
    expect(wrapper.emitted('new-course')?.[0]).toEqual([filledCourse]);

    expect(mockCreateExternalCourse).toHaveBeenCalled();
  });

  test('fill and save without complete information', async () => {
    await flushPromises();
    const courseExecutionNameInput = new DOMWrapper(document.querySelector('[data-cy="courseExecutionNameInput"] input') as Element);
    await courseExecutionNameInput.setValue('Software Engineering');

    const button = new DOMWrapper(document.querySelector('button[data-cy="saveButton"]') as Element);
    await button.trigger('click');

    expect(mockStoreContext.setError).toHaveBeenCalled();
    expect(mockCreateExternalCourse).not.toHaveBeenCalled();
  });
});
