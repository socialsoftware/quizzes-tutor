<template>
  <v-card max-width="1200" class="mx-auto my-7">
    <v-data-table
      :headers="headers"
      :items="discussions"
      :sort-by="[{ key: 'lastReplyDate', order: 'desc' }]"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title style="width: 50%">
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
          />
        </v-card-title>
      </template>
      <template v-slot:[`item.closed`]="{ item }">
        <v-chip v-if="item.closed === true" :color="'green'" dark>Yes</v-chip>
        <v-chip v-else :color="'red'" dark>No</v-chip>
      </template>
      <template v-slot:[`item.replies.length`]="{ item }">
        <v-chip v-if="item.replies === null" :color="'grey'" dark>0</v-chip>
        <v-chip v-else :color="'grey'" dark>{{ item.replies.length }}</v-chip>
      </template>

      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              data-cy="showDiscussionButton"
              class="mr-2 action-button"
              v-bind="activatorProps"
              @click="showDiscussionDialogAction(item)"
              >fas fa-comment-dots</v-icon
            >
          </template>
          <span>Show Discussion</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-discussion-dialog
      v-if="currentDiscussion"
      v-model:dialog="discussionDialog"
      :discussion="currentDiscussion"
      v-on:close-show-question-dialog="onCloseShowDiscussionDialog"
    />
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Discussion from '@/models/management/Discussion';
import ShowDiscussionDialog from '@/views/student/discussions/ShowDiscussionDialog.vue';

const props = defineProps<{
  discussions: Discussion[];
}>();

const search = ref('');
const currentDiscussion = ref<Discussion | null>(null);
const discussionDialog = ref(false);

const headers = [
  { title: 'Actions', key: 'action', align: 'start', width: '5px', sortable: false },
  { title: 'Discussion Number', key: 'id' },
  { title: 'Question Title', key: 'question.title' },
  { title: 'Question Content', key: 'question.content' },
  { title: 'Message', key: 'message' },
  { title: 'Last Reply Date', key: 'lastReplyDate' },
  { title: 'Closed', key: 'closed' },
  { title: 'Replies', key: 'replies.length' },
] as const;

const showDiscussionDialogAction = (discussion: Discussion) => {
  currentDiscussion.value = discussion;
  discussionDialog.value = true;
};

const onCloseShowDiscussionDialog = () => {
  currentDiscussion.value = null;
  discussionDialog.value = false;
};
</script>

<style scoped></style>
