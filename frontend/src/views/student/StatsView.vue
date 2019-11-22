<template>
  <div v-if="stats != null" class="stats-container">
    <!--p>Total quizzes done: {{ stats.totalQuizzes }}</p>
    <p>Total questions done: {{ stats.totalAnswers }}</p>
    <chart :stats="stats"></chart-->
    <div class="items">
      <div class="icon-wrapper" ref="number">
        <animated-number :number="stats.totalQuizzes"></animated-number>
      </div>
      <div class="project-name">
        <p>Total Quizzes Solved</p>
      </div>
    </div>
    <div class="items">
      <div class="icon-wrapper" ref="number">
        <animated-number :number="stats.totalAnswers"></animated-number>
      </div>
      <div class="project-name">
        <p>Total Questions Solved</p>
      </div>
    </div>
    <div class="items">
      <div class="icon-wrapper" ref="number">
        <animated-number :number="stats.totalUniqueQuestions"></animated-number>
      </div>
      <div class="project-name">
        <p>Unique Questions Solved</p>
      </div>
    </div>
    <div class="items">
      <div class="icon-wrapper" ref="number">
        <animated-number :number="stats.correctAnswers">%</animated-number>
      </div>
      <div class="project-name">
        <p>Total Correct Answers</p>
      </div>
    </div>
    <div class="items">
      <div class="icon-wrapper" ref="number">
        <animated-number :number="stats.improvedCorrectAnswers"
          >%</animated-number
        >
      </div>
      <div class="project-name">
        <p>Improved Correct Questions</p>
      </div>
    </div>
    <div class="items">
      <div class="icon-wrapper" ref="number">
        <animated-number
          :number="
            (stats.totalUniqueQuestions * 100) / stats.totalAvailableQuestions
          "
          >%</animated-number
        >
      </div>
      <div class="project-name">
        <p>Percentage of questions seen</p>
      </div>
    </div>
    <span>{{ stats.totalUniqueQuestions }}</span>
    <br />
    <span>{{ stats.totalAvailableQuestions }}</span>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import StudentStats from "@/models/statement/StudentStats";
import RemoteServices from "@/services/RemoteServices";
import Chart from "@/views/components/Chart.vue";
import AnimatedNumber from "@/views/components/AnimatedNumber.vue";

@Component({
  components: { AnimatedNumber, Chart }
})
export default class StatsView extends Vue {
  stats: StudentStats | null = null;

  async created() {
    await this.$store.dispatch("loading");
    try {
      this.stats = await RemoteServices.getUserStats();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    await this.$store.dispatch("clearLoading");
  }
}
</script>

<style lang="scss">
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
