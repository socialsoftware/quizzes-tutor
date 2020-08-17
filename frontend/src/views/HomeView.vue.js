import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import Store from '@/store';
let HomeView = class HomeView extends Vue {
    constructor() {
        super(...arguments);
        this.appName = process.env.VUE_APP_NAME || 'ENV FILE MISSING';
        this.fenixUrl = process.env.VUE_APP_FENIX_URL || '';
    }
    get isLoggedIn() {
        return Store.state.token;
    }
    async demoStudent() {
        await this.$store.dispatch('loading');
        try {
            await this.$store.dispatch('demoStudentLogin');
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    async demoTeacher() {
        await this.$store.dispatch('loading');
        try {
            await this.$store.dispatch('demoTeacherLogin');
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    async demoAdmin() {
        await this.$store.dispatch('loading');
        try {
            await this.$store.dispatch('demoAdminLogin');
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
};
HomeView = __decorate([
    Component
], HomeView);
export default HomeView;
//# sourceMappingURL=HomeView.vue.js.map