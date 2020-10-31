<template>
  <div id="ViewCodeMirror">
    <BaseCodeEditor ref="myCmView" 
      :code.sync="questionDetails.code"
      :language.sync="questionDetails.language"
      />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import CodeFillInQuestionDetails from '@/models/management/questions/CodeFillInQuestionDetails';
import FillInOptions from '@/components/questions/FillInOptions.vue';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import CodeFillInSpot from '@/models/management/questions/CodeFillInSpot';

@Component({
  components: {
    BaseCodeEditor
  }
})
export default class ShowCodeFillInQuestion extends Vue {
  @Prop({ type: CodeFillInQuestionDetails, required: true })
  readonly questionDetails!: CodeFillInQuestionDetails;
  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
  replaceDropdowns() {
    function shuffle(arr: any) {
      const array = arr.slice();
      let currentIndex = array.length;
      let temporaryValue;
      let randomIndex;
      while (currentIndex !== 0) {
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;
        temporaryValue = array[currentIndex];
        array[currentIndex] = array[randomIndex];
        array[randomIndex] = temporaryValue;
      }
      return array;
    }
    function createOptionChild(optText: any, index: any) {
      const o = document.createElement('option');
      o.appendChild(
        document.createTextNode(
          (optText.correct ? ' ✔: ' : ' ✖: ') + optText.content
        )
      );
      o.value = index;
      return o;
    }
    function addOptions(select: any, options: any) {
      console.log(options);
      options.forEach((opt: any, i: any) => {
        select.appendChild(createOptionChild(opt, i));
      });
    }
    function getOptions(name: number, options: CodeFillInSpot[]) {
      const result = options.find(el => el.sequence === name);
      return result ? result.options : result;
    }
    console.log("SELECT", document.querySelectorAll('.cm-custom-drop-down'));
    document.querySelectorAll('.cm-custom-drop-down').forEach((e, index) => {
      console.log(e.innerHTML);
      const d = document.createElement('select');
      d.className = 'code-dropdown';
      d.name = e.innerHTML;
      console.log(e.parentNode);
      console.log(d);
      e.parentNode.replaceChild(d, e);
      var num = e.innerHTML.match(/\d+/)[0];
      addOptions(d, getOptions(Number(num) ,this.questionDetails.fillInSpots));
    });
  }

  updateQuestion() {
    this.$refs.myCmView.codemirror.refresh();
    this.replaceDropdowns();
    document.body.addEventListener(
      'mousedown',
      function(evt: Event) {
        if (evt && evt.target && evt.target.className === 'code-dropdown') {
          evt.stopPropagation();
        }
      },
      true
    );
  }

  mounted() {
    setTimeout(() => {
      this.updateQuestion();
    }, 1000);
  }
}
</script>

<style>
#ViewCodeMirror select.code-dropdown.incorrect {
  background-color: rgba(187, 36, 36, 0.76);
}
#ViewCodeMirror select.code-dropdown.correct {
  background-color: rgba(33, 201, 33, 0.76);
}
#ViewCodeMirror select.code-dropdown {
  background-color: transparent;
  color: inherit;
  border-radius: 0px;
  border-width: 0 0 1px 0;
  border-style: solid;
  border-color: rgb(169, 169, 169);
  padding: 0;
}
#ViewCodeMirror select.code-dropdown option {
  color: #272822;
}
#ViewCodeMirror .CodeMirror {
  border: 1px solid #eee;
  height: auto;
}
</style>