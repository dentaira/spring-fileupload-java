<template>
  <div>
    <h1>サインイン</h1>
    <a href="#" @click="signIn">サインインする</a>
  </div>
</template>

<script lang="ts">
import { profileMockData, profileStore } from "@/store/profile";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  setup() {
    const router = useRouter();

    const signIn = () => {
      profileStore.profile = profileMockData;
      router.push("/home");
    };

    return {
      signIn,
    };
  },
});
</script>
