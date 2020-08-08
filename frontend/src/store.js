import Vue from 'vue';
import Vuex from 'vuex';
import RemoteServices from '@/services/RemoteServices';
const state = {
    token: '',
    user: null,
    currentCourse: null,
    error: false,
    errorMessage: '',
    loading: false
};
Vue.use(Vuex);
Vue.config.devtools = true;
export default new Vuex.Store({
    state: state,
    mutations: {
        login(state, authResponse) {
            state.token = authResponse.token;
            state.user = authResponse.user;
        },
        logout(state) {
            state.token = '';
            state.user = null;
            state.currentCourse = null;
        },
        error(state, errorMessage) {
            state.error = true;
            state.errorMessage = errorMessage;
        },
        clearError(state) {
            state.error = false;
            state.errorMessage = '';
        },
        loading(state) {
            state.loading = true;
        },
        clearLoading(state) {
            state.loading = false;
        },
        currentCourse(state, currentCourse) {
            state.currentCourse = currentCourse;
        }
    },
    actions: {
        error({ commit }, errorMessage) {
            commit('error', errorMessage);
        },
        clearError({ commit }) {
            commit('clearError');
        },
        loading({ commit }) {
            commit('loading');
        },
        clearLoading({ commit }) {
            commit('clearLoading');
        },
        async fenixLogin({ commit }, code) {
            const authResponse = await RemoteServices.fenixLogin(code);
            commit('login', authResponse);
            // localStorage.setItem("token", authResponse.token);
            // localStorage.setItem("userRole", authResponse.user.role);
        },
        async demoStudentLogin({ commit }) {
            const authResponse = await RemoteServices.demoStudentLogin();
            commit('login', authResponse);
            commit('currentCourse', Object.values(authResponse.user.courses)[0][0]);
            // localStorage.setItem("token", authResponse.token);
            // localStorage.setItem("userRole", authResponse.user.role);
        },
        async demoTeacherLogin({ commit }) {
            const authResponse = await RemoteServices.demoTeacherLogin();
            commit('login', authResponse);
            commit('currentCourse', Object.values(authResponse.user.courses)[0][0]);
            // localStorage.setItem("token", authResponse.token);
            // localStorage.setItem("userRole", authResponse.user.role);
        },
        async demoAdminLogin({ commit }) {
            const authResponse = await RemoteServices.demoAdminLogin();
            commit('login', authResponse);
            // localStorage.setItem("token", authResponse.token);
            // localStorage.setItem("userRole", authResponse.user.role);
        },
        logout({ commit }) {
            return new Promise(resolve => {
                commit('logout');
                // localStorage.removeItem("token");
                // localStorage.removeItem("userRole");
                resolve();
            });
        },
        currentCourse({ commit }, currentCourse) {
            commit('currentCourse', currentCourse);
        }
    },
    getters: {
        isLoggedIn(state) {
            return !!state.token;
        },
        isAdmin(state) {
            return (!!state.token &&
                state.user !== null &&
                (state.user.admin || state.user.role == 'DEMO_ADMIN'));
        },
        isTeacher(state) {
            return (!!state.token && state.user !== null && state.user.role == 'TEACHER');
        },
        isStudent(state) {
            return (!!state.token && state.user !== null && state.user.role == 'STUDENT');
        },
        getToken(state) {
            return state.token;
        },
        getUser(state) {
            return state.user;
        },
        getCurrentCourse(state) {
            return state.currentCourse;
        },
        getError(state) {
            return state.error;
        },
        getErrorMessage(state) {
            return state.errorMessage;
        },
        getLoading(state) {
            return state.loading;
        }
    }
});
//# sourceMappingURL=store.js.map