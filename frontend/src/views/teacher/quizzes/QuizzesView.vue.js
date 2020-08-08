import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import QuizForm from '@/views/teacher/quizzes/QuizForm.vue';
import QuizList from '@/views/teacher/quizzes/QuizList.vue';
let QuizzesView = class QuizzesView extends Vue {
    constructor() {
        super(...arguments);
        this.quizzes = [];
        this.quiz = null;
        this.editMode = false;
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.quizzes = await RemoteServices.getNonGeneratedQuizzes();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    changeMode() {
        this.editMode = !this.editMode;
        if (this.editMode) {
            this.quiz = new Quiz();
        }
        else {
            this.quiz = null;
        }
    }
    async editQuiz(quizId) {
        try {
            this.quiz = await RemoteServices.getQuiz(quizId);
            this.editMode = true;
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    updateQuiz(updatedQuiz) {
        this.quizzes = this.quizzes.filter(quiz => quiz.id !== updatedQuiz.id);
        this.quizzes.unshift(updatedQuiz);
        this.editMode = false;
        this.quiz = null;
    }
    deleteQuiz(quizId) {
        this.quizzes = this.quizzes.filter(quiz => quiz.id !== quizId);
    }
    newQuiz() {
        this.editMode = true;
        this.quiz = new Quiz();
    }
};
QuizzesView = __decorate([
    Component({
        components: {
            QuizForm,
            QuizList
        }
    })
], QuizzesView);
export default QuizzesView;
//# sourceMappingURL=QuizzesView.vue.js.map