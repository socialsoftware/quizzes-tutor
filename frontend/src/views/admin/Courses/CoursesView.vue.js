import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';
import EditCourseDialog from '@/views/admin/Courses/EditCourseDialog.vue';
let CoursesView = class CoursesView extends Vue {
    constructor() {
        super(...arguments);
        this.courses = [];
        this.currentCourse = null;
        this.editCourseDialog = false;
        this.search = '';
        this.headers = [
            {
                text: 'Actions',
                value: 'action',
                align: 'left',
                sortable: false,
                width: '5px'
            },
            {
                text: 'Course Type',
                value: 'courseType',
                align: 'center',
                width: '10%'
            },
            { text: 'Name', value: 'name', align: 'left', width: '30%' },
            {
                text: 'Execution Type',
                value: 'courseExecutionType',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Acronym',
                value: 'acronym',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Academic Term',
                value: 'academicTerm',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Number of Teachers',
                value: 'numberOfTeachers',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Number of Students',
                value: 'numberOfStudents',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Number of Questions',
                value: 'numberOfQuestions',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Number of Quizzes',
                value: 'numberOfQuizzes',
                align: 'center',
                width: '10%'
            },
            {
                text: 'Status',
                value: 'status',
                align: 'center',
                width: '10%'
            }
        ];
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.courses = await RemoteServices.getCourses();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    newCourse() {
        this.currentCourse = new Course();
        this.editCourseDialog = true;
    }
    createFromCourse(course) {
        this.currentCourse = new Course(course);
        this.currentCourse.courseExecutionId = undefined;
        this.currentCourse.courseExecutionType = 'EXTERNAL';
        this.currentCourse.acronym = undefined;
        this.currentCourse.academicTerm = undefined;
        this.editCourseDialog = true;
    }
    async onCreateCourse(course) {
        this.courses.unshift(course);
        this.editCourseDialog = false;
        this.currentCourse = null;
    }
    onCloseDialog() {
        this.editCourseDialog = false;
        this.currentCourse = null;
    }
    async deleteCourse(courseToDelete) {
        if (confirm('Are you sure you want to delete this course execution?')) {
            try {
                await RemoteServices.deleteCourse(courseToDelete.courseExecutionId);
                this.courses = this.courses.filter(course => course.courseExecutionId != courseToDelete.courseExecutionId);
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
};
CoursesView = __decorate([
    Component({
        components: {
            'edit-course-dialog': EditCourseDialog
        }
    })
], CoursesView);
export default CoursesView;
//# sourceMappingURL=CoursesView.vue.js.map