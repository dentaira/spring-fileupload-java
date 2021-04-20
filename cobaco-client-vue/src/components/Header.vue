<template>
  <header>
    <div class="container">
      <div class="header-inner">
        <h1 class="header-logo">
          <a href="#"><img src="@/assets/logo.png" alt="Cobaco"></a>
        </h1>
        <a href="/logout">ログアウト</a>
      </div>
    </div>
  </header>
</template>

<style>
.header-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.header-logo {
    margin: 0;
    padding: 10px 0;
    width: 180px;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  setup() {},
});
</script>
