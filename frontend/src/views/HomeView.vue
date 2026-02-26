<template>
  <div class="container">
    <h1 id="home-title" class="display-2 font-weight-thin mb-3">
      {{ appName }}
    </h1>

    <div class="horizontal-btn-container" v-if="!isLoggedIn">
      <v-btn :href="fenixUrl" variant="flat" color="primary">
        Log in with Fenix <i class="fas fa-sign-in-alt ml-3" style="font-size: 24px;" />
      </v-btn>

      <v-btn href="./login/external" variant="flat" color="primary">
        External User Login <i class="fas fa-sign-in-alt ml-3" style="font-size: 24px;" />
      </v-btn>
    </div>

    <div class="horizontal-btn-container" v-if="!isLoggedIn">
      <v-btn
        variant="flat"
        size="small"
        color="primary"
        @click="demoStudent(false)"
        data-cy="demoStudentLoginButton"
      >
        <i class="fa fa-graduation-cap mr-2" /> Demo as student
      </v-btn>
      <v-btn
        variant="flat"
        size="small"
        color="primary"
        @click="demoStudent(true)"
        data-cy="demoNewStudentLoginButton"
      >
        <i class="fa fa-graduation-cap mr-2" /> Demo as new student
      </v-btn>
      <v-btn
        variant="flat"
        size="small"
        color="primary"
        @click="demoTeacher"
        data-cy="demoTeacherLoginButton"
      >
        <i class="fa fa-graduation-cap mr-2" /> Demo as teacher
      </v-btn>
      <v-btn
        variant="flat"
        size="small"
        color="primary"
        @click="demoAdmin"
        data-cy="demoAdminLoginButton"
      >
        <i class="fa fa-user-cog mr-2" /> Demo as administrator
      </v-btn>
    </div>

    <v-footer class="footer">
      <img
        :src="istLogo"
        class="logo"
        alt="Técnico Logo"
      />
      <div>
        <v-btn
          variant="flat"
          size="small"
          color="secondary"
          href="https://github.com/socialsoftware/quizzes-tutor"
          target="_blank"
        >
          <i class="fab fa-github mr-2" /> View code
        </v-btn>
      </div>
      <div>
        <v-btn
          variant="flat"
          size="small"
          color="secondary"
          href="https://quizzes-tecnico.slack.com/"
          target="_blank"
        >
          <i class="fab fa-slack mr-2" /> Discussion Group
        </v-btn>
      </div>

      <div>
        <v-btn
          variant="flat"
          size="small"
          color="secondary"
          href="https://github.com/socialsoftware/quizzes-tutor/issues"
          target="_blank"
        >
          <i class="fab fa-github mr-2" /> Bug report
        </v-btn>
      </div>
      <img
        :src="impressLogo"
        class="logo"
        alt="IMPRESS Logo"
      />
    </v-footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useStore } from '@/store';
import istLogo from '@/assets/img/ist_optimized.png';
import impressLogo from '@/assets/img/impress_optimized.png';

const store = useStore();

const appName: string = import.meta.env.VUE_APP_NAME || 'ENV FILE MISSING';
const fenixUrl: string = import.meta.env.VUE_APP_FENIX_URL || '';

const isLoggedIn = computed(() => !!store.token);

const demoStudent = async (createNew: boolean) => {
  store.setLoading();
  try {
    if (createNew) {
      await store.demoNewStudentLogin();
    } else {
      await store.demoStudentLogin();
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const demoTeacher = async () => {
  store.setLoading();
  try {
    await store.demoTeacherLogin();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const demoAdmin = async () => {
  store.setLoading();
  try {
    await store.demoAdminLogin();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>

<style lang="scss" scoped>
.container {
  height: 100%;
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  justify-content: center;
  align-items: center;

  #home-title {
    box-sizing: border-box;
    color: rgb(255, 255, 255);
    min-height: auto;
    min-width: auto;
    text-align: center;
    text-decoration: none solid rgb(255, 255, 255);
    text-rendering: optimizelegibility;
    text-size-adjust: 100%;
    column-rule-color: rgb(255, 255, 255);
    perspective-origin: 229.922px 34px;
    transform-origin: 229.922px 34px;
    caret-color: rgb(255, 255, 255);
    background: rgba(0, 0, 0, 0.75) none no-repeat scroll 0 0 / auto padding-box border-box;
    border: 0 none rgb(255, 255, 255);
    font-family: Roboto, sans-serif !important;
    font-weight: 100 !important;
    font-size: 45px !important;
    line-height: 48px !important;
    margin-bottom: 70px !important;
    outline: rgb(255, 255, 255) none 0;
    padding: 10px 20px;
  }

  .horizontal-btn-container {
    margin-top: 40px;
    padding-bottom: 30px;

    button,
    a {
      margin: 0 10px;
    }
  }

  .footer {
    background-color: rgba(0, 0, 0, 0) !important;
    display: flex; /* or inline-flex */
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    max-height: 100px;
    position: absolute;
    bottom: 0;
    overflow: hidden;

    .logo {
      flex-shrink: 1;
      width: 20%;
      max-width: 200px;
      min-width: 100px;
      padding: 2%;
    }
  }
}
</style>
