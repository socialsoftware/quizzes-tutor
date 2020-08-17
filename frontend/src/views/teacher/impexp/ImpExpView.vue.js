import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
let ImpExptView = class ImpExptView extends Vue {
    async exportAll() {
        try {
            await RemoteServices.exportAll();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
};
ImpExptView = __decorate([
    Component
], ImpExptView);
export default ImpExptView;
//# sourceMappingURL=ImpExpView.vue.js.map