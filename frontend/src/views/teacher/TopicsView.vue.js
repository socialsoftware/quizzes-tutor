import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';
let TopicsView = class TopicsView extends Vue {
    constructor() {
        super(...arguments);
        this.topics = [];
        this.editedTopic = new Topic();
        this.topicDialog = false;
        this.search = '';
        this.headers = [
            {
                text: 'Actions',
                value: 'action',
                align: 'left',
                width: '5px',
                sortable: false
            },
            { text: 'Name', value: 'name', align: 'left' },
            {
                text: 'Questions',
                value: 'numberOfQuestions',
                align: 'center',
                width: '115px'
            }
        ];
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.topics = await RemoteServices.getTopics();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    customFilter(value, search) {
        // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
        return (search != null &&
            typeof value === 'string' &&
            value.toLocaleLowerCase().indexOf(search.toLocaleLowerCase()) !== -1);
    }
    formTitle() {
        return this.editedTopic === null ? 'New Topic' : 'Edit Topic';
    }
    newTopic() {
        this.editedTopic = new Topic();
        this.topicDialog = true;
    }
    closeDialogue() {
        this.topicDialog = false;
    }
    editTopic(topic, e) {
        if (e)
            e.preventDefault();
        this.editedTopic = { ...topic };
        this.topicDialog = true;
    }
    async deleteTopic(toDeleteTopic) {
        if (confirm('Are you sure you want to delete this topic?')) {
            try {
                await RemoteServices.deleteTopic(toDeleteTopic);
                this.topics = this.topics.filter(topic => topic.id !== toDeleteTopic.id);
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
    async saveTopic() {
        try {
            if (this.editedTopic.id) {
                this.editedTopic = await RemoteServices.updateTopic(this.editedTopic);
                this.topics = this.topics.filter(topic => topic.id !== this.editedTopic.id);
            }
            else if (this.editedTopic) {
                this.editedTopic = await RemoteServices.createTopic(this.editedTopic);
            }
            this.topics.unshift(this.editedTopic);
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        this.closeDialogue();
    }
};
TopicsView = __decorate([
    Component
], TopicsView);
export default TopicsView;
//# sourceMappingURL=TopicsView.vue.js.map