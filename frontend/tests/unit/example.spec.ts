import { shallowMount } from '@vue/test-utils';
import HelloWorld from '@/components/HelloWorld.vue';

describe('HelloWorld.vue', () => {
  it('renders props.msg when passed', () => {
    const msg = 'new message';
    const wrapper = shallowMount(HelloWorld, {
      propsData: { msg }
    });
    expect(wrapper.text()).toMatch(msg);
  });
});

// import { shallowMount, createLocalVue } from '@vue/test-utils';
// import Vuex from 'vuex';
// const localVue = createLocalVue();
// localVue.use(Vuex);
// import axios from 'axios';
// import MockAdapter from 'axios-mock-adapter';
// import RemoteServices from '@/services/RemoteServices';
//
// describe('RemoteServices.ts', () => {
//   beforeEach(() => {
//     let actions = {
//       loading: jest.fn(),
//       clearLoading: jest.fn(),
//       error: jest.fn()
//     };
//     let store = new Vuex.Store({
//       actions
//     });
//   });
//
//   it('renders stats when passed', async () => {
//
//     let mockAdapter = new MockAdapter(axios);
//     mockAdapter.onGet().reply(200, data);
//     let spy = jest.spyOn(axios, 'get');
//
//     try {
//       let a = await RemoteServices.demoStudentLogin();
//       expect(a).toEqual('b');
//     } catch (e) {
//       expect(e.getValue()).toMatch('Unable to connect to server');
//     }
//
//     expect(spy).toHaveBeenCalled();
//     expect(3).toEqual(3);
//   });
// });
