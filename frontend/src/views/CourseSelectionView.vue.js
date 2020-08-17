import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
let CourseSelectionView = class CourseSelectionView extends Vue {
    constructor() {
        super(...arguments);
        this.courseExecutions = null;
        this.confirmationDialog = false;
        this.selectedCourse = null;
    }
    async created() {
        this.courseExecutions = await this.$store.getters.getUser.courses;
    }
    async selectCourse(course) {
        if (course.status !== 'INACTIVE') {
            await this.$store.dispatch('currentCourse', course);
            await this.$router.push({ name: 'home' });
        }
        else {
            this.selectedCourse = course;
            this.confirmationDialog = true;
        }
    }
    async activateCourse() {
        this.confirmationDialog = false;
        try {
            if (this.selectedCourse) {
                this.selectedCourse = await RemoteServices.activateCourse(this.selectedCourse);
                await this.$store.dispatch('currentCourse', this.selectedCourse);
                await this.$router.push({ name: 'home' });
            }
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    unselectCourse() {
        this.selectedCourse = null;
        this.confirmationDialog = false;
    }
};
CourseSelectionView = __decorate([
    Component
], CourseSelectionView);
export default CourseSelectionView;
//# sourceMappingURL=CourseSelectionView.vue.js.map