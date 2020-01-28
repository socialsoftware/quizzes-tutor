import { shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
const localVue = createLocalVue();
localVue.use(Vuex);

import StatsView from '@/views/student/StatsView.vue';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

describe('StatsView.vue', () => {
  let store: any;
  let actions;

  beforeEach(() => {
    actions = {
      loading: jest.fn(),
      clearLoading: jest.fn(),
      error: jest.fn()
    };
    store = new Vuex.Store({
      actions
    });
  });

  it('renders stats when passed', () => {
    const data = {
      totalQuizzes: 2,
      totalAnswers: 10,
      totalUniqueQuestions: 10,
      correctAnswers: 20.0,
      improvedCorrectAnswers: 20.0,
      uniqueCorrectAnswers: 0,
      uniqueWrongAnswers: 0,
      totalAvailableQuestions: 460
    };

    let mockAdapter = new MockAdapter(axios);
    mockAdapter.onGet().reply(200, data);

    const created = jest.fn();

    const wrapper = shallowMount(StatsView, {
      store,
      localVue,
      methods: {
        created
      }
    });

    expect(wrapper.find('.container h2').text()).toBe('Statistics');
    // expect(wrapper.find(".stats-container").html()).toBe("Statistics");
    // expect(wrapper.attributes().stats).toBe(data);
    // expect(wrapper.find({ ref: "improvedCorrectAnswers" }).exists()).toBe(true);
    // const totalQuizzes = wrapper.find(".icon-wrapper");
  });
});
