<template>
  <div>
    <p>Error</p>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component
export default class HomeView extends Vue {
  /*error!: String | String[];
  error_description!: String | String[];
  code!: String | String[];

  mounted() {
    if (this.$route.query.error) {
        console.log(this.$route.query.error_description)
    } else {
        this.$route.query.code
        axios.get("https://fenix.tecnico.ulisboa.pt/oauth/access_token?client_id=<client_id>&client_secret=<client_secret>&redirect_uri=<redirect_uri>&code=<code>&grant_type=authorization_code").then(response => {
            // handle success
            this.id = response.data["id"];
            for (let i = 0; i < response.data["questions"].length; i++) {
                let a1 = new Question(response.data["questions"][i]);
                this.questions.push(a1);
            }
            return this.questions;
        });
    }

  }*/
}
</script>

<style></style>
