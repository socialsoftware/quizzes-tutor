<template>
  <v-card border-variant="light" outlined>
    <v-card-title>
      <span>Answer Slot #{{ spot.sequence }}</span>
      <v-badge
        :content="spot.options.length"
        :value="spot.options.length"
        color="grey"
        inline
      />
    </v-card-title>
    <v-list>
      <v-list-item>
        <v-list-item-content>
          <v-list-item-title>
            <v-text-field
              v-model="currentText"
              label="Add option"
              v-on:keyup.enter="addNewElement"
            />
          </v-list-item-title>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn @click="addNewElement" class="ma-2" icon>
            <v-icon color="grey lighten-1">mdi-plus</v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
      <v-list-item v-for="(item, index) in spot.options" :key="item.content">
        <v-list-item-content>
          <v-list-item-title>{{ item.content }} </v-list-item-title>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn @click="item.correct = !item.correct" icon>
            <v-icon v-if="!item.correct" color="grey lighten-1"
              >mdi-checkbox-blank-outline
            </v-icon>
            <v-icon v-if="item.correct" color="green lighten-1"
              >mdi-checkbox-marked-outline</v-icon
            >
          </v-btn>
          <v-btn @click="spot.options.splice(index, 1)" icon>
            <v-icon color="red lighten-1">mdi-delete-forever </v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
    </v-list>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop, PropSync } from 'vue-property-decorator';
import CodeFillInSpot from '@/models/management/questions/CodeFillInSpot';
import Option from '@/models/management/Option';

@Component
export default class CodeFillInOptions extends Vue {
  @PropSync('value', { type: CodeFillInSpot }) spot!: CodeFillInSpot;
  currentText: string = '';
  addNewElement() {
    if (this.currentText) {
      const option = new Option();
      option.content = this.currentText;
      this.spot.options.push(option);
      this.currentText = '';
    }
  }
}
</script>
