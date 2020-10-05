<template>
  <div class="reply-container" v-if="discussion !== null">
    <div class="discussion">
      <ul>
        <li style="margin-bottom: 25px !important;" :key="discussion.message">
          <div style="display: inline-flex; width: 100%">
            <div style="width: 88%" class="text-left">
              <b v-if="user.id !== discussion.userId"
                >{{ user.username }} opened a discussion on
                {{ discussion.date }} :
              </b>
              <b v-else>You opened a discussion on {{ discussion.date }} :</b>
              <span v-html="convertMarkDown(discussion.message)" />
            </div>
            <v-switch
              v-if="discussion.replies !== null"
              style="width: 12%"
              v-model="discussion.closed"
              :label="discussion.closed ? 'Closed' : 'Open'"
              @change="changeDiscussionStatus(discussion.id)"
            />
          </div>
          <v-expansion-panels v-if="discussion.replies !== null" :inset="true">
            <v-expansion-panel>
              <v-expansion-panel-header style="background-color: #d5d5d5"
                >Show replies
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <div
                  v-for="reply in discussion.replies"
                  :key="reply.id"
                  class="text-left reply"
                >
                  <div
                    style="display: inline-flex; width: 100%"
                    v-if="user.role === 'TEACHER'"
                  >
                    <div style="width: 88%">
                      <b v-if="user.id !== reply.userId"
                        >{{ reply.userName }} replied on {{ reply.date }} :
                      </b>
                      <b v-else>You replied on {{ reply.date }} :</b>
                      <span v-html="convertMarkDown(reply.message)" />
                    </div>
                    <v-switch
                      style="width: 12%"
                      v-model="reply.available"
                      :label="reply.available ? 'Public' : 'Private'"
                      @change="changeReplyAvailability(reply.id)"
                    />
                  </div>
                  <div style="width: 100%" v-else>
                    <div>
                      <b v-if="user.id !== reply.userId"
                        >{{ reply.userName }} replied on {{ reply.date }} :
                      </b>
                      <b v-else>You replied on {{ reply.date }} :</b>
                      <span v-html="convertMarkDown(reply.message)" />
                    </div>
                  </div>
                </div>
                <div
                  class="reply-message"
                  v-if="
                    !discussion.closed &&
                      (discussion.userId === user.id || user.role === 'TEACHER')
                  "
                >
                  <v-textarea
                    data-cy="replyTextArea"
                    class="textarea-reply"
                    solo
                    :id="'reply' + discussion.userId"
                    label="Type a reply..."
                    @input="setReplyMessage"
                  ></v-textarea>
                  <v-card-actions>
                    <v-btn
                      data-cy="submitReplyButton"
                      class="submit-button"
                      @click="
                        submitReply();
                        clearTextarea('#reply' + discussion.userId);
                      "
                      >Submit</v-btn
                    >
                  </v-card-actions>
                </div>
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>
          <div v-else class="reply-message">
            <v-textarea
              class="textarea-reply"
              solo
              :id="'reply' + discussion.userId"
              label="Type a reply..."
              @input="setReplyMessage"
            ></v-textarea>
            <v-card-actions>
              <v-btn
                class="submit-button"
                @click="
                  submitReply();
                  clearTextarea('#reply' + discussion.userId);
                "
                >Submit</v-btn
              >
            </v-card-actions>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '../../../services/RemoteServices';
import User from '@/models/user/User';

@Component
export default class ReplyComponent extends Vue {
  @Prop() readonly discussion!: Discussion;
  replyMessages: Map<number, string> = new Map();
  user: User = this.$store.getters.getUser;

  @Emit()
  async submitReply() {
    try {
      if (this.replyMessages.get(this.discussion.userId!) === undefined) {
        this.replyMessages.set(this.discussion.userId!, '');
      }
      const reply = await RemoteServices.createReply(
        this.replyMessages.get(this.discussion.userId!)!,
        this.discussion!
      );

      if (this.discussion.replies === null) {
        this.discussion.replies = [];
      }

      this.discussion.replies.push(reply);

      this.replyMessages.set(this.discussion.userId!, '');
    } catch (error) {
      await this.$store.dispatch('error', error);

      return false;
    }

    return true;
  }

  setReplyMessage(message: string) {
    this.replyMessages.set(this.discussion.userId!, message);
  }

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
  }

  clearTextarea(name: string) {
    let textArea: HTMLTextAreaElement;
    let val = document.querySelector(name)!;
    textArea = val as HTMLTextAreaElement;
    textArea.value = '';
  }

  async changeReplyAvailability(id: number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.changeReplyAvailability(id);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeDiscussionStatus(id: number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.changeDiscussionStatus(id);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.reply-container {
  ul {
    list-style-type: none;
  }

  .reply-message {
    width: 95%;
    margin: 0 25px;
  }

  .discussion {
    width: 100%;
    margin: 5px;
    padding: 25px 25px 2px;
  }

  .reply {
    margin: 5px;
    padding: 15px 15px 0 30px;
  }

  .submit-button {
    background-color: #1976d2 !important;
    color: white;
    margin-left: 89%;
  }

  .textarea-reply {
    margin-bottom: -18px;
  }
}
</style>
