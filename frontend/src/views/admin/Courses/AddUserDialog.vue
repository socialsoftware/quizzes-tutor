<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-card-title>
          <span class="headline">Add user to External Course</span>
        </v-card-title>

        <v-card-text class="text-left">
          <b>Course:</b>
          {{ course.name }} {{ course.academicTerm }}
          <v-text-field
            v-model="user.name"
            label="Name"
            data-cy="userNameInput"
            :rules="[(value) => !!value || 'Name is required']"
            required
          />
          <v-text-field
            v-model="user.username"
            label="Username"
            data-cy="userUsernameInput"
            :rules="[(value) => !!value || 'Username is required']"
            required
          />
          <v-text-field
            v-model="user.email"
            label="Email"
            data-cy="userEmailInput"
            :rules="[
              (value) => !!value || 'E-mail is required',
              (value) => validateEmail(value) || 'E-mail must be valid',
            ]"
            required
          />
          <v-select
            v-model="user.role"
            :items="roles"
            required
            data-cy="userRoleSelect"
            label="Role"
          ></v-select>
          <div class="add-user-feedback-container">
            <span class="add-user-feedback" v-if="success"
              >{{ user.role }} {{ user.name }} added</span
            >
          </div>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
            @click="$emit('close-dialog')"
            data-cy="cancelButton"
            >Close</v-btn
          >
          <v-btn color="blue darken-1" @click="addUser" data-cy="saveButton"
            >Add</v-btn
          >
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import ExternalUser from '../../../models/user/ExternalUser';

@Component
export default class AddUserDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Course, required: true }) readonly course!: Course;

  roles = ['TEACHER', 'STUDENT'];
  user: ExternalUser = new ExternalUser();
  valid = true;
  success = false;

  created() {
    this.user = new ExternalUser();
  }

  validateEmail(email: string) {
    return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(email);
  }

  async addUser() {
    let user: ExternalUser;
    this.success = false;

    if (!(this.$refs.form as Vue & { validate: () => boolean }).validate())
      return;

    try {
      user = await RemoteServices.registerExternalUser(
        this.course.courseExecutionId as number,
        this.user
      );
      this.$emit('user-created', user);
      this.success = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped>
.add-user-feedback-container {
  height: 25px;
}
.add-user-feedback {
  font-size: 1.05rem;
  color: #1b5e20;
  text-transform: uppercase;
}
</style>
