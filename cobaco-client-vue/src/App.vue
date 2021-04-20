<template>
  <div id="app">
    <Header />
    <router-view />
  </div>
</template>

<style>
html, body {
    font-size: 16px;
    font-family: sans-serif;
    color: #393939;
}
body, div, p, h1, h2, h3, h4, ul, figure {
    margin: 0;
    padding: 0;
}
p, td, th, li {
    line-height: 1.8;
}
img {
    width: 100%;
    height: auto;
}
a {
    text-decoration: none;
    color: #1864b9;
}
a:hover {
    color: #1864b9;
}
.img-responsive {
    display: block;
    max-width: 100%;
    height: auto;
}

.float-clear {
    overflow: hidden;
}

.container {
    margin: 0 auto;
    max-width: 1000px;
}
</style>

<script lang="ts">
import 'normalize.css'
import { defineComponent } from 'vue'
import HeaderComponent from '@/components/Header.vue'

export default defineComponent({
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  setup() {
    
  },
  components: {
    Header: HeaderComponent,
  }
})
</script>
