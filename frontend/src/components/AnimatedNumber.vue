<template>
  <span>{{ displayNumber }}<slot /></span>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';

const props = defineProps({
  number: {
    type: Number,
    default: 0
  }
});

const displayNumber = ref<number>(0);
let interval: number = 0;

const updateNumber = () => {
  window.clearInterval(interval);
  if (props.number == displayNumber.value) {
    return;
  }
  interval = window.setInterval(() => {
    if (displayNumber.value < props.number) {
      let change = (props.number - displayNumber.value) / 10;
      change = change >= 0 ? Math.ceil(change) : Math.floor(change);
      displayNumber.value = displayNumber.value + change;
    } else {
      window.clearInterval(interval);
    }
  }, 20);
};

onMounted(() => {
  updateNumber();
});

watch(() => props.number, () => {
  updateNumber();
});
</script>

<style scoped lang="scss" />
