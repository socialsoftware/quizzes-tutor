import { __decorate } from "tslib";
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
let StudentsView = class StudentsView extends Vue {
    constructor() {
        super(...arguments);
        this.course = null;
        this.students = [];
        this.search = '';
        this.headers = [
            { text: 'Name', value: 'name', align: 'left', width: '40%' },
            {
                text: 'Teacher Quizzes',
                value: 'numberOfTeacherQuizzes',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Generated Quizzes',
                value: 'numberOfStudentQuizzes',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Total Answers',
                value: 'numberOfAnswers',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Correct Answers',
                value: 'percentageOfCorrectAnswers',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Answers Teacher Quiz',
                value: 'numberOfTeacherAnswers',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Correct Answers Teacher Quiz',
                value: 'percentageOfCorrectTeacherAnswers',
                align: 'center',
                width: '10%'
            }
        ];
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.course = this.$store.getters.getCurrentCourse;
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    async onAcademicTermChange() {
        await this.$store.dispatch('loading');
        try {
            if (this.course) {
                this.students = await RemoteServices.getCourseStudents(this.course);
            }
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    getPercentageColor(percentage) {
        if (percentage < 25)
            return 'red';
        else if (percentage < 50)
            return 'orange';
        else if (percentage < 75)
            return 'lime';
        else
            return 'green';
    }
};
__decorate([
    Watch('course')
], StudentsView.prototype, "onAcademicTermChange", null);
StudentsView = __decorate([
    Component
], StudentsView);
export default StudentsView;
//# sourceMappingURL=StudentsView.vue.js.map