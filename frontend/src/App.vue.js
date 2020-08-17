import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import axios from 'axios';
import TopBar from '@/components/TopBar.vue';
import ErrorMessage from '@/components/ErrorMessage.vue';
import Loading from '@/components/Loading.vue';
import '@/assets/css/_global.scss';
import '@/assets/css/_scrollbar.scss';
import '@/assets/css/_question.scss';
require('typeface-roboto');
let App = class App extends Vue {
    created() {
        axios.interceptors.response.use(undefined, err => {
            return new Promise(() => {
                if (err.status === 401 && err.config && !err.config.__isRetryRequest) {
                    this.$store.dispatch('logout');
                }
                throw err;
            });
        });
    }
};
App = __decorate([
    Component({
        components: { TopBar, ErrorMessage, Loading }
    })
], App);
export default App;
//# sourceMappingURL=App.vue.js.map