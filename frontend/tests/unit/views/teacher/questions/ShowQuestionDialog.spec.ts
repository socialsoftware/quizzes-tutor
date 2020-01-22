import { shallowMount, Wrapper } from "@vue/test-utils";
import ShowQuestionDialog from "@/views/teacher/questions/ShowQuestionDialog.vue";
import ShowQuestion from "@/views/teacher/questions/ShowQuestion.vue";
import question from "../../../samples/Question";
import Vue from "vue";
import Vuetify from "vuetify";

describe("ShowQuestionDialog.vue with question", () => {
  let wrapper: Wrapper<ShowQuestionDialog>;
  let dialog: boolean = true;

  beforeEach(() => {
    Vue.use(Vuetify);

    wrapper = shallowMount(ShowQuestionDialog, {
      propsData: { question, dialog }
    });
  });

  it("show a question", () => {
    expect(wrapper.find(".headline").text()).toMatch(question.title);
    expect(wrapper.find(ShowQuestion).exists()).toBe(true);
    // expect(wrapper.find({ name: "v-card" });
  });
});
