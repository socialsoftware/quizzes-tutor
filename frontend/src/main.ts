import "./class-component-hooks";
import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import DatetimePicker from "vuetify-datetime-picker";
import "material-design-icons-iconfont/dist/material-design-icons.css";
import vuetify from "@/plugins/vuetify";

Vue.config.productionTip = false;
Vue.use(DatetimePicker);

new Vue({
  vuetify,
  router,
  store,
  render: h => h(App)
}).$mount("#app");
