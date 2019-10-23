<template>
  <v-content>
    <v-card>
      <v-divider class="mx-4" inset vertical> </v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" dark class="mb-2" @click="switchMode">
          {{ editMode ? "Close" : "Create" }}
        </v-btn>
        <v-btn
          color="primary"
          dark
          class="mb-2"
          v-if="editMode"
          @click="saveAssessment"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
    <v-card v-if="editMode && assessment">
      <v-card-title>
        <span class="headline">Create Assessment</span>
        <v-dialog v-model="showAssessment" max-width="1000px">
          <v-card v-if="assessmentToShow">
            <v-card-title>
              <span class="headline">{{ assessmentToShow.title }}</span>
            </v-card-title>
            <v-card-text>
              <v-container grid-list-md fluid>
                <v-layout column wrap>
                  <v-flex class="text-left" xs24 sm12 md8>
                    <p>assessmentToShow</p>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="blue darken-1"
                text
                @click="closeShowAssessmentDialog"
                >Close</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-card-title>
      <v-card-text>
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs18 sm9 md6>
              <v-text-field
                v-model="assessment.title"
                label="Title"
              ></v-text-field>
            </v-flex>
          </v-layout>
        </v-container>
        <v-container grid-list-md fluid>
          <v-data-table
            :headers="topicHeaders"
            :items="assessment.topicConjunctions"
            :items-per-page="10"
          >
            <template v-slot:item.topics="{ item }">
              <v-form>
                <v-autocomplete
                  v-model="item.topics"
                  :items="topics"
                  multiple
                  return-object
                  item-text="name"
                  item-value="name"
                >
                  <!--                  @change="saveAssessment(item.id)"-->
                  <template v-slot:selection="data">
                    <v-chip
                      v-bind="data.attrs"
                      :input-value="data.selected"
                      close
                      @click="data.select"
                      @click:close="removeTopic(item.sequence, data.item)"
                    >
                      {{ data.item.name }}
                    </v-chip>
                  </template>
                  <template v-slot:item="data">
                    <v-list-item-content>
                      <v-list-item-title
                        v-html="data.item.name"
                      ></v-list-item-title>
                    </v-list-item-content>
                  </template>
                </v-autocomplete>
              </v-form>
            </template>

            <template v-slot:item.action="{ item }">
              <v-icon
                small
                class="mr-2"
                @click="removeTopicConjunction(item.sequence)"
              >
                close</v-icon
              >
            </template>
          </v-data-table>
          <v-btn
            color="primary"
            dark
            class="mb-2"
            v-if="editMode"
            @click="newTopicConjunction"
            >Add Topic Conjunction</v-btn
          >
        </v-container>
        <v-container grid-list-md fluid>
          <v-data-table
            :headers="questionHeaders"
            :items="questions"
            :items-per-page="10"
            show-expand
          >
            <template v-slot:item.content="{ item }">
              <div
                class="text-left"
                @click="props.expanded = !props.expanded"
                v-html="convertMarkDownNoFigure(item.content, item.image)"
              ></div>
            </template>

            <template v-slot:item.topics="{ item }">
              <span v-for="topic in item.topics" :key="topic.id">
                {{ topic.name }}
              </span>
            </template>

            <template v-slot:expanded-item="{ item }">
              <td :colspan="9">
                <v-simple-table>
                  <thead>
                    <tr>
                      <th class="text-left">Option</th>
                      <th class="text-left">Correct</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="option in item.options" :key="option.id">
                      <td
                        class="text-left"
                        v-html="convertMarkDownNoFigure(option.content, null)"
                      ></td>
                      <td>
                        <span v-if="option.correct">TRUE</span
                        ><span v-else>FALSE</span>
                      </td>
                    </tr>
                  </tbody>
                </v-simple-table>
              </td>
            </template>
          </v-data-table>
        </v-container>
      </v-card-text>
    </v-card>
  </v-content>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import Assessment from "@/models/management/Assessment";
import Topic from "@/models/management/Topic";
import Question from "@/models/management/Question";
import {
  convertMarkDown,
  convertMarkDownNoFigure
} from "@/services/ConvertMarkdownService";
import Image from "@/models/management/Image";
import TopicConjunctions from "@/models/management/TopicConjunction";

@Component
export default class AssessmentForm extends Vue {
  @Prop(Assessment) readonly assessment!: Assessment;
  @Prop(Boolean) readonly editMode!: boolean;
  topics: Topic[] = [];
  allQuestions: Question[] = [];
  questions: Question[] = [];
  showAssessment: boolean = false;
  assessmentToShow: Assessment | null | undefined = null;
  position: number | null = null;
  topicHeaders: object = [
    {
      text: "Topics",
      value: "topics",
      align: "left",
      width: "99%",
      sortable: false
    },
    {
      text: "Actions",
      value: "action",
      align: "center",
      width: "1%",
      sortable: false
    }
  ];

  questionHeaders: object = [
    {
      text: "Question",
      value: "content",
      align: "left",
      width: "70%",
      sortable: false
    },
    {
      text: "Topics",
      value: "topics",
      align: "left",
      width: "20%",
      sortable: false
    },
    {
      text: "Title",
      value: "title",
      align: "left",
      width: "5%",
      sortable: false
    }
  ];

  // noinspection JSUnusedGlobalSymbols
  async created() {
    try {
      this.topics = await RemoteServices.getTopics();
      this.allQuestions = await RemoteServices.getQuestions();
      this.questions = this.allQuestions;
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  switchMode() {
    this.$emit("switchMode");
  }

  async saveAssessment() {
    if (this.assessment && !this.assessment.title) {
      await this.$store.dispatch("error", "Assessment must have title");
      return;
    }

    try {
      let updatedAssessment: Assessment = await RemoteServices.saveAssessment(
        this.assessment
      );
      this.$emit("updateAssessment", updatedAssessment);
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  removeTopicConjunction(sequence: number) {
    this.assessment.topicConjunctions = this.assessment.topicConjunctions.filter(
      topicConjunction => topicConjunction.sequence != sequence
    );
  }

  removeTopic(sequence: number, toRemoveTopic: Topic) {
    this.assessment.topicConjunctions.find(
      topicConjunction => topicConjunction.sequence == sequence
    )!.topics = this.assessment.topicConjunctions
      .find(topicConjunction => topicConjunction.sequence == sequence)!
      .topics.filter(topic => topic.id != toRemoveTopic.id);
  }

  newTopicConjunction() {
    this.assessment.topicConjunctions.push(new TopicConjunctions());
  }

  @Watch("assessment.topicConjunctions", { deep: true })
  recalculateQuestionList() {
    if (this.assessment) {
      this.questions = this.allQuestions.filter(question => {
        return this.assessment.topicConjunctions.find(topicConjunction => {
          return (
            String(question.topics.map(topic => topic.id).sort()) ===
            String(topicConjunction.topics.map(topic => topic.id).sort())
          );
        });
      });
    }
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  renderQuestion(question: Question): string {
    let text =
      convertMarkDown(question.content, question.image) + " <br/> <br/> ";
    text =
      text +
      question.options
        .map(
          (option, index) =>
            index.toString() +
            " - " +
            option.content +
            (option.correct ? " (correct)" : "")
        )
        .join(" <br/> ");
    return text;
  }
}
</script>

<style lang="scss"></style>
