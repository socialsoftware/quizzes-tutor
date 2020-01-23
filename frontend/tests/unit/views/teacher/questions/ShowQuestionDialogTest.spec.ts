import { shallowMount, Wrapper } from '@vue/test-utils';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import { questionWithFigure } from '../../../samples/Question';
import Vue from 'vue';
import Vuetify from 'vuetify';

describe('ShowQuestionDialog.vue with question', () => {
  let wrapper: Wrapper<ShowQuestionDialog>;
  let dialog: boolean = true;

  beforeEach(() => {
    Vue.use(Vuetify);

    wrapper = shallowMount(ShowQuestionDialog, {
      propsData: { question: questionWithFigure, dialog: dialog }
    });
  });

  it('show a question', () => {
    expect(wrapper.html()).toContain(questionWithFigure.title);
    expect(wrapper.find('.headline').text()).toMatch(questionWithFigure.title);
    expect(wrapper.contains({ name: 'v-card' })).toBe(true);
    expect(wrapper.vm.$props.dialog).toBe(true);
    expect(wrapper.find(ShowQuestion).exists()).toBe(true);
  });
});
