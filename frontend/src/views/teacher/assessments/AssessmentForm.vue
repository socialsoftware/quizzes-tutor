<template>
  <v-card v-if="editMode && assessment" class="table">
    <v-card-title>
      <span>Create Assessment</span>
      <v-spacer />
      <v-btn color="primary" dark @click="$emit('switchMode')">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn color="green darken-1" @click="saveAssessment">Save</v-btn>
    </v-card-title>
    <v-card-text>
      <v-container fluid>
        <v-row>
          <v-col>
            <v-text-field
              v-model="assessment.title"
              label="Title"
            ></v-text-field>
          </v-col>

          <v-col>
            <v-text-field
              min="0"
              step="1"
              type="number"
              label="Order"
              :value="assessment.sequence"
              @change="assessment.sequence = Number($event)"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-row>
          <v-col class="light-green lighten-4">
            <v-data-table
              :headers="topicHeaders"
              :custom-filter="topicFilter"
              :items="assessment.topicConjunctions"
              :search="JSON.stringify(currentTopicsSearch)"
              :mobile-breakpoint="0"
              :items-per-page="15"
              :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
            >
              <template v-slot:top>
                <h2>Currently selected</h2>
                <v-autocomplete
                  v-model="currentTopicsSearch"
                  label="Search"
                  :items="allTopics"
                  :filter="topicSearch"
                  :search-input.sync="currentTopicsSearchText"
                  @change="currentTopicsSearchText = ''"
                  item-text="name"
                  return-object
                  chips
                  small-chips
                  clearable
                  deletable-chips
                  multiple
                  dense
                  class="mx-4"
                >
                </v-autocomplete>
              </template>
              <template v-slot:[`item.topics`]="{ item }">
                <div v-if="item.topics.length > 0">
                  <v-chip v-for="topic in item.topics" :key="topic.id">
                    {{ topic.name }}
                  </v-chip>
                </div>
                <div v-else>No Topic</div>
              </template>
              <template v-slot:[`item.action`]="{ item }">
                <v-tooltip bottom>
                  <template v-slot:activator="{ on }">
                    <v-icon
                      class="mr-2 action-button"
                      v-on="on"
                      @click="removeTopicConjunction(item)"
                    >
                      chevron_right</v-icon
                    >
                  </template>
                  <span>Remove from Assessment</span>
                </v-tooltip>

                <v-tooltip bottom>
                  <template v-slot:activator="{ on }">
                    <v-icon
                      class="mr-2 action-button"
                      v-on="on"
                      @click="showQuestionsDialog(item)"
                    >
                      visibility</v-icon
                    >
                  </template>
                  <span>Show Questions</span>
                </v-tooltip>
              </template>
            </v-data-table>
          </v-col>
          <v-col class="red lighten-4">
            <v-data-table
              :headers="topicHeaders"
              :custom-filter="topicFilter"
              :items="topicConjunctions"
              :search="JSON.stringify(allTopicsSearch)"
              :mobile-breakpoint="0"
              :items-per-page="15"
              :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
            >
              <template v-slot:top>
                <h2>Available topics</h2>
                <v-autocomplete
                  v-model="allTopicsSearch"
                  label="Search"
                  :items="allTopics"
                  :filter="topicSearch"
                  :search-input.sync="allTopicsSearchText"
                  @change="allTopicsSearchText = ''"
                  item-text="name"
                  return-object
                  chips
                  small-chips
                  clearable
                  deletable-chips
                  multiple
                  dense
                  class="mx-4"
                >
                </v-autocomplete>
              </template>
              <template v-slot:[`item.topics`]="{ item }">
                <div v-if="item.topics.length > 0">
                  <div v-if="item.topics">
                    <v-chip v-for="topic in item.topics" :key="topic.id">
                      {{ topic.name }}
                    </v-chip>
                  </div>
                  <div v-else>No Topic</div>
                </div>
                <div v-else>No Topic</div>
              </template>
              <template v-slot:[`item.action`]="{ item }">
                <v-tooltip bottom>
                  <template v-slot:activator="{ on }">
                    <v-icon
                      class="mr-2 action-button"
                      v-on="on"
                      @click="addTopicConjunction(item)"
                    >
                      chevron_left</v-icon
                    >
                  </template>
                  <span>Add to Assessment</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on }">
                    <v-icon
                      class="mr-2 action-button"
                      v-on="on"
                      @click="showQuestionsDialog(item)"
                    >
                      visibility</v-icon
                    >
                  </template>
                  <span>Show Questions</span>
                </v-tooltip>
              </template>
            </v-data-table>
          </v-col>
        </v-row>
      </v-container>
    </v-card-text>

    <show-question-list-dialog
      :dialog="questionsDialog"
      :questions="questionsToShow"
      v-on:close="onCloseQuestionsDialog"
    ></show-question-list-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/management/Assessment';
import Question from '@/models/management/Question';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import TopicConjunction from '@/models/management/TopicConjunction';
import Topic from '@/models/management/Topic';
import ShowQuestionListDialog from '@/views/teacher/questions/ShowQuestionListDialog.vue';

@Component({
  components: { ShowQuestionListDialog },
})
export default class AssessmentForm extends Vue {
  @Prop({ type: Assessment, required: true }) readonly assessment!: Assessment;
  @Prop(Boolean) readonly editMode!: boolean;
  currentTopicsSearch: string = '';
  currentTopicsSearchText: string = '';
  allTopicsSearch: string = '';
  allTopicsSearchText: string = '';

  questionsDialog: boolean = false;
  allTopics: Topic[] = [];
  topicConjunctions: TopicConjunction[] = [];
  questionsToShow: Question[] = [];

  topicHeaders: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'left',
      sortable: false,
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.topicConjunctions, this.allTopics] = await Promise.all([
        RemoteServices.getTopicConjunctions(
          this.assessment.id ? this.assessment.id : 0
        ),
        RemoteServices.getTopics(),
      ]);
      let assessmentTopicConjunctionIds = this.assessment.topicConjunctions.map(
        (topicConjunction: TopicConjunction) => {
          return topicConjunction.id;
        }
      );
      this.topicConjunctions = this.topicConjunctions.filter(
        (topicConjunction) =>
          !assessmentTopicConjunctionIds.includes(topicConjunction.id)
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  topicFilter(
    value: string,
    search: string,
    topicConjunction: TopicConjunction
  ) {
    let searchTopics = JSON.parse(search);

    if (searchTopics !== '') {
      return searchTopics
        .map((searchTopic: Topic) => searchTopic.name)
        .every((t: string) =>
          topicConjunction.topics.map((topic) => topic.name).includes(t)
        );
    }
    return true;
  }

  topicSearch(topic: Topic, search: string) {
    return (
      search != null &&
      topic.name.toLowerCase().indexOf(search.toLowerCase()) !== -1
    );
  }

  async saveAssessment() {
    if (this.assessment && !this.assessment.title) {
      await this.$store.dispatch('error', 'Assessment must have title');
      return;
    }

    await this.$store.dispatch('loading');
    try {
      let updatedAssessment: Assessment = await RemoteServices.saveAssessment(
        this.assessment
      );
      this.$emit('updateAssessment', updatedAssessment);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showQuestionsDialog(topicConjunction: TopicConjunction) {
    await this.$store.dispatch('loading');
    try {
      this.questionsToShow = await RemoteServices.getTopicConjuctionQuestions(
        topicConjunction
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');

    this.questionsDialog = true;
  }

  onCloseQuestionsDialog() {
    this.questionsDialog = false;
    this.questionsToShow = [];
  }

  removeTopicConjunction(topicConjuntion: TopicConjunction) {
    this.topicConjunctions.push(topicConjuntion);
    this.assessment.topicConjunctions =
      this.assessment.topicConjunctions.filter(
        (tc) => tc.sequence != topicConjuntion.sequence
      );
  }

  addTopicConjunction(topicConjuntion: TopicConjunction) {
    this.assessment.topicConjunctions.push(topicConjuntion);
    this.topicConjunctions = this.topicConjunctions.filter(
      (tc) => tc.sequence !== topicConjuntion.sequence
    );
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped></style>
