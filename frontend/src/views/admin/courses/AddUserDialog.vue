<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('close-dialog')"
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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import ExternalUser from '../../../models/user/ExternalUser';

const props = defineProps<{
  dialog: boolean;
  course: Course;
}>();

const emit = defineEmits(['close-dialog', 'user-created', 'update:dialog']);

const store = useStore();
const form = ref<any>(null);

const roles = ['TEACHER', 'STUDENT'];
const user = ref<ExternalUser>(new ExternalUser());
const valid = ref(true);
const success = ref(false);

onMounted(() => {
  user.value = new ExternalUser();
});

const validateEmail = (email: string) => {
  return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(email);
};

const addUser = async () => {
  let createdUser: ExternalUser;
  success.value = false;

  if (!form.value?.validate()) return;

  try {
    createdUser = await RemoteServices.registerExternalUser(
      props.course.courseExecutionId as number,
      user.value
    );
    emit('user-created', createdUser);
    success.value = true;
  } catch (error) {
    store.setError(error as string);
  }
};
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
