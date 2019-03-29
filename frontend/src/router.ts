import Vue from "vue";
import Router from "vue-router";
import Quiz from "./views/QuizView.vue";

Vue.use(Router);

export default new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "quiz",
      component: Quiz
    },
    {
      path: "/quiz",
      name: "quiz2",
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () =>
        import(/* webpackChunkName: "about" */ "./views/QuizView.vue")
    }
  ]
});
