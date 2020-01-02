import "./class-component-hooks";
import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vuetify from "@/vuetify";

import DatetimePicker from "vuetify-datetime-picker";
Vue.use(DatetimePicker);

// Markdown Editor
import VueSimplemde from "vue-simplemde";
import "simplemde/dist/simplemde.min.css";
Vue.component("vue-simplemde", VueSimplemde);

Vue.config.productionTip = false;

new Vue({
  vuetify,
  router,
  store,
  render: h => h(App)
}).$mount("#app");
