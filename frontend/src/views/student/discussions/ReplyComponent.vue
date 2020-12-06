<template>
  <v-expansion-panels v-if="discussion.replies.length > 0" :inset="true">
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
          <div v-if="user.role === 'STUDENT'" style="width: 100%">
            <div>
              <b v-if="user.id !== reply.userId"
                >{{ reply.name }} ({{ reply.username }}) replied on
                {{ reply.date }} :
              </b>
              <b v-else>You replied on {{ reply.date }} :</b>
              <span v-html="convertMarkDown(reply.message)" />
            </div>
          </div>
          <div v-else style="display: inline-flex; width: 100%">
            <div style="width: 88%">
              <b v-if="user.id !== reply.userId"
                >{{ reply.name }} ({{ reply.username }}) replied on
                {{ reply.date }}:
              </b>
              <b v-else>You replied on {{ reply.date }} :</b>
              <span v-html="convertMarkDown(reply.message)" />
            </div>
            <v-switch
              style="width: 12%"
              v-model="reply.public"
              :label="reply.public ? 'Public' : 'Private'"
              @change="changeReplyAvailability(reply.id)"
            />
          </div>
        </div>
        <div class="reply-message" v-if="!discussion.closed">
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
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '../../../services/RemoteServices';
import User from '@/models/user/User';
import Reply from '@/models/management/Reply';

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
    reply.username = this.user.username;
    reply.date = new Date().toISOString();

    let replyResponse = await RemoteServices.addReply(
      reply,
      this.discussion!.id
    );

    if (this.discussion.replies === null) {
      this.discussion.replies = [];
    }
    this.discussion.replies.push(replyResponse);
    this.discussion.lastReplyDate = replyResponse.date;

    this.replyMessage = '';
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
}
</script>
<style lang="scss" scoped>
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
  color: white !important;
  margin-left: 89%;
}

.textarea-reply {
  margin-bottom: -18px;
}
/*
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
}*/
</style>
