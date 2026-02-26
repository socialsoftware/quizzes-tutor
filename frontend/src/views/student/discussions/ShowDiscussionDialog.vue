<template>
  <v-dialog
    :value="dialog"
    @input="$emit('update:dialog', false)"
    @keydown.esc="$emit('update:dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ discussion.question?.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question v-if="discussion.question" :question="discussion.question" />
      </v-card-text>

      <v-card-text class="text-left">
        <v-card>
          <div class="reply-container" v-if="discussion !== null">
            <div class="discussion">
              <ul>
                <li
                  style="margin-bottom: 25px !important"
                  :key="discussion.message"
                >
                  <div style="display: inline-flex; width: 100%">
                    <div style="width: 88%" class="text-left">
                      <b v-if="user?.role !== 'TEACHER'"
                        >You opened a discussion on {{ discussion.date }} :
                      </b>
                      <b v-else
                        >{{ discussion.name }} ({{ discussion.username }})
                        opened a discussion on {{ discussion.date }} :
                      </b>
                      <span v-html="convertMarkDownText(discussion.message)" />
                    </div>
                    <v-switch
                      v-if="
                        discussion.replies.length > 0 &&
                        ((user?.role === 'STUDENT' && discussion.closed) ||
                          user?.role === 'TEACHER')
                      "
                      style="width: 12%"
                      v-model="discussion.closed"
                      :label="discussion.closed ? 'Closed' : 'Reopen'"
                      @change="changeDiscussionStatus(discussion.id)"
                    />
                  </div>
                  <reply-component
                    v-if="discussion != null"
                    :discussion="discussion"
                  />
                </li>
              </ul>
            </div>
          </div>
        </v-card>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          dark
          color="blue darken-1"
          data-cy="showDiscussionDialogCloseButton"
          @click="$emit('update:dialog', false)"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { useStore } from '@/store';
import Discussion from '@/models/management/Discussion';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ReplyComponent from '@/views/student/discussions/ReplyComponent.vue';
import User from '@/models/user/User';
import RemoteServices from '@/services/RemoteServices';

const props = defineProps<{
  dialog: boolean;
  discussion: Discussion;
}>();

const emit = defineEmits(['update:dialog']);

const store = useStore();
const user = store.user as User | null;

const convertMarkDownText = (text: string) => {
  return convertMarkDown(text, null);
};

const changeDiscussionStatus = async (id: number) => {
  store.setLoading();
  await RemoteServices.changeDiscussionStatus(id);
  store.clearLoading();
};
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
