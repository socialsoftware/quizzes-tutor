<template><div></div></template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Store from '@/store';

@Component
export default class ExportCourseView extends Vue {
  async created() {
    let course = Store.getters.getCurrentCourse;
    let fileName = course.acronym + '.tar.gz';
    try {
      if (course.courseExecutionId != null) {
        let result = await RemoteServices.exportCourseExecutionInfo(
          course.courseExecutionId
        );
        const url = window.URL.createObjectURL(result);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped></style>
