<template>
  <div :id="elementId"></div>
</template>

<script setup lang="ts">
import { computed, watch, onMounted } from 'vue';
import Plotly, { Layout, ViolinData } from 'plotly.js-cartesian-dist-min';
import { FraudScore } from '@/models/management/fraud/FraudScore';

const props = withDefaults(defineProps<{
  quizFraudScores?: FraudScore[];
  graphId: string;
  title: string;
}>(), {
  quizFraudScores: () => []
});

const scores = computed(() => props.quizFraudScores.map((qfs) => qfs.score));
const labels = computed(() => props.quizFraudScores.map((qfs) => `(User, ${qfs.userInfo.name})`));
const elementId = computed(() => 'fraud-graph-' + props.graphId);

watch(() => props.quizFraudScores, () => {
  drawPlot();
}, { deep: true });

onMounted(() => {
  drawPlot();
});

const drawPlot = () => {
  let data: Partial<ViolinData>[] = [
    {
      type: 'violin',
      y: scores.value,
      points: 'all',
      box: {
        visible: true,
      },
      line: {
        color: 'black',
      },
      text: labels.value,
      fillcolor: '#1876d1',
      opacity: 0.6,
      meanline: {
        visible: true,
      },
      x0: 'Scores',
    },
  ];

  const layout: Partial<Layout> = {
    title: { text: props.title },
    autosize: true,
    yaxis: {
      zeroline: false,
    },
    width: 280,
    showlegend: false,
    margin: {
      r: 25,
      l: 25,
    },
  };

  Plotly.newPlot(elementId.value, data, layout, { responsive: true });
};
</script>

<style></style>
