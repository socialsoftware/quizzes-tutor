<template>
  <v-content>
    <v-card>
        <v-card-actions>
            <v-flex class="text-xs-right">
                <v-btn color="primary" dark class="mb-2" @click="switchMode">{{editMode ? 'Close' : 'Create'}}</v-btn>
                <v-btn color="primary" dark class="mb-2" v-if="editMode" @click="save">Save</v-btn>
            </v-flex>
        </v-card-actions>
    </v-card>
    <v-card v-if="editMode">
        <v-card-title>
            <span class="headline">Edit Quiz</span>
          </v-card-title>
          <v-card-text>
            <v-container grid-list-md fluid>
              <v-layout row wrap>
                <v-flex xs18 sm9 md6>
                  <v-text-field
                    v-model="quiz.title"
                    label="Title"
                  ></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                    <br/>
                    <datetime type="datetime" v-model="quiz.availableDate">
                        <label for="startDate" slot="before">Available Date:</label>
                    </datetime>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>
    </v-card>
  </v-content>
</template>

<script lang="ts">
import { Component, Vue, Prop} from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import { Quiz } from "@/models/management/Quiz";
import { Datetime } from 'vue-datetime';
import 'vue-datetime/dist/vue-datetime.css'

@Component({
  components: {
    Datetime,
  }
})
export default class QuizForm extends Vue {
    @Prop(Boolean) readonly editMode!: boolean;
    quiz: Quiz = new Quiz();

    constructor() {
        super();
    }

    switchMode() {
        this.$emit('switchMode');
    }

    async save() {
        try {
            this.quiz.type = 'TEACHER';
            this.quiz.year = (new Date()).getFullYear();
            let updatedQuiz: Quiz; 
            updatedQuiz = await RemoteServices.saveQuiz(this.quiz);
            this.quiz = new Quiz();
            this.$emit('updateQuiz', updatedQuiz);
        } catch(error) {
            await this.$store.dispatch("error", error);
        }
    }
}
</script>

<style lang="scss"></style>
