import { __decorate } from "tslib";
import { Component, Vue, Watch } from 'vue-property-decorator';
let Loading = class Loading extends Vue {
    constructor() {
        super(...arguments);
        this.loading = this.$store.getters.getLoading;
    }
    created() {
        this.loading = this.$store.getters.getLoading;
        this.$store.watch((state, getters) => getters.getLoading, () => {
            this.loading = this.$store.getters.getLoading;
        });
    }
    closeError() {
        if (!this.loading) {
            this.$store.dispatch('clearLoading');
        }
    }
};
__decorate([
    Watch('loading')
], Loading.prototype, "closeError", null);
Loading = __decorate([
    Component
], Loading);
export default Loading;
//# sourceMappingURL=Loading.vue.js.map