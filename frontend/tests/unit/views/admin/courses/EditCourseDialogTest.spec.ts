import { mount, Wrapper } from '@vue/test-utils';
import Vue from 'vue';
import Vuetify from 'vuetify';
import EditCourseDialog from '@/views/admin/courses/EditCourseDialog.vue';
import { emptyCourse, filledCourse } from '../../../samples/Course';
import RemoteServices from '@/services/RemoteServices';
import Vuex from 'vuex';

describe('EditCourseDialog view test', () => {
  let wrapper: Wrapper<EditCourseDialog>;
  let vuetify: Vuetify;
  const actions = { error: jest.fn() };
  let mockCreateExternalCourse: RemoteServices;

  beforeAll(() => {
    mockCreateExternalCourse = jest
      .spyOn(RemoteServices, 'createExternalCourse')
      .mockImplementation(() => {
        return Promise.resolve(filledCourse);
      });
  });

  afterAll(() => {
    jest.restoreAllMocks();
  });

  beforeEach(() => {
    jest.clearAllMocks();

    Vue.use(Vuetify);
    vuetify = new Vuetify();
    const store = new Vuex.Store({
      actions,
    });

    wrapper = mount(EditCourseDialog, {
      vuetify,
      store,
      propsData: { course: emptyCourse, dialog: true },
    });
  });

  afterEach(() => {
    wrapper.destroy();
  });

  test('fill and cancel', async () => {
    const courseExecutionNameInput = wrapper.find(
      'input[data-cy="courseExecutionNameInput"]'
    );
    courseExecutionNameInput.setValue('Software Engineering');

    const courseExecutionAcronymInput = wrapper.find(
      'input[data-cy="courseExecutionAcronymInput"]'
    );
    courseExecutionAcronymInput.setValue('ES2021');

    const courseExecutionAcademicTermInput = wrapper.find(
      'input[data-cy="courseExecutionAcademicTermInput"]'
    );
    courseExecutionAcademicTermInput.setValue('Spring Semester 20/21');

    const button = wrapper.find('button[data-cy="cancelButton"]');
    await button.trigger('click');

    expect(wrapper.emitted('close-dialog')?.length).toBe(1);

    expect(mockCreateExternalCourse).not.toHaveBeenCalled();
  });

  test('fill and save', async () => {
    const courseExecutionNameInput = wrapper.find(
      'input[data-cy="courseExecutionNameInput"]'
    );
    courseExecutionNameInput.setValue('Software Engineering');

    const courseExecutionAcronymInput = wrapper.find(
      'input[data-cy="courseExecutionAcronymInput"]'
    );
    courseExecutionAcronymInput.setValue('ES2021');

    const courseExecutionAcademicTermInput = wrapper.find(
      'input[data-cy="courseExecutionAcademicTermInput"]'
    );
    courseExecutionAcademicTermInput.setValue('Spring Semester 20/21');

    const button = wrapper.find('button[data-cy="saveButton"]');
    await button.trigger('click');

    expect(wrapper.emitted('new-course')?.length).toBe(1);
    expect(wrapper.emitted('new-course')?.[0]).toEqual([filledCourse]);

    expect(mockCreateExternalCourse).toHaveBeenCalled();
  });

  test('fill and save without complete information', async () => {
    const courseExecutionNameInput = wrapper.find(
      'input[data-cy="courseExecutionNameInput"]'
    );
    courseExecutionNameInput.setValue('Software Engineering');

    const button = wrapper.find('button[data-cy="saveButton"]');
    await button.trigger('click');

    expect(actions.error).toHaveBeenCalled();
    expect(mockCreateExternalCourse).not.toHaveBeenCalled();
  });
});
