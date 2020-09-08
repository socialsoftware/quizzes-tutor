import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
let LoginView = class LoginView extends Vue {
    async created() {
        await this.$store.dispatch('loading');
        if (this.$route.query.error) {
            await this.$store.dispatch('error', 'Fenix authentication error');
            await this.$router.push({ name: 'home' });
        }
        else {
            try {
                await this.$store.dispatch('fenixLogin', this.$route.query.code);
                await this.$router.push({ name: 'courses' });
            }
            catch (error) {
                await this.$store.dispatch('error', error);
                await this.$router.push({ name: 'home' });
            }
        }
        await this.$store.dispatch('clearLoading');
    }
};
LoginView = __decorate([
    Component
], LoginView);
export default LoginView;
//# sourceMappingURL=LoginView.vue.js.map