import { mount, flushPromises } from '@vue/test-utils';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import { Student } from '@/models/user/Student';
import { filledCourse } from '../../../samples/Course';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import StudentsView from '@/views/teacher/students/StudentsView.vue';

describe('StudentsView test', () => {
  beforeEach(() => {
    (useStore as any).mockReturnValue({
      setLoading: vi.fn(),
      clearLoading: vi.fn(),
      setError: vi.fn(),
      getCurrentCourse: filledCourse,
    });
    const students = Array.from({ length: 35 }, (_, i) =>
      new Student({ id: i, name: `Student ${i}`, username: `s${i}`, percentageOfCorrectAnswers: 50 } as Student)
    );
    vi.spyOn(RemoteServices, 'getCourseStudents').mockResolvedValue(students);
  });

  test('lists every student, not just the first page of 10', async () => {
    const vuetify = createVuetify({ components, directives });
    const wrapper = mount(StudentsView, { global: { plugins: [vuetify] } });
    await flushPromises();

    expect(wrapper.findAll('tbody tr')).toHaveLength(35);
  });
});
