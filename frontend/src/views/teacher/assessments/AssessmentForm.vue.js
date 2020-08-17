import { __decorate } from "tslib";
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/management/Assessment';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import TopicConjunction from '@/models/management/TopicConjunction';
import { _ } from 'vue-underscore';
let AssessmentForm = class AssessmentForm extends Vue {
    constructor() {
        super(...arguments);
        this.currentTopicsSearch = '';
        this.currentTopicsSearchText = '';
        this.allTopicsSearch = '';
        this.allTopicsSearchText = '';
        this.questionsDialog = false;
        this.allTopics = [];
        this.topicConjunctions = [];
        this.questionsToShow = [];
        this.allQuestions = [];
        this.selectedQuestions = [];
        this.topicHeaders = [
            {
                text: 'Actions',
                value: 'action',
                align: 'left',
                width: '5px',
                sortable: false
            },
            {
                text: 'Topics',
                value: 'topics',
                align: 'left',
                sortable: false
            }
        ];
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            [this.allQuestions, this.allTopics] = await Promise.all([
                RemoteServices.getQuestions(),
                RemoteServices.getTopics()
            ]);
            this.calculateTopicCombinations();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    // Calculates the ((set of (topics of all the questions)) not present in the current assessment)
    calculateTopicCombinations() {
        if (this.editMode) {
            this.topicConjunctions = [];
            this.allQuestions.map((question) => {
                if (!this.contains(this.topicConjunctions, question.topics) &&
                    !this.contains(this.assessment.topicConjunctions, question.topics)) {
                    let topicConjunction = new TopicConjunction();
                    topicConjunction.topics = question.topics;
                    this.topicConjunctions.push(topicConjunction);
                }
            });
        }
    }
    // Checks if the topics of one topicConjunction has an exact match to the topicArray
    contains(topicConjunctions, topicArray) {
        return (topicConjunctions.filter(topicConjunction => _.isEqual(topicConjunction.topics.sort((a, b) => (a.name > b.name ? 1 : -1)), topicArray.sort((a, b) => (a.name > b.name ? 1 : -1)))).length !== 0);
    }
    topicFilter(value, search, topicConjunction) {
        let searchTopics = JSON.parse(search);
        if (searchTopics !== '') {
            return searchTopics
                .map((searchTopic) => searchTopic.name)
                .every((t) => topicConjunction.topics.map(topic => topic.name).includes(t));
        }
        return true;
    }
    topicSearch(topic, search) {
        return (search != null &&
            topic.name.toLowerCase().indexOf(search.toLowerCase()) !== -1);
    }
    async saveAssessment() {
        if (this.assessment && !this.assessment.title) {
            await this.$store.dispatch('error', 'Assessment must have title');
            return;
        }
        try {
            let updatedAssessment = await RemoteServices.saveAssessment(this.assessment);
            this.$emit('updateAssessment', updatedAssessment);
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    showQuestionsDialog(topicConjunction) {
        if (topicConjunction !== null) {
            this.questionsToShow = this.allQuestions.filter(question => {
                return _.isEqual(topicConjunction.topics, question.topics);
            });
        }
        else {
            this.questionsToShow = this.allQuestions.filter(question => {
                return (this.assessment.topicConjunctions.filter(topicConjunction => {
                    return _.isEqual(topicConjunction.topics, question.topics);
                }).length !== 0);
            });
        }
        this.questionsDialog = true;
    }
    closeQuestionsDialog() {
        this.questionsDialog = false;
        this.questionsToShow = [];
    }
    removeTopicConjunction(topicConjuntion) {
        this.topicConjunctions.push(topicConjuntion);
        this.assessment.topicConjunctions = this.assessment.topicConjunctions.filter(tc => tc.sequence != topicConjuntion.sequence);
    }
    addTopicConjunction(topicConjuntion) {
        this.assessment.topicConjunctions.push(topicConjuntion);
        this.topicConjunctions = this.topicConjunctions.filter(tc => tc.sequence !== topicConjuntion.sequence);
    }
    recalculateQuestionList() {
        if (this.assessment) {
            this.selectedQuestions = this.allQuestions.filter(question => {
                return this.assessment.topicConjunctions.find(topicConjunction => {
                    return (String(question.topics.map(topic => topic.id).sort()) ===
                        String(topicConjunction.topics.map(topic => topic.id).sort()));
                });
            });
        }
    }
    convertMarkDown(text, image = null) {
        return convertMarkDown(text, image);
    }
};
__decorate([
    Prop(Assessment)
], AssessmentForm.prototype, "assessment", void 0);
__decorate([
    Prop(Boolean)
], AssessmentForm.prototype, "editMode", void 0);
__decorate([
    Watch('assessment')
], AssessmentForm.prototype, "calculateTopicCombinations", null);
__decorate([
    Watch('assessment.topicConjunctions', { deep: true })
], AssessmentForm.prototype, "recalculateQuestionList", null);
AssessmentForm = __decorate([
    Component
], AssessmentForm);
export default AssessmentForm;
//# sourceMappingURL=AssessmentForm.vue.js.map