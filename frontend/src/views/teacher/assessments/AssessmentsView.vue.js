import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/management/Assessment';
import AssessmentForm from '@/views/teacher/assessments/AssessmentForm.vue';
import AssessmentList from '@/views/teacher/assessments/AssessmentList.vue';
let AssessmentsView = class AssessmentsView extends Vue {
    constructor() {
        super(...arguments);
        this.assessments = [];
        this.assessment = null;
        this.editMode = false;
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.assessments = await RemoteServices.getAssessments();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    changeMode() {
        this.editMode = !this.editMode;
        if (this.editMode) {
            this.assessment = new Assessment();
        }
        else {
            this.assessment = null;
        }
    }
    async editAssessment(assessmentId) {
        try {
            this.assessment = {
                ...this.assessments.find(assessment => assessment.id === assessmentId)
            };
            this.editMode = true;
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    updateAssessment(updatedAssessment) {
        this.assessments = this.assessments.filter(assessment => assessment.id !== updatedAssessment.id);
        this.assessments.unshift(updatedAssessment);
        this.editMode = false;
        this.assessment = null;
    }
    newAssessment() {
        this.assessment = new Assessment();
        this.editMode = true;
    }
    deleteAssessment(assessmentId) {
        this.assessments = this.assessments.filter(assessment => assessment.id !== assessmentId);
    }
};
AssessmentsView = __decorate([
    Component({
        components: {
            AssessmentForm,
            AssessmentList
        }
    })
], AssessmentsView);
export default AssessmentsView;
//# sourceMappingURL=AssessmentsView.vue.js.map