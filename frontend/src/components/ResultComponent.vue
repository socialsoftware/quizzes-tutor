<template>
  <div id="DIV_52" v-if="question">
    <div id="DIV_66">
      <span id="SPAN_67">
        <span class="question-order">{{ order + 1 }}</span></span
      >
      <div
        class="question-content"
        v-html="convertMarkDown(question.content)"
      ></div>
      <div id="DIV_71" @click="increaseOrder">
        <i class="fas fa-chevron-right"></i>
      </div>
    </div>
    <ul class="option-list">
      <li
        v-bind:class="[
          correctAnswer.correctOption === 0 ? 'correct' : '',
          answer.option === 0 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">A</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[0].content)"
        ></span>
      </li>
      <li
        v-bind:class="[
          correctAnswer.correctOption === 1 ? 'correct' : '',
          answer.option === 1 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">B</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[1].content)"
        ></span>
      </li>
      <li
        v-bind:class="[
          correctAnswer.correctOption === 2 ? 'correct' : '',
          answer.option === 2 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">C</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[2].content)"
        ></span>
      </li>
      <li
        v-bind:class="[
          correctAnswer.correctOption === 3 ? 'correct' : '',
          answer.option === 3 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">D</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[3].content)"
        ></span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import Question from "@/models/Question";
import Answer from "@/models/Answer";
import showdown from "showdown";
import CorrectAnswer from "@/models/CorrectAnswer";

@Component
export default class ResultComponent extends Vue {
  @Model("order", Number) order: number | undefined;
  @Prop(Question) readonly question: Question | undefined;
  @Prop(CorrectAnswer) readonly correctAnswer!: CorrectAnswer;
  @Prop(Answer) readonly answer: Answer | undefined;

  constructor() {
    super();
  }

  convertMarkDown(text: string): string {
    const converter = new showdown.Converter();

    if (this.question && this.question.image) {
      text +=
        "  \n  \n  \n[image]: " +
        process.env.VUE_APP_ROOT_API +
        "/images/questions/" +
        this.question.image.url +
        ' "Image"';
    }

    var str = converter.makeHtml(text);
    //remove root paragraphs <p></p>
    str = str.substring(3);
    str = str.substring(0, str.length - 4);

    return str;
  }

  @Emit()
  increaseOrder() {
    return 1;
  }
}
</script>

<style lang="scss">
.wrong {
  .option-content {
    background-color: #cf2323;
  }

  .option-letter {
    background-color: #cf2323;
  }
}

.correct {
  .option-content {
    background-color: #299455;
  }
  .option-letter {
    background-color: #299455;
  }
}

#DIV_52 {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  max-width: 1024px;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 512px 356.5px;
  transform-origin: 512px 356.5px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  background: rgb(255, 255, 255) none repeat scroll 0 0 / auto padding-box
    border-box;
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  margin: 50px auto;
  outline: rgb(51, 51, 51) none 0;
  overflow: hidden;
} /*#DIV_52*/

#DIV_53 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 323px;
  left: 0;
  position: relative;
  right: 0;
  text-align: left;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  top: 0;
  width: 1024px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 512px 161.5px;
  transform-origin: 512px 161.5px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
  padding: 20px 0 20px 20px;
} /*#DIV_53*/

#DIV_54 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  float: left;
  height: 280px;
  left: 0;
  position: relative;
  right: 0;
  text-align: left;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  top: 0;
  width: 422px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 211px 140px;
  transform-origin: 211px 140px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
} /*#DIV_54*/

#DIV_57 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  height: 35px;
  left: 377.422px;
  pointer-events: none;
  position: absolute;
  right: 0;
  text-align: left;
  text-decoration: none solid rgb(255, 255, 255);
  text-size-adjust: 100%;
  top: 245px;
  width: 44.5781px;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 22.2812px 17.5px;
  transform-origin: 22.2891px 17.5px;
  user-select: none;
  caret-color: rgb(255, 255, 255);
  background: rgba(0, 0, 0, 0.66) none repeat scroll 0 0 / auto padding-box
    border-box;
  border: 0 none rgb(255, 255, 255);
  border-radius: 8px 0 0 0;
  font: normal normal 400 normal 20px / 20px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(255, 255, 255) none 0;
  padding: 7px 13px;
} /*#DIV_57*/

#DIV_59 {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  float: right;
  height: 283px;
  text-align: center;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  width: 581px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 290.5px 141.5px;
  transform-origin: 290.5px 141.5px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
} /*#DIV_59*/

#DIV_60 {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 283px;
  text-align: center;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  width: 581px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 290.5px 141.5px;
  transform-origin: 290.5px 141.5px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
} /*#DIV_60*/

#DIV_65 {
  box-sizing: border-box;
  clear: both;
  color: rgb(51, 51, 51);
  height: 0;
  text-align: left;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  width: 1004px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 502px 0;
  transform-origin: 502px 0;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
} /*#DIV_65*/

#DIV_66 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  display: table;
  left: 0;
  position: relative;
  right: 0;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  top: 0;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 492px 35px;
  transform-origin: 492px 35px;
  user-select: text;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 700 normal 18px / 18px "Open Sans", Helvetica, Arial,
    sans-serif;
  margin: 0 20px 0 20px;
  outline: rgb(51, 51, 51) none 0;
  overflow: hidden;
} /*#DIV_66*/

#SPAN_67,
#DIV_71 {
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  cursor: pointer;
  display: table-cell;
  height: 70px;
  min-height: 70px;
  min-width: 70px;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-shadow: rgb(96, 107, 116) 2px 2px 0;
  text-size-adjust: 100%;
  vertical-align: middle;
  width: 70px;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 35px 35px;
  transform-origin: 35px 35px;
  user-select: none;
  caret-color: rgb(255, 255, 255);
  background: rgb(108, 120, 130) none repeat scroll 0 0 / auto padding-box
    border-box;
  border: 0 none rgb(255, 255, 255);
  font: normal normal 700 normal 44px / 70px "Open Sans", Helvetica, Arial,
    sans-serif;
  outline: rgb(255, 255, 255) none 0;
} /*#SPAN_67, #DIV_71*/

.question-order {
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  cursor: pointer;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-shadow: rgb(96, 107, 116) 2px 2px 0;
  text-size-adjust: 100%;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 0 0;
  transform-origin: 0 0;
  user-select: none;
  caret-color: rgb(255, 255, 255);
  border: 0 none rgb(255, 255, 255);
  font: normal normal 700 normal 44px / 70px "Open Sans", Helvetica, Arial,
    sans-serif;
  outline: rgb(255, 255, 255) none 0;
}

.question-content {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  display: table-cell;
  height: 70px;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  vertical-align: middle;
  width: 844px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 422px 35px;
  transform-origin: 422px 35px;
  user-select: text;
  caret-color: rgb(51, 51, 51);
  background: rgba(108, 120, 130, 0.21) none repeat scroll 0 0 / auto
    padding-box border-box;
  border: 0 none rgb(51, 51, 51);
  font: normal normal 700 normal 18px / 18px "Open Sans", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
  padding: 10px;
}

img {
  width: 80%;
  margin: 0 auto;
}

.selected .option-letter {
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  cursor: pointer;
  display: block;
  float: left;
  height: 70px;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-size-adjust: 100%;
  vertical-align: middle;
  width: 70px;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 35px 35px;
  transform-origin: 35px 35px;
  user-select: text;
  caret-color: rgb(255, 255, 255);
  background: rgb(61, 69, 76) none repeat scroll 0% 0% / auto padding-box
    border-box;
  border: 0px none rgb(255, 255, 255);
  font: normal normal 400 normal 50px / 70px "Varela Round", Helvetica, Arial,
    sans-serif;
  list-style: none outside none;
  outline: rgb(255, 255, 255) none 0px;
}

.selected .option-content {
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  cursor: pointer;
  display: table-cell;
  height: 70px;
  text-align: left;
  text-decoration: none solid rgb(255, 255, 255);
  text-size-adjust: 100%;
  vertical-align: middle;
  width: 914px;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 457px 35px;
  transform-origin: 457px 35px;
  user-select: text;
  caret-color: rgb(255, 255, 255);
  background: rgb(61, 69, 76) none repeat scroll 0% 0% / auto padding-box
    border-box;
  border: 0px none rgb(255, 255, 255);
  font: normal normal 400 normal 17px / 17px "Varela Round", Helvetica, Arial,
    sans-serif;
  list-style: none outside none;
  outline: rgb(255, 255, 255) none 0px;
  padding: 0px 20px;
}

.option-list {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 280px;
  left: 0;
  position: relative;
  right: 0;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 492px 105px;
  transform-origin: 492px 105px;
  user-select: text;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  border-radius: 0 0 3px 3px;
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  list-style: none outside none;
  margin: 0 20px 0 20px;
  outline: rgb(51, 51, 51) none 0;
  overflow: hidden;
  padding: 0;
}

.option {
  box-sizing: border-box;
  clear: left;
  color: rgb(51, 51, 51);
  cursor: pointer;
  display: table;
  height: 70px;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  width: 984px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 492px 35px;
  transform-origin: 492px 35px;
  user-select: text;
  caret-color: rgb(51, 51, 51);
  background: rgb(255, 255, 255) none repeat scroll 0 0 / auto padding-box
    border-box;
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  list-style: none outside none;
  outline: rgb(51, 51, 51) none 0;
}

.option-letter {
  box-sizing: border-box;
  color: rgb(105, 105, 105);
  cursor: pointer;
  display: block;
  float: left;
  height: 70px;
  text-align: center;
  text-decoration: none solid rgb(105, 105, 105);
  text-size-adjust: 100%;
  vertical-align: middle;
  width: 70px;
  column-rule-color: rgb(105, 105, 105);
  perspective-origin: 35px 35px;
  transform-origin: 35px 35px;
  user-select: text;
  caret-color: rgb(105, 105, 105);
  background: rgb(247, 247, 247) none repeat scroll 0 0 / auto padding-box
    border-box;
  border: 0 none rgb(105, 105, 105);
  font: normal normal 400 normal 50px / 70px "Varela Round", Helvetica, Arial,
    sans-serif;
  list-style: none outside none;
  outline: rgb(105, 105, 105) none 0;
}

.option-content {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  cursor: pointer;
  display: table-cell;
  height: 70px;
  text-align: left;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  vertical-align: middle;
  width: 914px;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 457px 35px;
  transform-origin: 457px 35px;
  user-select: text;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 17px / 17px "Varela Round", Helvetica, Arial,
    sans-serif;
  list-style: none outside none;
  outline: rgb(51, 51, 51) none 0;
  padding: 0 20px;
}
</style>
