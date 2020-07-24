<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="users"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
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
          <v-spacer />


          <v-select
            v-model=currentCourse
            :items="courses"
            label="Select a Course Execution"
            outlined
            clearable
            v-on:change="updateCurrentExecution"
            class="ml-auto"
          >
            <template slot="selection" slot-scope="data">
              {{ data.item.name }} - {{ data.item.acronym }}
            </template>
            <template slot="item" slot-scope="data">
              {{ data.item.name }} - {{ data.item.acronym }}
            </template>
          </v-select>
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import ExternalUser from '@/models/user/ExternalUser';

@Component
export default class ExternalUsersView extends Vue {

    currentCourse : Course | null = null;
    courses : Course[] = [];
    users : ExternalUser[] = [];
    search : String = '';

    headers: object = [
        { text: 'Username', value: 'username', align: 'left', width: '40%' },
        {
        text: 'State',
        value: 'state',
        align: 'center',
        width: '20%'
        },
    ];

    async created(){
      await this.$store.dispatch('loading');

      try {
        this.courses = await RemoteServices.getCourses();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      
      this.courses = this.courses.filter(c => c.courseExecutionType === 'EXTERNAL');

      this.updateCurrentExecution();
      await this.$store.dispatch('clearLoading');

    }

    async updateCurrentExecution(){
      
      var requestParameter : String = '';
      await this.$store.dispatch('loading');

      if(this.currentCourse)
        requestParameter = ''+this.currentCourse.courseExecutionId;
      else
        requestParameter = 'ALL';

      try {
        this.users = await RemoteServices.getExternalUsers(requestParameter);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');

    
    }
}
</script>