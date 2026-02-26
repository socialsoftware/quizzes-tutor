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
          <div v-if="user?.role === 'STUDENT'" style="width: 100%">
            <div>
              <b v-if="user?.id !== reply.userId"
                >{{ reply.name }} ({{ reply.username }}) replied on
                {{ reply.date }} :
              </b>
              <b v-else>You replied on {{ reply.date }} :</b>
              <span v-html="convertMarkDownText(reply.message)" />
            </div>
          </div>
          <div v-else style="display: inline-flex; width: 100%">
            <div style="width: 88%">
              <b v-if="user?.id !== reply.userId"
                >{{ reply.name }} ({{ reply.username }}) replied on
                {{ reply.date }}:
              </b>
              <b v-else>You replied on {{ reply.date }} :</b>
              <span v-html="convertMarkDownText(reply.message)" />
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

<script setup lang="ts">
import { ref } from 'vue';
import { useStore } from '@/store';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '../../../services/RemoteServices';
import User from '@/models/user/User';
import Reply from '@/models/management/Reply';

const props = defineProps<{
  discussion: Discussion;
}>();

const store = useStore();
const replyMessage = ref('');
const user = store.user as User | null;

const submitReply = async () => {
  if (replyMessage.value.trim() === '') {
    store.setError('Reply must have content');
    return;
  }

  let reply = new Reply();
  reply.message = replyMessage.value;
  reply.username = user?.username ?? '';
  reply.date = new Date().toISOString();

  try {
    let replyResponse = await RemoteServices.addReply(
      reply,
      props.discussion.id
    );

    if (props.discussion.replies === null) {
      props.discussion.replies = [];
    }
    props.discussion.replies.push(replyResponse);
    props.discussion.lastReplyDate = replyResponse.date;

    replyMessage.value = '';
  } catch (error) {
    store.setError(error as string);
  }
};

const setReplyMessage = (message: string) => {
  replyMessage.value = message;
};

const convertMarkDownText = (text: string) => {
  return convertMarkDown(text, null);
};

const clearTextarea = (name: string) => {
  let textArea = document.querySelector(name);
  if (textArea) {
    (textArea as HTMLTextAreaElement).value = ' ';
  }
};

const changeReplyAvailability = async (id: number) => {
  store.setLoading();
  try {
    await RemoteServices.changeReplyAvailability(id);
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
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
