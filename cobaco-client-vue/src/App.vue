<template>
  <div id="app">
    <Header />
    <router-view />
  </div>
</template>

<style lang="scss"></style>

<script lang="ts">
import { defineComponent } from 'vue'
import HeaderComponent from '@/components/Header.vue'

export default defineComponent({
  setup() {
    
  },
  components: {
    Header: HeaderComponent,
  }
})
</script>
