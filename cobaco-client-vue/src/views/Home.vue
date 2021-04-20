<template>
  <section>
    <div class="container">
      <div class="breadcrumb">
        <ol>
          <li>
            <a href="/file/home">home</a>
          </li>
          <li>
            <a href="/file/home/folderId" text="祖先ディレクトリ"></a>
          </li>
          <li>
            <span>カレントディレクトリ</span>
          </li>
        </ol>
      </div>
    </div>
  </section>
</template>

<style>
/* パンくずリスト */
.breadcrumb {
    min-height: 80px;
    margin-bottom: 20px;
}
.breadcrumb ol {
    list-style-type: none;
    margin: 0;
    padding: 0;
}
.breadcrumb li {
    font-size: 1.4rem;
    display: inline;
}
.breadcrumb li::after {
    content: "/";
    color: #999;
    margin: 0 7px;
}
.breadcrumb li:last-child::after {
    content: none;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({});
</script>
