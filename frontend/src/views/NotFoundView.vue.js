import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
let NotFoundView = class NotFoundView extends Vue {
    created() {
        setTimeout(async () => {
            await this.$router.push({ name: 'home' });
        }, 5000);
    }
};
NotFoundView = __decorate([
    Component
], NotFoundView);
export default NotFoundView;
//# sourceMappingURL=NotFoundView.vue.js.map