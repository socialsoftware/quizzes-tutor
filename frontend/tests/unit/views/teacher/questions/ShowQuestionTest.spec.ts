import { mount, Wrapper } from '@vue/test-utils';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import {
  questionWithFigure,
  questionWithoutFigure
} from '../../../samples/Question';
import Vue from 'vue';
import Vuetify from 'vuetify';
import Option from '@/models/management/Option';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';

describe('ShowQuestion.vue with question', () => {
  let wrapper: Wrapper<ShowQuestion>;

  beforeEach(() => {
    Vue.use(Vuetify);
  });

  it('show a question without figure', () => {
    wrapper = mount(ShowQuestion, {
      propsData: { question: questionWithoutFigure }
    });

    expect(wrapper.html()).toContain(questionWithoutFigure.content);
    expect(wrapper.find('div span').text()).toMatch(
      questionWithoutFigure.content
    );
    var question = questionWithoutFigure.questionDetailsDto as MultipleChoiceQuestionDetails;
    question.options.forEach((option: Option) => {
      expect(wrapper.html()).toContain(option.content);
    });
  });

  it('show a question with figure', () => {
    wrapper = mount(ShowQuestion, {
      propsData: { question: questionWithFigure }
    });

    expect(wrapper.html()).toContain('395.png');
    expect(wrapper.find('div span img').html()).toContain('395.png');
  });
});
