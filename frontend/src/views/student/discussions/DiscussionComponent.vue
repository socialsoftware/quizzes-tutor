<template>
  <div class="discussion-container" v-if="answered || hasDiscussion">
    <v-card>
      <v-card-title
        class="justify-center headline"
        style="background-color: #1976d2; color: white"
      >
        Discussions
      </v-card-title>
      <div v-if="answered && !hasDiscussion" class="discussion-message">
        <v-textarea
          clearable
          solo
          v-model="discussionMessage"
          v-on:input="onInput"
          name="input-7-4"
          label="Enter discussion message here"
        ></v-textarea>
        <v-card-actions>
          <v-spacer />
          <v-btn class="submit-button" @click="submitDiscussion">Submit</v-btn>
        </v-card-actions>
      </div>
      <reply-component v-if="discussions != null" :discussions="discussions" />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit, Watch } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Discussion from '@/models/management/Discussion';
import ReplyComponent from '@/views/student/discussions/ReplyComponent.vue';

@Component({
  components: { 'reply-component': ReplyComponent }
})
export default class DiscussionComponent extends Vue {
  @Prop(Boolean) readonly hasDiscussion!: boolean;
  @Prop() readonly question?: Question;
  @Prop() readonly discussions?: Discussion[];
  @Prop(Boolean) readonly answered!: boolean;
  discussionMessage: string = '';

  @Emit()
  submitDiscussion() {
    this.discussionMessage = '';
    return 1;
  }

  @Emit('discussionMessage')
  onInput() {
    return this.discussionMessage;
  }

  @Watch('question')
  onQuestionChange() {
    this.discussionMessage = '';
  }

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
  }
}
</script>

<style lang="scss" scoped>
.discussion-container {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  max-width: 1024px;
  text-decoration: none solid;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  overflow: hidden;
  margin: -100px auto 100px;
  border-radius: 0;

  .comp-title {
    padding: 5px !important;
  }

  .discussion-message {
    width: 95%;
    margin: 20px auto auto;
  }

  .discussion {
    width: 100%;
    left: 0;
    margin: 5px;
    padding: 15px;
    border-top: #1e88e5 solid 2px;
  }

  .submit-button {
    background-color: #1976d2 !important;
    color: white;
  }
}
</style>
