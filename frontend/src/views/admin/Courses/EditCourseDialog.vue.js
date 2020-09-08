import { __decorate } from "tslib";
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
let EditCourseDialog = class EditCourseDialog extends Vue {
    constructor() {
        super(...arguments);
        this.isCreateCourse = false;
    }
    created() {
        this.editCourse = new Course(this.course);
        this.isCreateCourse = !!this.editCourse.name;
    }
    async saveCourse() {
        if (this.editCourse &&
            (!this.editCourse.name ||
                !this.editCourse.acronym ||
                !this.editCourse.academicTerm)) {
            await this.$store.dispatch('error', 'Course must have name, acronym and academicTerm');
            return;
        }
        if (this.editCourse && this.editCourse.courseExecutionId == null) {
            try {
                const result = await RemoteServices.createExternalCourse(this.editCourse);
                this.$emit('new-course', result);
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
};
__decorate([
    Model('dialog', Boolean)
], EditCourseDialog.prototype, "dialog", void 0);
__decorate([
    Prop({ type: Course, required: true })
], EditCourseDialog.prototype, "course", void 0);
EditCourseDialog = __decorate([
    Component
], EditCourseDialog);
export default EditCourseDialog;
//# sourceMappingURL=EditCourseDialog.vue.js.map