import { __decorate } from "tslib";
import { Component, Vue, Watch } from 'vue-property-decorator';
let ErrorMessage = class ErrorMessage extends Vue {
    constructor() {
        super(...arguments);
        this.dialog = this.$store.getters.getError;
        this.errorMessage = this.$store.getters.getErrorMessage;
    }
    created() {
        this.dialog = this.$store.getters.getError;
        this.errorMessage = this.$store.getters.getErrorMessage;
        this.$store.watch((state, getters) => getters.getError, () => {
            this.dialog = this.$store.getters.getError;
            this.errorMessage = this.$store.getters.getErrorMessage;
        });
    }
    closeError() {
        if (!this.dialog) {
            this.$store.dispatch('clearError');
        }
    }
};
__decorate([
    Watch('dialog')
], ErrorMessage.prototype, "closeError", null);
ErrorMessage = __decorate([
    Component
], ErrorMessage);
export default ErrorMessage;
//# sourceMappingURL=ErrorMessage.vue.js.map