<template>
  <div v-if="stats != null">
    <!--p>Total quizzes done: {{ stats.totalQuizzes }}</p>
    <p>Total questions done: {{ stats.totalAnswers }}</p>
    <chart :stats="stats"></chart-->
    <div class="stats-container">
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
          <animated-number
            :number="stats.totalUniqueQuestions"
          ></animated-number>
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
    </div>
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

  // noinspection JSUnusedGlobalSymbols
  async mounted() {
    try {
      this.stats = await RemoteServices.getUserStats();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }
}
</script>

<style lang="scss">
.stats-container {
  box-sizing: border-box;
  font-family: "Raleway", sans-serif;
  width: 800px;
  height: 250px;

  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  margin: auto;
  box-sizing: border-box;

  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  grid-template-rows: 1fr;
  grid-gap: 15px;
}

.items {
  background-color: rgba(255, 255, 255, 0.75);
  color: #1976d2;
  border-radius: 5px;
  display: grid;
  grid-template-rows: 2fr 1fr;
  grid-gap: 10px;
  cursor: pointer;
  transition: all 0.6s;
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
