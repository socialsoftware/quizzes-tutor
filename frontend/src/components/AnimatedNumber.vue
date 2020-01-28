<template>
  <span>{{ displayNumber }}<slot /></span>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';

@Component
export default class AnimatedNumber extends Vue {
  @Prop({ default: 0 }) readonly number!: number;
  displayNumber: number = 0;
  interval: number = 0;

  created() {
    this.updateNumber();
  }

  @Watch('number')
  updateNumber() {
    clearInterval(0);
    if (this.number == this.displayNumber) {
      return;
    }
    this.interval = window.setInterval(() => {
      if (this.displayNumber < this.number) {
        let change = (this.number - this.displayNumber) / 10;
        change = change >= 0 ? Math.ceil(change) : Math.floor(change);
        this.displayNumber = this.displayNumber + change;
      }
    }, 20);
  }
}
</script>

<style scoped lang="scss" />
