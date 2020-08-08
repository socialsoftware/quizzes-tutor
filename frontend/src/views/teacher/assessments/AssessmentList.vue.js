import { __decorate } from "tslib";
import { Component, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
let AssessmentList = class AssessmentList extends Vue {
    constructor() {
        super(...arguments);
        this.assessment = null;
        this.search = '';
        this.statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];
        this.dialog = false;
        this.headers = [
            {
                text: 'Actions',
                value: 'action',
                align: 'left',
                sortable: false,
                width: '5px'
            },
            { text: 'Order', value: 'sequence', align: 'center', width: '5px' },
            { text: 'Title', value: 'title', width: '80%', align: 'left' },
            {
                text: 'Number of questions',
                value: 'numberOfQuestions',
                align: 'center',
                width: '5px'
            },
            { text: 'Status', value: 'status', align: 'center', width: '5px' }
        ];
    }
    closeAssessment() {
        this.dialog = false;
        this.assessment = null;
    }
    async setStatus(assessmentId, status) {
        try {
            await RemoteServices.setAssessmentStatus(assessmentId, status);
            let assessment = this.assessments.find(assessment => assessment.id === assessmentId);
            if (assessment) {
                assessment.status = status;
            }
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    editAssessment(assessmentId, e) {
        if (e)
            e.preventDefault();
        this.$emit('editAssessment', assessmentId);
    }
    async deleteAssessment(assessmentId) {
        if (confirm('Are you sure you want to delete this assessment?')) {
            try {
                await RemoteServices.deleteAssessment(assessmentId);
                this.$emit('deleteAssessment', assessmentId);
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
    getStatusColor(status) {
        if (status === 'REMOVED')
            return 'red';
        else if (status === 'DISABLED')
            return 'orange';
        else
            return 'green';
    }
    convertMarkDown(text, image = null) {
        return convertMarkDown(text, image);
    }
};
__decorate([
    Prop({ type: Array, required: true })
], AssessmentList.prototype, "assessments", void 0);
AssessmentList = __decorate([
    Component
], AssessmentList);
export default AssessmentList;
//# sourceMappingURL=AssessmentList.vue.js.map