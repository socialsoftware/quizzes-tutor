import { __decorate } from "tslib";
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';
import EditQuestionTopics from '@/views/teacher/questions/EditQuestionTopics.vue';
let QuestionsView = class QuestionsView extends Vue {
    constructor() {
        super(...arguments);
        this.questions = [];
        this.topics = [];
        this.currentQuestion = null;
        this.editQuestionDialog = false;
        this.questionDialog = false;
        this.search = '';
        this.statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];
        this.headers = [
            {
                text: 'Actions',
                value: 'action',
                align: 'left',
                width: '5px',
                sortable: false
            },
            { text: 'Title', value: 'title', width: '50%', align: 'left' },
            {
                text: 'Topics',
                value: 'topics',
                width: '30%',
                align: 'center',
                sortable: false
            },
            { text: 'Status', value: 'status', width: '150px', align: 'left' },
            {
                text: 'Image',
                value: 'image',
                width: '10%',
                align: 'center',
                sortable: false
            },
            { text: 'Difficulty', value: 'difficulty', width: '5px', align: 'center' },
            {
                text: 'Answers',
                value: 'numberOfAnswers',
                width: '5px',
                align: 'center'
            },
            {
                text: 'Nº of generated quizzes',
                value: 'numberOfGeneratedQuizzes',
                width: '5px',
                align: 'center'
            },
            {
                text: 'Nº of non generated quizzes',
                value: 'numberOfNonGeneratedQuizzes',
                width: '5px',
                align: 'center'
            },
            {
                text: 'Creation Date',
                value: 'creationDate',
                width: '150px',
                align: 'center'
            }
        ];
    }
    closeError() {
        if (!this.editQuestionDialog) {
            this.currentQuestion = null;
        }
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            [this.topics, this.questions] = await Promise.all([
                RemoteServices.getTopics(),
                RemoteServices.getQuestions()
            ]);
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    customFilter(value, search, question) {
        // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
        return (search != null &&
            JSON.stringify(question)
                .toLowerCase()
                .indexOf(search.toLowerCase()) !== -1);
    }
    onQuestionChangedTopics(questionId, changedTopics) {
        let question = this.questions.find((question) => question.id == questionId);
        if (question) {
            question.topics = changedTopics;
        }
    }
    getDifficultyColor(difficulty) {
        if (difficulty < 25)
            return 'red';
        else if (difficulty < 50)
            return 'orange';
        else if (difficulty < 75)
            return 'lime';
        else
            return 'green';
    }
    async setStatus(questionId, status) {
        try {
            await RemoteServices.setQuestionStatus(questionId, status);
            let question = this.questions.find(question => question.id === questionId);
            if (question) {
                question.status = status;
            }
        }
        catch (error) {
            await this.$store.dispatch('error', error);
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
    async handleFileUpload(event, question) {
        if (question.id) {
            try {
                const imageURL = await RemoteServices.uploadImage(event, question.id);
                question.image = new Image();
                question.image.url = imageURL;
                confirm('Image ' + imageURL + ' was uploaded!');
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
    showQuestionDialog(question) {
        this.currentQuestion = question;
        this.questionDialog = true;
    }
    onCloseShowQuestionDialog() {
        this.currentQuestion = null;
        this.questionDialog = false;
    }
    newQuestion() {
        this.currentQuestion = new Question();
        this.editQuestionDialog = true;
    }
    editQuestion(question, e) {
        if (e)
            e.preventDefault();
        this.currentQuestion = question;
        this.editQuestionDialog = true;
    }
    duplicateQuestion(question) {
        this.currentQuestion = new Question(question);
        this.currentQuestion.id = null;
        this.currentQuestion.options.forEach(option => {
            option.id = null;
        });
        this.currentQuestion.image = null;
        this.editQuestionDialog = true;
    }
    async onSaveQuestion(question) {
        this.questions = this.questions.filter(q => q.id !== question.id);
        this.questions.unshift(question);
        this.editQuestionDialog = false;
        this.currentQuestion = null;
    }
    async exportCourseQuestions() {
        let fileName = this.$store.getters.getCurrentCourse.name + '-Questions.zip';
        try {
            let result = await RemoteServices.exportCourseQuestions();
            const url = window.URL.createObjectURL(result);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    async deleteQuestion(toDeletequestion) {
        if (toDeletequestion.id &&
            confirm('Are you sure you want to delete this question?')) {
            try {
                await RemoteServices.deleteQuestion(toDeletequestion.id);
                this.questions = this.questions.filter(question => question.id != toDeletequestion.id);
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
};
__decorate([
    Watch('editQuestionDialog')
], QuestionsView.prototype, "closeError", null);
QuestionsView = __decorate([
    Component({
        components: {
            'show-question-dialog': ShowQuestionDialog,
            'edit-question-dialog': EditQuestionDialog,
            'edit-question-topics': EditQuestionTopics
        }
    })
], QuestionsView);
export default QuestionsView;
//# sourceMappingURL=QuestionsView.vue.js.map