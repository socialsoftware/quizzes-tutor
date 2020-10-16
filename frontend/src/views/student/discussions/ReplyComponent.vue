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
              v-if="
                discussion.replies !== null &&
                  discussion.replies.length > 0 &&
                  discussion.closed
              "
              style="width: 12%"
              v-model="discussion.closed"
              :label="discussion.closed ? 'Closed' : 'Reopen'"
              @change="changeDiscussionStatus(discussion.id)"
            />
          </div>
          <v-expansion-panels
            v-if="discussion.replies !== null && discussion.replies.length > 0"
            :inset="true"
          >
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
                  <div style="width: 100%">
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
                  v-if="!discussion.closed && discussion.userId === user.id"
                >
                  <v-textarea
                    data-cy="replyTextArea"
                    class="textarea-reply"
                    solo
                    :id="'reply' + discussion.id"
                    label="Type a reply..."
                    @input="setReplyMessage"
                  ></v-textarea>
                  <v-card-actions>
                    <v-btn
                      data-cy="submitReplyButton"
                      class="submit-button"
                      @click="
                        submitReply();
                        clearTextarea('#reply' + discussion.id);
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
              :id="'reply' + discussion.id"
              label="Type a reply..."
              @input="setReplyMessage"
            ></v-textarea>
            <v-card-actions>
              <v-btn
                class="submit-button"
                @click="
                  submitReply();
                  clearTextarea('#reply' + discussion.id);
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
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '../../../services/RemoteServices';
import User from '@/models/user/User';
import Reply from '@/models/management/Reply';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component
export default class ReplyComponent extends Vue {
  @Prop() readonly discussion!: Discussion;
  replyMessage: string = '';
  user: User = this.$store.getters.getUser;

  async submitReply() {
    if (this.replyMessage.trim() === '') {
      await this.$store.dispatch('error', 'Reply must have content');
      return;
    }

    let reply = new Reply();
    reply.message = this.replyMessage;
    reply.userId = this.user.id!;
    reply.userName = this.user.username;
    reply.date = new Date().toISOString();
    reply.isPublic = false;

    let replyResponse = await RemoteServices.addReply(
      reply,
      this.discussion!.id
    );

    if (this.discussion.replies === null) {
      this.discussion.replies = [];
    }
    this.discussion.replies.push(replyResponse);
    this.discussion.lastReplyDate = replyResponse.date;
  }

  setReplyMessage(message: string) {
    this.replyMessage = message;
  }

  convertMarkDown(text: string) {
    return convertMarkDown(text, null);
  }

  clearTextarea(name: string) {
    let textArea = document.querySelector(name);
    (textArea as HTMLTextAreaElement).value = ' ';
  }

  async changeReplyAvailability(id: number) {
    await this.$store.dispatch('loading');
    await RemoteServices.changeReplyAvailability(id);
    await this.$store.dispatch('clearLoading');
  }

  async changeDiscussionStatus(id: number) {
    await this.$store.dispatch('loading');
    await RemoteServices.changeDiscussionStatus(id);
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
