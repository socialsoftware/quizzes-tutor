<template>
  <div class="container">
    <h3>Statistics</h3>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div ref="totalQuizzes" class="icon-wrapper">
          <animated-number :number="stats.totalQuizzes" />
        </div>
        <div class="project-name">
          <p>Total Quizzes Solved</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalAnswers" class="icon-wrapper">
          <animated-number :number="stats.totalAnswers" />
        </div>
        <div class="project-name">
          <p>Total Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalUniqueQuestions" class="icon-wrapper">
          <animated-number :number="stats.totalUniqueQuestions" />
        </div>
        <div class="project-name">
          <p>Unique Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div ref="correctAnswers" class="icon-wrapper">
          <animated-number :number="stats.correctAnswers">%</animated-number>
        </div>
        <div class="project-name">
          <p>Total Correct Answers</p>
        </div>
      </div>
      <div class="items">
        <div ref="improvedCorrectAnswers" class="icon-wrapper">
          <animated-number :number="stats.improvedCorrectAnswers"
            >%
          </animated-number>
        </div>
        <div class="project-name">
          <p>Improved Correct Questions</p>
        </div>
      </div>
      <div class="items">
        <div ref="percentageOfSeenQuestions" class="icon-wrapper">
          <animated-number
            :number="
              (stats.totalUniqueQuestions * 100) / stats.totalAvailableQuestions
            "
            >%
          </animated-number>
        </div>
        <div class="project-name">
          <p>Percentage of questions seen</p>
        </div>
      </div>
      <div class="items">
        <div ref="improvedCorrectAnswers" class="icon-wrapper">
          <animated-number :number="stats.createdDiscussions" />
        </div>
        <div class="project-name">
          <p>Discussions Created</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import StudentStats from '@/models/dashboard/StudentStats';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';

const props = defineProps<{
  dashboardId: number;
}>();

const store = useStore();
const stats = ref<StudentStats | null>(null);

onMounted(async () => {
  store.setLoading();
  try {
    stats.value = await RemoteServices.getUserCourseExecutionStats(
      props.dashboardId
    );
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
