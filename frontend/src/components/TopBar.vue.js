import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
let TopBar = class TopBar extends Vue {
    constructor() {
        super(...arguments);
        this.appName = process.env.VUE_APP_NAME || 'ENV FILE MISSING';
        this.fenixUrl = process.env.VUE_APP_FENIX_URL || '';
        this.drawer = false;
    }
    get currentCourse() {
        return this.$store.getters.getCurrentCourse;
    }
    get moreThanOneCourse() {
        return (this.$store.getters.getUser.coursesNumber > 1 &&
            this.$store.getters.getCurrentCourse);
    }
    get isLoggedIn() {
        return this.$store.getters.isLoggedIn;
    }
    get isTeacher() {
        return this.$store.getters.isTeacher;
    }
    get isAdmin() {
        return this.$store.getters.isAdmin;
    }
    get isStudent() {
        return this.$store.getters.isStudent;
    }
    async logout() {
        await this.$store.dispatch('logout');
        await this.$router.push({ name: 'home' }).catch(() => { });
    }
};
TopBar = __decorate([
    Component
], TopBar);
export default TopBar;
//# sourceMappingURL=TopBar.vue.js.map