<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="courses"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class CoursesView extends Vue {
  courses: Course[] = [];
  search: string = '';
  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '40%' },
    {
      text: 'Course Type',
      value: 'courseType',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Acronym',
      value: 'acronym',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Execution Type',
      value: 'courseExecutionType',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Academic Term',
      value: 'academicTerm',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.courses = await RemoteServices.getCourses();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped></style>
