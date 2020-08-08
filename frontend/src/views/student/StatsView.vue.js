import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
let StatsView = class StatsView extends Vue {
    constructor() {
        super(...arguments);
        this.stats = null;
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.stats = await RemoteServices.getUserStats();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
};
StatsView = __decorate([
    Component({
        components: { AnimatedNumber }
    })
], StatsView);
export default StatsView;
//# sourceMappingURL=StatsView.vue.js.map