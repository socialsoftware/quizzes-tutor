<template>
    <v-card>
      <reply-component
        v-if="this.discussion != null"
        :discussions="[this.discussion]"
      />
    </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import ReplyComponent from '@/views/student/discussions/ReplyComponent.vue';
import Discussion from '@/models/management/Discussion';

@Component({
  components: {
    'reply-component': ReplyComponent
  }
})
export default class ShowDiscussion extends Vue {
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;
}
</script>

<style lang="scss" scoped>

</style>