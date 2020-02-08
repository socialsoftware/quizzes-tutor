import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import vuetify from '@/vuetify';
import DatetimePicker from 'vuetify-datetime-picker';
import VueSimplemde from 'vue-simplemde'; // Markdown Editor
import 'simplemde/dist/simplemde.min.css'; // Markdown Editor

Vue.use(DatetimePicker);
Vue.component('vue-simplemde', VueSimplemde);

Vue.config.productionTip = false;

new Vue({
  vuetify,
  router,
  store,
  render: h => h(App)
}).$mount('#app');
