<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
			<v-form>
				<v-card-title>
					<span class="headline">
						Add user to External Course
					</span>
				</v-card-title>

				<v-card-text class="text-left">
					<b>Course:</b> {{ course.name }} {{ course.academicTerm }}
					<v-text-field
						v-model="user.name"
						label="Name"
						data-cy="userNameInput"
						required
					/>
					<v-text-field
						v-model="user.email"
						label="Email"
						data-cy="userEmailInput"
						required
					/>
					<v-select
						v-model="user.role"
						:items="roles"
						required
						data-cy="userRoleSelect"
						label="Role"
					></v-select>
				</v-card-text>

				<v-card-actions>
					<v-spacer />
					<v-btn
						color="blue darken-1"
						@click="$emit('close-dialog')"
						data-cy="cancelButton"
						>Cancel</v-btn
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
  user !: ExternalUser;

  created() {
		this.user = new ExternalUser();
	}

  async addUser() {
	 const user = await RemoteServices.createExternalUser(this.course.courseExecutionId as number, this.user);
  }
}
</script>
