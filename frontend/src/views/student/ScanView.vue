import QrcodeVue from "*.vue";
<template>
  <div class="container">
    <qrcode-stream></qrcode-stream>
    <qrcode-drop-zone></qrcode-drop-zone>
    <qrcode-capture></qrcode-capture>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QrcodeStream, QrcodeDropZone, QrcodeCapture } from 'vue-qrcode-reader';

@Component({
  components: {
    'qrcode-stream': QrcodeStream,
    'qrcode-drop-zone': QrcodeDropZone,
    'qrcode-capture': QrcodeCapture
  }
})
export default class ScanView extends Vue {
  async created() {}
}
</script>

<style lang="scss" scoped></style>
