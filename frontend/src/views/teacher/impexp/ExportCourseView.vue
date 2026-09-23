<template><div></div></template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';

const store = useStore();

onMounted(async () => {
  let course = store.getCurrentCourse as Course;
  if (!course) return;
  
  let fileName = course.acronym + '.tar.gz';
  try {
    if (course.courseExecutionId != null) {
      let result = await RemoteServices.exportCourseExecutionInfo(
        course.courseExecutionId
      );
      const url = window.URL.createObjectURL(result as any);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
    }
  } catch (error) {
    store.setError(error as string);
  }
});
</script>

<style lang="scss" scoped></style>
