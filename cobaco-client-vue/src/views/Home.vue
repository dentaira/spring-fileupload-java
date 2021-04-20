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
  <section>
    <div class="container">
      <main class="main">
        <div class="action-toolbar float-clear">
          <form
            name="uploadForm"
            action="/file/upload/"
            enctype="multipart/form-data"
            method="POST"
          >
            <button
              class="upload-button"
              onclick="clickFile('uploadFile'); return false"
            >
              ファイルをアップロード
            </button>
            <input
              type="file"
              id="uploadFile"
              onchange="submitUpload()"
              class="upload-button"
            />
          </form>
          <form
            name="createDirForm"
            action="/file/create/directory/"
            method="POST"
          >
            <button
              onclick="clickFile('createDirName'); return false"
              class="create-button"
            >
              フォルダを作成
            </button>
            <input
              type="text"
              style="display: none"
              onclick="submitCreateDir()"
              id="createDirName"
              name="createDirName"
            />
          </form>
        </div>
        <div>
          <table class="file-table">
            <thead>
              <tr>
                <th scope="col">ファイル名</th>
                <th scope="col">サイズ</th>
                <th scope="col">削除</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>
                  <div class="float-clear">
                    <div class="file-icon" file-type="FILE"></div>
                    <a href="/file/download/" text="ファイル名" />
                  </div>
                </td>
                <td>
                  <span class="file-size-text">138KB</span>
                </td>
                <td>
                  <a
                    name="delete"
                    href="JavaScript:void(0)"
                    data-id="storedFile.id">削除</a>
                </td>
              </tr>
            </tbody>
          </table>
          <form name="deleteForm" action="/file/delete/" method="POST" />
        </div>
      </main>
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

/* アクションツールバー */
.action-toolbar button {
  margin-right: 20px;
  min-height: 40px;
  background-size: 20px 20px;
  background-position-x: 10px;
  background-position-y: center;
  padding-left: 34px;
  padding-right: 12px;
  border: none;
  outline: none;
  color: #fff;
}
#uploadFile {
  display: none;
}
.upload-button {
  float: left;
  background: url(../assets/upload.png) no-repeat mediumaquamarine;
}
.upload-button:hover,
.upload-button:focus,
.upload-button:active {
  background-color: #82cfad;
}
.create-button {
  background: url(../assets/plus.png) no-repeat gold;
}
.create-button:hover,
.create-button:focus,
.create-button:active {
  background-color: #ffe14c;
}

/** ファイル一覧 */
.file-table {
  border-collapse: collapse;
  width: 100%;
}
.file-table th {
  text-align: left;
}
.file-table th,
.file-table td {
  border-bottom: 1px lightgray solid;
  height: 50px;
}
.file-icon {
  float: left;
  margin: auto 15px;
}
.file-icon[file-type="FILE"] {
  content: url(../assets/file.png);
  height: 32px;
  width: 32px;
}
.file-icon[file-type="DIRECTORY"] {
  content: url(../assets/folder.png);
  height: 32px;
  width: 32px;
}
.file-size-text {
  font-size: 0.9rem;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({});
</script>
