import { mount, Wrapper } from '@vue/test-utils';
import Vue from 'vue';
import Vuetify from 'vuetify';
import {
  filledCourse,
  filledCourseWithUsers,
  filledOldISTCourse,
} from '../../../samples/Course';
import RemoteServices from '@/services/RemoteServices';
import Vuex from 'vuex';
import CoursesView from '@/views/admin/courses/CoursesView.vue';
import { userBeto, userZe } from '../../../samples/User';

describe('CoursesView test', () => {
  let wrapper: Wrapper<CoursesView>;
  let vuetify: Vuetify;
  const actions = {
    loading: jest.fn(),
    clearLoading: jest.fn(),
    error: jest.fn(),
  };

  beforeAll(() => {
    jest.spyOn(RemoteServices, 'getCourses').mockImplementation(() => {
      return Promise.all([filledCourse]);
    });
  });

  afterAll(() => {
    jest.restoreAllMocks();
  });

  beforeEach(() => {
    Vue.use(Vuetify);
    vuetify = new Vuetify();
    const store = new Vuex.Store({
      actions,
    });

    wrapper = mount(CoursesView, {
      vuetify,
      store,
    });
  });

  afterEach(() => {
    wrapper.destroy();
  });

  test('open and load courses', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const tr = wrapper.findAll('tbody > tr');
    expect(tr.length).toBe(1);

    const td = tr.at(0).findAll('td');
    expect(td.length).toBe(13);

    expect(td.at(1).text()).toBe(filledCourse.courseType);
    expect(td.at(2).text()).toBe(filledCourse.name);
    expect(td.at(3).text()).toBe(filledCourse.courseExecutionType);
    expect(td.at(4).text()).toBe(filledCourse.acronym);
    expect(td.at(5).text()).toBe(filledCourse.academicTerm);
    expect(td.at(6).text()).toBe(
      filledCourse.numberOfActiveTeachers?.toString()
    );
    expect(td.at(7).text()).toBe(
      filledCourse.numberOfInactiveTeachers?.toString()
    );
    expect(td.at(8).text()).toBe(
      filledCourse.numberOfActiveStudents?.toString()
    );
    expect(td.at(9).text()).toBe(
      filledCourse.numberOfInactiveStudents?.toString()
    );
    expect(td.at(10).text()).toBe(filledCourse.numberOfQuestions?.toString());
    expect(td.at(11).text()).toBe(filledCourse.numberOfQuizzes?.toString());
    expect(td.at(12).text()).toBe(filledCourse.status?.toString());
  });

  test('select new course and open edit course dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="createButton"]');
    await button.trigger('click');

    expect(wrapper.findComponent({ name: 'EditCourseDialog' }).exists()).toBe(
      true
    );
  });

  test('select create course from course and open edit course dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="createFromCourse"]');
    await button.trigger('click');

    const editCourseDialog = wrapper.findComponent({
      name: 'EditCourseDialog',
    });

    expect(editCourseDialog.exists()).toBe(true);

    const course = editCourseDialog.props().course;
    expect(course.courseType).toEqual(filledCourse.courseType);
    expect(course.name).toEqual(filledCourse.name);
    expect(course.courseExecutionType).toEqual(
      filledCourse.courseExecutionType
    );
    expect(course.acronym).toEqual(undefined);
    expect(course.academicTerm).toEqual(undefined);
  });

  test('select view users and open view users dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="viewUsersButton"]');
    await button.trigger('click');

    const viewUsersDialog = wrapper.findComponent({ name: 'ViewUsersDialog' });
    expect(viewUsersDialog.exists()).toBe(true);
    expect(viewUsersDialog.props().course.courseExecutionUsers).toEqual(
      filledCourse.courseExecutionUsers
    );
  });

  test('select upload users and open upload users dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="uploadUsersHandler"]');
    await button.trigger('click');

    const uploadUsersDialog = wrapper.findComponent({
      name: 'UploadUsersDialog',
    });
    expect(uploadUsersDialog.exists()).toBe(true);
  });

  test('select add external user and open add user dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="addExternalUser"]');
    await button.trigger('click');

    const addUserDialog = wrapper.findComponent({
      name: 'AddUserDialog',
    });
    expect(addUserDialog.exists()).toBe(true);
    expect(addUserDialog.props().course.name).toEqual(filledCourse.name);
    expect(addUserDialog.props().course.academicTerm).toEqual(
      filledCourse.academicTerm
    );
  });

  test('select export and invoke remote', async () => {
    const mockExportCourse = jest.spyOn(
      RemoteServices,
      'exportCourseExecutionInfo'
    );

    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="exportCourse"]');
    await button.trigger('click');

    expect(mockExportCourse).toHaveBeenCalled();
    expect(mockExportCourse).toHaveBeenCalledWith(
      filledCourse.courseExecutionId
    );
  });

  // TODO: mock window.confirm
  test.skip('select delete course and invoke remote', async () => {
    const mockDeleteCourse = jest.spyOn(
      RemoteServices,
      'deleteCourseExecution'
    );

    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="deleteCourse"]');
    await button.trigger('click');

    expect(mockDeleteCourse).toHaveBeenCalled();
    expect(mockDeleteCourse).toHaveBeenCalledWith(
      filledCourse.courseExecutionId
    );
  });

  // TODO: mock window.confirm and new Date()
  test.skip('select anonymize students in old tecnico course and invoke remote', async () => {
    const mockAnonymizeCourse = jest.spyOn(RemoteServices, 'anonymizeCourse');

    const mockDate = new Date('2121-04-07T10:20:30Z');
    // jest.spyOn(global, 'Date').mockImplementation(() => Date());

    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    console.log(wrapper.html());

    const button = wrapper.find('button[data-cy="anonymizeCourse"]');
    await button.trigger('click');

    expect(mockAnonymizeCourse).toHaveBeenCalled();
    expect(mockAnonymizeCourse).toHaveBeenCalledWith(
      filledCourse.courseExecutionId
    );
  });

  test('receive new-course event and observe that there is a new course', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="createButton"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'EditCourseDialog' })
      .vm.$emit('new-course', filledCourse);

    expect(wrapper.findComponent({ name: 'EditCourseDialog' }).exists()).toBe(
      false
    );

    const tr = wrapper.findAll('tbody > tr');
    expect(tr.length).toBe(2);
  });

  test('receive close-dialog from create course edit dialog and close dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="createButton"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'EditCourseDialog' })
      .vm.$emit('close-dialog');

    expect(wrapper.findComponent({ name: 'EditCourseDialog' }).exists()).toBe(
      false
    );
  });

  test('receive delete-users event and observe results', async () => {
    const mockDeleteExternalInactiveUsers = jest
      .spyOn(RemoteServices, 'deleteExternalInactiveUsers')
      .mockImplementation(() => {
        return Promise.resolve(filledCourseWithUsers);
      });

    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="viewUsersButton"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'ViewUsersDialog' })
      .vm.$emit('delete-users', [userZe]);

    expect(actions.loading).toHaveBeenCalled();
    await Vue.nextTick();

    expect(mockDeleteExternalInactiveUsers).toHaveBeenCalled();

    await wrapper
      .findComponent({ name: 'ViewUsersDialog' })
      .vm.$emit('close-dialog');
    await wrapper
      .findComponent({ name: 'ViewUsersDialog' })
      .vm.$emit('close-dialog');

    const tr = wrapper.findAll('tbody > tr');
    expect(tr.length).toBe(1);

    const td = tr.at(0).findAll('td');
    expect(td.at(7).text()).toBe('0');
    expect(td.at(9).text()).toBe('0');
  });

  test('receive close-dialog event from view users dialog and close dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="viewUsersButton"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'ViewUsersDialog' })
      .vm.$emit('close-dialog');

    expect(wrapper.findComponent({ name: 'ViewUsersDialog' }).exists()).toBe(
      false
    );
  });

  test('receive users-uploaded event and observe changed state', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="uploadUsersHandler"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'UploadUsersDialog' })
      .vm.$emit('users-uploaded', filledCourseWithUsers);

    await wrapper
      .findComponent({ name: 'UploadUsersDialog' })
      .vm.$emit('close-dialog', filledCourseWithUsers);

    expect(wrapper.findComponent({ name: 'UploadUsersDialog' }).exists()).toBe(
      false
    );

    await Vue.nextTick();

    const tr = wrapper.findAll('tbody > tr');
    expect(tr.length).toBe(1);

    const td = tr.at(0).findAll('td');
    expect(td.at(7).text()).toBe(
      filledCourseWithUsers.numberOfInactiveTeachers?.toString()
    );
  });

  test('receive close-dialog from upload users dialog and close dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="uploadUsersHandler"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'UploadUsersDialog' })
      .vm.$emit('close-dialog');

    expect(wrapper.findComponent({ name: 'UploadUsersDialog' }).exists()).toBe(
      false
    );
  });

  test('receive user-created event and observe changed state', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="addExternalUser"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'AddUserDialog' })
      .vm.$emit('user-created', userBeto);

    await wrapper
      .findComponent({ name: 'AddUserDialog' })
      .vm.$emit('close-dialog', userBeto);

    expect(wrapper.findComponent({ name: 'AddUserDialog' }).exists()).toBe(
      false
    );

    await Vue.nextTick();

    const tr = wrapper.findAll('tbody > tr');
    expect(tr.length).toBe(1);

    const td = tr.at(0).findAll('td');
    expect(td.at(7).text()).toBe('0');
    expect(td.at(9).text()).toBe('0');
  });

  test('receive close-dialog from add external user dialog and close dialog', async () => {
    expect(actions.loading).toHaveBeenCalled();
    expect(actions.clearLoading).toHaveBeenCalled();
    await Vue.nextTick();

    const button = wrapper.find('button[data-cy="addExternalUser"]');
    await button.trigger('click');

    await wrapper
      .findComponent({ name: 'AddUserDialog' })
      .vm.$emit('close-dialog');

    expect(wrapper.findComponent({ name: 'AddUserDialog' }).exists()).toBe(
      false
    );
  });

  // TODO: re-initialize per test the mock for getCourses with different implementations
  test.skip('use hold tecnico course and verify tecnico executions courses only have three options available', async () => {});
});
